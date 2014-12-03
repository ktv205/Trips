package com.nyu.cs9033.eta.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyTripDataBaseHelper extends SQLiteOpenHelper {
	private static final int VERSION=6;
	private static final String DATABASE_NAME="trip.db";
		private static final String COMMA_SEP=",";
		private static final String TEXT_TYPE = " TEXT";
		private static final String TRIP_QUERY="create table "+ 
				TripContract.TripEntry.TABLE_NAME
				+" ("+TripContract.TripEntry._ID+" INTEGER PRIMARY KEY"+COMMA_SEP
				+TripContract.TripEntry.COLUMN_NAME_REMOTE_ID+" INTEGER"+COMMA_SEP
				+TripContract.TripEntry.COLUMN_NAME_TRIP_NAME+TEXT_TYPE+COMMA_SEP
				+TripContract.TripEntry.COLUMN_NAME_LOCATION+TEXT_TYPE+COMMA_SEP
				+TripContract.TripEntry.COLUMN_NAME_DATE+TEXT_TYPE+COMMA_SEP
				+TripContract.TripEntry.COLUMN_NAME_TIME+TEXT_TYPE+COMMA_SEP
				+TripContract.TripEntry.COLUMN_NAME_TIMESTAMP+TEXT_TYPE+COMMA_SEP
				+TripContract.TripEntry.COLUMN_NAME_LATITUDE+TEXT_TYPE+COMMA_SEP
				+TripContract.TripEntry.COLUMN_NAME_LONGITUDE+TEXT_TYPE+")";
		private static final String PERSON_QUERY="create table "+ 
				TripContract.PersonEntry.TABLE_NAME
				+" ("+TripContract.PersonEntry._ID+" INTEGER PRIMARY KEY"+COMMA_SEP
				+TripContract.PersonEntry.COLUMN_NAME_PERSON_NAME+TEXT_TYPE+COMMA_SEP
				+TripContract.PersonEntry.COLUMN_NAME_PHONE+TEXT_TYPE+COMMA_SEP
				+TripContract.PersonEntry.COLUMN_NAME_LOCATION+TEXT_TYPE+COMMA_SEP
				+TripContract.PersonEntry.COLUMN_NAME_TIMESTAMP+TEXT_TYPE+COMMA_SEP
				+ TripContract.PersonEntry.COLUMN_NAME_LATITUDE+TEXT_TYPE+COMMA_SEP
				+TripContract.PersonEntry.COLUMN_NAME_LONGITUDE+TEXT_TYPE+COMMA_SEP
				+TripContract.PersonEntry.COLUMN_NAME_CONTACT_ID+" INTEGER"+")";
		private static final String PERSON_TRIP_QUERY="create table "+ 
				TripContract.PersonTripEntry.TABLE_NAME
				+" ("+TripContract.PersonTripEntry._ID+" INTEGER PRIMARY KEY"+COMMA_SEP
				+TripContract.PersonTripEntry.COLUMN_NAME_PERSON_ID+" INTEGER"+COMMA_SEP
				+TripContract.PersonTripEntry.COLUMN_NAME_TRIP_ID+" INTEGER"
				+")";
	Context context;
	public MyTripDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		//Toast.makeText(context,"in the helper constructor", Toast.LENGTH_LONG).show();
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Toast.makeText(context,"in the onCreate", Toast.LENGTH_LONG).show();
				db.execSQL(TRIP_QUERY);
				db.execSQL(PERSON_QUERY);
				db.execSQL(PERSON_TRIP_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Toast.makeText(context,"in the onupgrade", Toast.LENGTH_LONG).show();
		 db.execSQL("DROP TABLE IF EXISTS " + TripContract.TripEntry.TABLE_NAME);
		 db.execSQL("DROP TABLE IF EXISTS " + TripContract.PersonEntry.TABLE_NAME);
		 db.execSQL("DROP TABLE IF EXISTS " + TripContract.PersonTripEntry.TABLE_NAME);
		onCreate(db);
	}

}
