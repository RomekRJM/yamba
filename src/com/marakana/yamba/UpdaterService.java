package com.marakana.yamba;

import com.maracana.yamba.twitter.YambaApp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {

	static final String TAG = "UpdaterService";
	private boolean update;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStarted");
		
		update = true;

		new Thread() {
			public void run() {
				while (update) {
					((YambaApp)getApplication()).pullAndInsert();
					try {
						Thread.sleep(getDelay());
					} catch (InterruptedException e) {
						Log.e(TAG, "Wait failed", e);
					}
				}
			}
		}.start();

		return super.onStartCommand(intent, flags, startId);
	}
	
	private long getDelay() {
		String delay = ((YambaApp)getApplication()).prefs.getString("delay", "30");
		long delayLong = Long.parseLong(delay) * 1000;
		return delayLong;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		update = false;
		super.onDestroy();
	}

}
