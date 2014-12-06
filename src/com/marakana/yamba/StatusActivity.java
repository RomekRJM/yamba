package com.marakana.yamba;

import com.maracana.yamba.twitter.YambaApp;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener, LocationListener {

	private EditText editStatus;
	private Button buttonUpdate;
	private Context context;
	private LocationManager locationManager;
	private Location location;
	
	private static final String PROVIDER = LocationManager.GPS_PROVIDER;
	private static final String TAG = "StatusActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Debug.startMethodTracing("Yamba.trace");

		setContentView(R.layout.status);

		editStatus = (EditText) findViewById(R.id.editStatus);
		buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
		context = this;

		buttonUpdate.setOnClickListener(this);
		
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		location = locationManager.getLastKnownLocation(PROVIDER);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
		Log.d(TAG, "On location changed: " + location.toString());
	}
	
	@Override
	public void onProviderDisabled(String arg0) {
	}
	
	@Override
	public void onProviderEnabled(String arg0) {
	}
	
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(PROVIDER, 30000, 1000, this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Debug.stopMethodTracing();
	}

	@Override
	public void onClick(View view) {
		String status = editStatus.getText().toString();

		new PostTweet().execute(status);
	}

	class PostTweet extends AsyncTask<String, Void, String> {

		/* New Thread */
		@Override
		protected String doInBackground(String... params) {
			
			try {
				YambaApp.updateStatus(params[0]);
				return getResources().getString(
						R.string.msgStatusUpdatedSuccessfully);
			} catch (Exception e) {
				return getResources().getString(R.string.msgStatusUpdateFailed);
			}
		}

		/* UI Thread */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(context, result, Toast.LENGTH_SHORT);
		}

	}


}
