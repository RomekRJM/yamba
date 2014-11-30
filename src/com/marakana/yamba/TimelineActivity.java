package com.marakana.yamba;

import com.maracana.yamba.twitter.YambaApp;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineActivity extends ListActivity {
	static final String TAG = "TimelineActivity";
	static final String[] FROM = { StatusData.C_USER, StatusData.C_TEXT,
			StatusData.C_CREATED_AT };
	static final int[] TO = { R.id.text_user, R.id.text_text,
			R.id.text_created_at };

	Cursor cursor;
	SimpleCursorAdapter adapter;
	TimelineReceiver receiver;

	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() != R.id.text_created_at)
				return false;

			long time = cursor.getLong(cursor
					.getColumnIndex(StatusData.C_CREATED_AT));
			CharSequence relativeTime = DateUtils
					.getRelativeTimeSpanString(time);
			((TextView) view).setText(relativeTime);

			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);

		setTitle(R.string.timeline);
		setListAdapter(adapter);
	}

	// Menu Stuff
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intentUpdater = new Intent(this, UpdaterService.class);
		Intent intentRefresher = new Intent(this, RefreshService.class);
		Intent prefs = new Intent(this, PrefsActivity.class);
		Intent status = new Intent(this, StatusActivity.class);

		switch (item.getItemId()) {
		case R.id.item_start_service:
			startService(intentUpdater);
			return true;
		case R.id.item_stop_service:
			stopService(intentUpdater);
			return true;
		case R.id.item_refresh_service:
			startService(intentRefresher);
			return true;
		case R.id.item_prefs:
			startActivity(prefs);
			return true;
		case R.id.item_status_update:
			startActivity(status);
			return true;
		case R.id.item_fake_boot:
			sendBroadcast(new Intent(YambaApp.ACTION_FAKE_BOOT));
			return true;
		default:
			return false;
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(receiver == null) {
			receiver = new TimelineReceiver();
			registerReceiver(receiver, new IntentFilter(YambaApp.ACTION_NEW_STATUS));
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(receiver != null) {
			unregisterReceiver(receiver);
		}
	}


	public class TimelineReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			cursor = ((YambaApp) getApplication()).statusData.query();
			adapter.changeCursor(cursor);
			Log.d(TAG, "TimelineReceiver.onReceive changeCursor() with count=" + intent.getIntExtra("count", 1));
		}
		
	}

}
