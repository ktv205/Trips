package com.nyu.cs9033.eta.controllers;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.nyu.cs9033.eta.*;
import com.nyu.cs9033.eta.models.MyTripDataBaseHelper;
import com.nyu.cs9033.eta.models.TripContract;
public class ViewATrip extends Activity {
	MyTripDataBaseHelper tripHelper;
	SQLiteDatabase tripDatabase;
	ListView listView;
	TextView title,location,when;
	FragmentManager manager;
	FragmentTransaction transaction;
	MyDialogFragment fragment;
	List<Integer> contactIDs=new ArrayList<Integer>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_a_trip);
		listView=(ListView)findViewById(R.id.viewATripList);
		title=(TextView)findViewById(R.id.trip_title_single_trip);
		location=(TextView)findViewById(R.id.trip_location_single_trip_text);
		when=(TextView)findViewById(R.id.trip_when_single_trip_text);
		setTitle("A Trip");
	}
	@SuppressLint("NewApi") @Override
	protected void onResume() {
		super.onResume();
		tripHelper=new MyTripDataBaseHelper(this);
		tripDatabase=tripHelper.getWritableDatabase();
		Cursor tripCursor = tripDatabase.rawQuery("SELECT * FROM "+TripContract.TripEntry.TABLE_NAME+" WHERE "+
				TripContract.TripEntry._ID+"="+getIntent().getExtras().getInt("tripIdViewATrip"),null);
		tripCursor.moveToFirst();
		title.setText(tripCursor.getString(tripCursor.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_TRIP_NAME)));
		location.setText(tripCursor.getString(tripCursor.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_LOCATION)));
		when.setText(tripCursor.getString(tripCursor.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_DATE))
				+","+tripCursor.getString(tripCursor.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_TIME)));
		Cursor friendCursor = tripDatabase.rawQuery("SELECT * FROM "+TripContract.PersonEntry.TABLE_NAME
				+" WHERE "+TripContract.PersonEntry._ID+" IN ("
				+ "SELECT "+TripContract.PersonTripEntry.COLUMN_NAME_PERSON_ID
				+" FROM "+TripContract.PersonTripEntry.TABLE_NAME
				+" WHERE "+TripContract.PersonTripEntry.COLUMN_NAME_TRIP_ID+"="+getIntent().getExtras().getInt("tripIdViewATrip")+")"
				,null);
		if(friendCursor.getCount()>0){
			SimpleCursorAdapter friendAdapter=new SimpleCursorAdapter(this, R.layout.view_friends_list_contents,friendCursor,
					new String[]{
					TripContract.PersonEntry.COLUMN_NAME_PERSON_NAME
					,TripContract.PersonEntry.COLUMN_NAME_LOCATION,
					TripContract.PersonEntry.COLUMN_NAME_PHONE
			}, new int[]{
					R.id.friend_name_view
					,R.id.friend_location_view
					,R.id.friend_phone_view
			}, 0);
			friendCursor.moveToFirst();
			while(!friendCursor.isAfterLast()){
				contactIDs.add(friendCursor.getInt(friendCursor.getColumnIndex(TripContract.PersonEntry.COLUMN_NAME_CONTACT_ID)));
				friendCursor.moveToNext();
			}
			listView.setAdapter(friendAdapter);

			listView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Log.i("in On Long Click", "cliked");
					manager=getFragmentManager();
					fragment=new MyDialogFragment();
					fragment.show(manager, "hello");
					Bundle bundle=new Bundle();
					TextView number=(TextView)arg1.findViewById(R.id.friend_phone_view);
					bundle.putString("Phone", number.getText().toString());
					bundle.putInt("contactID", contactIDs.get(arg2));
					Log.i("Phone",number.getText().toString());
					fragment.setArguments(bundle);
					return false;
				}
			});
		}

	}
}
