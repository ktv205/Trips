package com.nyu.cs9033.eta.models;

import android.provider.BaseColumns;

public class TripContract {

	public TripContract() {
		super();
		// TODO Auto-generated constructor stub
	}
   public static  abstract class TripEntry implements BaseColumns {
	   public static final String TABLE_NAME = "trip";
		public static final String COLUMN_NAME_TRIP_NAME = "trip_name";
		public static final String COLUMN_NAME_LOCATION = "trip_location";
		public static final String COLUMN_NAME_TIMESTAMP = "trip_timestamp";
		public static final String COLUMN_NAME_REMOTE_ID="remote_id";
		public static final String COLUMN_NAME_DATE="trip_date";
		public static final String COLUMN_NAME_TIME="trip_time";
		public static final String COLUMN_NAME_LATITUDE="latitude";
		public static final String COLUMN_NAME_LONGITUDE="longitude";
   }
   public static  abstract class PersonEntry implements BaseColumns {
	   public static final String TABLE_NAME = "person";
		public static final String COLUMN_NAME_PERSON_NAME = "person_name";
		public static final String COLUMN_NAME_LOCATION = "person_location";
		public static final String COLUMN_NAME_TIMESTAMP = "person_timestamp";
		public static final String COLUMN_NAME_PHONE="person_phone";
		public static final String COLUMN_NAME_LATITUDE="latitude";
		public static final String COLUMN_NAME_LONGITUDE="longitude";
		public static final String COLUMN_NAME_CONTACT_ID="contactid";
   }
   public static  abstract class PersonTripEntry implements BaseColumns {
	   public static final String TABLE_NAME = "Person_Trip";
		public static final String COLUMN_NAME_PERSON_ID = "person_id";
		public static final String COLUMN_NAME_TRIP_ID = "trip_id";
   }
}
