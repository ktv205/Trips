package com.nyu.cs9033.eta.controllers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.nyu.cs9033.eta.*;
import com.nyu.cs9033.eta.models.MyTripDataBaseHelper;
import com.nyu.cs9033.eta.models.TripContract;
public class TripsList extends Activity {
	ListView tripList;
	MyTripDataBaseHelper tripHelper;
	SQLiteDatabase tripDatabase;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trips_list);
		setTitle("Trip History");
	}
	@SuppressLint("NewApi") @Override
	protected void onResume() {
		super.onResume();
		tripList=(ListView)findViewById(R.id.tripList);
		tripHelper=new MyTripDataBaseHelper(this);
		tripDatabase=tripHelper.getWritableDatabase();
		Cursor tripCursor = tripDatabase.rawQuery("SELECT * FROM "+TripContract.TripEntry.TABLE_NAME,null);
		tripCursor.moveToFirst();
		Log.i("database count starts from 1->",tripCursor.getInt(0)+"");
		SimpleCursorAdapter cursor=new SimpleCursorAdapter(this,R.layout.view_trip_list_contents,tripCursor,
				                                    new String[]{TripContract.TripEntry.COLUMN_NAME_TRIP_NAME,
				                                                  TripContract.TripEntry.COLUMN_NAME_LOCATION,
				                                                  TripContract.TripEntry.COLUMN_NAME_DATE}, 
                                                                   new int[]{R.id.trip_title_list,R.id.trip_location_list_text,
				                                                   R.id.trip_when_list_text},0);
		tripList.setAdapter(cursor);
		tripList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.i("clicked event->",arg2+"");
				Intent intent=new Intent(TripsList.this,ViewATrip.class);
				intent.putExtra("tripIdViewATrip", arg2+1);
				startActivity(intent);
			}
		});
		tripList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				ActiveDialogFragment fragment=new ActiveDialogFragment();
				Bundle bundle=new Bundle();
				bundle.putInt("ID", position);
				fragment.setArguments(bundle);
				fragment.show(getFragmentManager(), null);
				return true;
			}
		});
	}

}
