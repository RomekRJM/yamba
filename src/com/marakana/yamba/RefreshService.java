package com.marakana.yamba;

import twitter4j.ResponseList;
import twitter4j.Status;

import com.maracana.yamba.twitter.YambaApp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RefreshService extends IntentService {

	private static final String TAG = "RefreshService";

	public RefreshService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ResponseList<Status> list = YambaApp.getUserTimeline();
		StatusData statusData = ((YambaApp)getApplication()).statusData;

		try {
			if (list != null) {
				for (Status status : list) {
					statusData.insert(status);
					Log.d(TAG, String.format("%s, %s", status.getUser()
							.getName(), status.getText()));
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Failes to access twitter service", e);
		}
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

}
