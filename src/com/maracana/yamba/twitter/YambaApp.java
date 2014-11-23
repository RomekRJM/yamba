package com.maracana.yamba.twitter;

import com.marakana.yamba.StatusData;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class YambaApp extends Application implements OnSharedPreferenceChangeListener {
	private static final String TAG = "TwitterAccessor";
	public static final String ACTION_NEW_STATUS = "com.example.yamba.status";
	
	private static Twitter twitter;
	public static SharedPreferences prefs;
	public StatusData statusData;

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		statusData = new StatusData(this);
		getTwitter();
		Log.d(TAG, "onCreate");
	}

	private static synchronized Twitter getTwitter() {
		if (twitter == null) {
			
			String consumerKey = prefs.getString("consumerKey", "");//"MHlv3clcV0kbn1LjOv3jjXTdw";
			String consumerSecret = prefs.getString("consumerSecret", "");//"9G9DDgyEpH280dDXp53hhmTX7tGnL1L7RZlA2CFcrI8cqtgI1b";
			String accessToken = prefs.getString("accessToken", "");//"2636584038-PajsA94p9LsWvoyA5UbLpfBBJNfIRFA3JrfhfAO";
			String accessSecret = prefs.getString("accessSecret", "");//"pxKVRurRJfdyeknronWSOWNbpy1G3qM5GZYEPCq6DCjuI";

			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
					.setOAuthConsumerSecret(consumerSecret)
					.setOAuthAccessToken(accessToken)
					.setOAuthAccessTokenSecret(accessSecret);

			TwitterFactory factory = new TwitterFactory(cb.build());
			twitter = factory.getInstance();
		}

		return twitter;
	}
	
	public static ResponseList<Status> getUserTimeline() {

		try {
			return getTwitter().getUserTimeline();
		} catch (TwitterException e) {
			Log.e(TAG, e.getErrorMessage());
		}

		return null;
	}
	
	public static void updateStatus(String text) {

		try {
			getTwitter().updateStatus(text);
		} catch (TwitterException e) {
			Log.e(TAG, e.getErrorMessage());
		}

	}
	
	long lastTimeSeen = -1l;
	
	public ResponseList<Status> pullAndInsert() {
		ResponseList<Status> list = YambaApp.getUserTimeline();
		int count = 0;
		long biggestTimestamp = -1;

		try {
			if (list != null) {
				for (Status status : list) {
					statusData.insert(status);
					
					if(status.getCreatedAt().getTime() > lastTimeSeen) {
						count++;
						biggestTimestamp = (status.getCreatedAt().getTime() > biggestTimestamp) ? 
								status.getCreatedAt().getTime() : biggestTimestamp;
						Log.d(TAG, String.format("%s, %s", status.getUser()
								.getName(), status.getText()));
					}
					
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Failes to access twitter service", e);
		}
		
		if(count > 0) {
			sendBroadcast(new Intent(ACTION_NEW_STATUS).putExtra("count", count));
		}
		
		lastTimeSeen = biggestTimestamp;
		
		return list;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
			twitter = null;
			Log.d(TAG, "sharedPreferences has changed for key: " + key);
	}
}
