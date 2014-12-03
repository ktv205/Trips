package com.nyu.cs9033.eta.controllers;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nyu.cs9033.eta.models.MyTripDataBaseHelper;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.TripContract;
import com.nyu.cs9033.eta.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateTripActivity extends Activity {
	MyTripDataBaseHelper tripHelper;
	SQLiteDatabase tripDatabase;
	Button addFriends, createEvent;
	EditText tripName, tripLocation, friendName, friendPhone, friendLocation;
	DatePicker tripDate;
	TimePicker tripTime;
	Trip trip;
	Person person;
	int flag=0;
	String tripTitleText, tripLocationText, tripDataText, tripTimeText,
			friendNameText, friendContactText, friendLocationText,
			tripLatitudeText, tripLongitudeText;
	int titleFlag = 0, nameFlag = 0, tripLocationFlag = 0, contactID;
	List<Long> personIds = new ArrayList<Long>();
	long personId, tripId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.create_trip_main);
		if (servicesConnected()) {
			Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();
		}
		setTitle("CreateTripWithFriends");
		addFriends = (Button) findViewById(R.id.add_friends_button);
		createEvent = (Button) findViewById(R.id.create_trip_button);
		tripDate = (DatePicker) findViewById(R.id.date_trip_datepicker);
		tripTime = (TimePicker) findViewById(R.id.time_trip_datepicker);
		friendName = (EditText) findViewById(R.id.add_friends_name_edittext);
		friendPhone = (EditText) findViewById(R.id.add_friends_phone_edittext);
		friendLocation = (EditText) findViewById(R.id.add_friends_location_edittext);
		tripName = (EditText) findViewById(R.id.name_trip_edittext);
		tripLocation = (EditText) findViewById(R.id.location_trip_edittext);
		File database = getApplicationContext().getDatabasePath("trip.db");
		if (!database.exists()) {
		} else {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		tripHelper = new MyTripDataBaseHelper(this);
		tripDatabase = tripHelper.getWritableDatabase();
		// isTableExists(tripDatabase);
		tripLocation.setHint("eg:area,city::cuisine.");
		TextWatcher text = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String value = s.toString();
				if (checkString(value)) {
					value = value.substring(0, value.length() - 1);
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					String uri = "location://com.example.nyu.hw3api";
					intent.putExtra("searchVal", value);
					intent.setData(Uri.parse(uri));
					startActivityForResult(intent, 1);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		tripLocation.addTextChangedListener(text);
		friendName.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true && friendName.getText().toString() != "") {
					Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
							ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(pickContactIntent, 2);
				}
			}
		});
		createEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tripName.getText().toString().equals("")) {
					titleFlag = 0;
				} else {
					titleFlag = 1;
				}
				if (tripLocation.getText().toString().equals("")) {
					tripLocationFlag = 0;
				} else {
					tripLocationFlag = 1;
				}
				if (titleFlag == 0) {
					tripName.requestFocus();
				} else if (tripLocationFlag == 0) {
					tripLocation.requestFocus();
				} else if (checkFriendField() == false) {
					friendFocusRequest();
				} else {
					if (friendName.getText().toString().equals("")) {
					} else {
						getFriendsFields();
					}
					tripTitleText = tripName.getText().toString();
					tripDataText = tripDate.getMonth() + 1 + "/"
							+ tripDate.getDayOfMonth() + "/"
							+ tripDate.getYear();
					tripTimeText = tripTime.getCurrentHour() + ":"
							+ tripTime.getCurrentMinute();
					tripName.setText("");
					tripLocation.setText("");
					addTripToDataBase();
				}
			}
		});
		addFriends.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (friendName.getText().toString().equals("")) {
					// Toast.makeText(CreateTripActivity.this, "add a friend",
					// Toast.LENGTH_SHORT).show();
					friendFocusRequest();
				} else {
					getFriendsFields();
				}
			}

		});
	}

	private void getFriendsFields() {
		nameFlag = 1;
		friendLocationText = friendLocation.getText().toString();
		friendName.setText("");
		friendLocation.setText("");
		friendPhone.setText("");
		addFriendToDataBase();
	}

	private void friendFocusRequest() {
		friendName.requestFocus();
		// Toast.makeText(this,
		// "please select a friend from address book",Toast.LENGTH_SHORT).show();

	}

	public boolean checkFriendField() {
		if (friendName.getText().toString().equals("")) {
			if (nameFlag == 0) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public void onBackPressed() {
		if(flag==1)
		new CreateTripAsyncTask(this).execute(tripId);
		finish();
	}

	public void addFriendToDataBase() {
		Cursor friendExists = tripDatabase.rawQuery("SELECT * FROM "
				+ TripContract.PersonEntry.TABLE_NAME + " WHERE "
				+ TripContract.PersonEntry.COLUMN_NAME_PERSON_NAME + "=?",
				new String[] { friendNameText });
		if (friendExists.getCount() == 0) {
			ContentValues values = new ContentValues();
			java.util.Date date = new java.util.Date();
			Timestamp myTimeStamp = new Timestamp(date.getTime());
			String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
					Locale.US).format(myTimeStamp);
			values.put(TripContract.PersonEntry.COLUMN_NAME_PERSON_NAME,
					friendNameText);
			values.put(TripContract.PersonEntry.COLUMN_NAME_PHONE,
					friendContactText);
			values.put(TripContract.PersonEntry.COLUMN_NAME_LOCATION,
					friendLocationText);
			values.put(TripContract.PersonEntry.COLUMN_NAME_TIMESTAMP,
					timeStamp);
			values.put(TripContract.PersonEntry.COLUMN_NAME_CONTACT_ID,
					contactID);
			personId = tripDatabase.insert(TripContract.PersonEntry.TABLE_NAME,
					null, values);
			personIds.add(personId);
		} else {
			friendExists.moveToFirst();
			personId = friendExists.getInt(friendExists
					.getColumnIndex(TripContract.PersonEntry._ID));
			personIds.add(personId);
		}

	}

	public void addTripToDataBase() {
		Cursor tripExists = tripDatabase.rawQuery("SELECT * FROM "
				+ TripContract.TripEntry.TABLE_NAME + " WHERE "
				+ TripContract.TripEntry.COLUMN_NAME_TRIP_NAME + "=?",
				new String[] { tripTitleText });
		if (tripExists.getCount() == 0) {
			ContentValues values = new ContentValues();
			java.util.Date date = new java.util.Date();
			Timestamp myTimeStamp = new Timestamp(date.getTime());
			String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
					Locale.US).format(myTimeStamp);
			values.put(TripContract.TripEntry.COLUMN_NAME_TRIP_NAME,
					tripTitleText);
			values.put(TripContract.TripEntry.COLUMN_NAME_LOCATION,
					tripLocationText);
			values.put(TripContract.TripEntry.COLUMN_NAME_DATE, tripDataText);
			values.put(TripContract.TripEntry.COLUMN_NAME_TIME, tripTimeText);
			values.put(TripContract.TripEntry.COLUMN_NAME_LONGITUDE,
					tripLongitudeText);
			values.put(TripContract.TripEntry.COLUMN_NAME_LATITUDE,
					tripLatitudeText);
			values.put(TripContract.TripEntry.COLUMN_NAME_TIMESTAMP, timeStamp);
			tripId = tripDatabase.insert(TripContract.TripEntry.TABLE_NAME,
					null, values);
		} else {
			tripExists.moveToFirst();
			tripId = tripExists.getInt(tripExists
					.getColumnIndex(TripContract.TripEntry._ID));
		}
		Log.i("tripId->", tripId + "-<");
		Log.i("personIds size->", personIds.size() + "-<");
		for (int i = 0; i < personIds.size(); i++) {
			tripDatabase.execSQL("INSERT INTO "
					+ TripContract.PersonTripEntry.TABLE_NAME + "("
					+ TripContract.PersonTripEntry.COLUMN_NAME_PERSON_ID + ","
					+ TripContract.PersonTripEntry.COLUMN_NAME_TRIP_ID
					+ ")VALUES(" + personIds.get(i) + "," + tripId + ")");
		}
		Cursor tripPerson = tripDatabase.rawQuery("SELECT * FROM "
				+ TripContract.PersonTripEntry.TABLE_NAME + " where "
				+ TripContract.PersonTripEntry.COLUMN_NAME_TRIP_ID + "="
				+ tripId, null);
		Log.i("count of friends", tripPerson.getCount() + "-<");
		flag=1;
		onBackPressed();
	}

	private boolean checkString(String value) {
		String line = value;
		String pattern = "[0-9\\sa-z&nbsp;A-Z,]+" + "::"
				+ "[0-9\\sa-z&nbsp;A-Z]+\\.";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(line);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("hello", "in onstatacrtiveyresult");
		if (data != null) {
			if (requestCode == 1) {
				Log.i("hello", "in onstatacrtiveyresult");
				// Toast.makeText(this,
				// data.getStringArrayListExtra("retVal").get(0),Toast.LENGTH_SHORT).show();
				tripLocationText = data.getStringArrayListExtra("retVal")
						.get(0)
						+ ","
						+ data.getStringArrayListExtra("retVal").get(1);
				tripLatitudeText = data.getStringArrayListExtra("retVal")
						.get(2);
				tripLongitudeText = data.getStringArrayListExtra("retVal").get(
						3);
				tripLocation.setText(tripLocationText);
				if (resultCode == RESULT_OK) {
				}
			}
			if (requestCode == 2) {
				Uri uri = data.getData();
				Cursor cursor = getContentResolver().query(uri, null, null,
						null, null);
				cursor.moveToFirst();
				friendNameText = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				friendName.setText(friendNameText);
				int id = cursor.getInt(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				contactID = id;
				int isNumber = cursor
						.getInt(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (isNumber == 1) {
					Cursor phoneCursor = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?",
							new String[] { String.valueOf(id) }, null);
					phoneCursor.moveToFirst();
					friendContactText = phoneCursor
							.getString(phoneCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					friendPhone.setText(friendContactText);
				} else {
					friendContactText = "No Number";
					friendPhone.setText(friendContactText);
				}
			}
		}
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason.
			// resultCode holds the error code.
		} else {
			return false;
		}
	}
}
