package com.marakana.yamba;

import com.maracana.yamba.twitter.YambaApp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

// 23. 25:44
public class TimelineActivity extends ListActivity {
	static final String[] FROM = { StatusData.C_USER, StatusData.C_TEXT,
			StatusData.C_CREATED_AT };
	static final int[] TO = { R.id.text_user, R.id.text_text,
			R.id.text_created_at };

	Cursor cursor;
	SimpleCursorAdapter adapter;

	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() != R.id.text_created_at)
				return false;
			// else if (cursor.getColumnIndex(StatusData.C_CREATED_AT) !=
			// columnIndex)
			// return false;

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

		cursor = ((YambaApp) getApplication()).statusData.query();
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);

		setTitle(R.string.timeline);
		setListAdapter(adapter);

		/*
		 * while (cursor.moveToNext()) { String user =
		 * cursor.getString(cursor.getColumnIndex(StatusData.C_USER)); String
		 * text = cursor.getString(cursor.getColumnIndex(StatusData.C_TEXT));
		 * 
		 * list.append( String.format("%s: %s\n", user, text) ); }
		 */
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
		Intent timeline = new Intent(this, TimelineActivity.class);

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
		case R.id.item_timeline:
			startActivity(timeline);
			return true;
		default:
			return false;
		}

	}

}
