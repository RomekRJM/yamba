package com.marakana.yamba;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StatusData {

	static final String TAG = "StatusData";
	
	public static final String DB_NAME = "timeline.db";
	public static final int DB_VERSION = 1;
	public static final String TABLE = "status";
	
	public static final String C_ID = "_id";
	public static final String C_CREATED_AT = "created_at";
	public static final String C_USER = "user_name";
	public static final String C_TEXT = "status_text";
	
	Context context;
	DbHelper dbHelper;
	SQLiteDatabase db;
	
	public StatusData(Context context) {
		this.context = context;
		dbHelper = new DbHelper();
	}
	
	public void insert(Status status) {		
		ContentValues values = new ContentValues();
		values.put(C_ID, status.getId());
		values.put(C_CREATED_AT, status.getCreatedAt().getTime());
		values.put(C_USER, status.getUser().getName());
		values.put(C_TEXT, status.getText());
		
		db = dbHelper.getWritableDatabase();
		db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
	}
	
	public Cursor query() {
		List<Status> list = new ArrayList<Status>();
		
		return dbHelper.getReadableDatabase()
				.query(TABLE, null, null, null, null, null, C_CREATED_AT + " DESC"); // SELECT * FROM status
	}
	
	class DbHelper extends SQLiteOpenHelper {

		public DbHelper() {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/**
		 * run once, after install
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = String.format("CREATE TABLE %s " + 
					"(%s int PRIMARY KEY, %s int, %s text, %s text)", 
					TABLE, C_ID, C_CREATED_AT, C_USER, C_TEXT);
			Log.d(TAG, "onCreate with SQL: " + sql);
			db.execSQL(sql);
		}

		/**
		 * migration scripts, invoked on schema change
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// usually alter table statement here
			Log.d(TAG, "onUpgrade from: " + oldVersion + " to: " + newVersion);
			String sql = String.format("DROP IF EXISTS %s", TABLE);
			Log.d(TAG, "onUpgrade with SQL: " + sql);
			db.execSQL(sql);
			onCreate(db);
		}
		
	}
}
