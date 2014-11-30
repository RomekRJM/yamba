package com.marakana.yamba;

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
		((YambaApp)getApplication()).pullAndInsert();
	}
}
