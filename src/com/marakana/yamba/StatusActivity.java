package com.marakana.yamba;

import com.maracana.yamba.twitter.YambaApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener {

	private EditText editStatus;
	private Button buttonUpdate;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Debug.startMethodTracing("Yamba.trace");

		setContentView(R.layout.status);

		editStatus = (EditText) findViewById(R.id.editStatus);
		buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
		context = this;

		buttonUpdate.setOnClickListener(this);
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
