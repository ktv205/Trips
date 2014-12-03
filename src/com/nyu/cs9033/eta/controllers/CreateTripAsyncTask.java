package com.nyu.cs9033.eta.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.nyu.cs9033.eta.models.MyTripDataBaseHelper;
import com.nyu.cs9033.eta.models.Person;
import com.nyu.cs9033.eta.models.RequestPackage;
import com.nyu.cs9033.eta.models.Trip;
import com.nyu.cs9033.eta.models.TripContract;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
public class CreateTripAsyncTask extends AsyncTask<Long, Integer, String> {
	Context context;
	MyTripDataBaseHelper tripHelper;
	SQLiteDatabase tripDatabase;
	Trip trip;
	RequestPackage requestPackage;
	ArrayList<Person> personList=new ArrayList<>();
	
	public CreateTripAsyncTask(Context context) {
		this.context=context;
	}
    
	@Override
	protected String doInBackground(Long... params) {
		String jsonTest = null;
		Cursor getTripDetails=new MyTripDataBaseHelper(context).getWritableDatabase().rawQuery(
				"Select * FROM "+TripContract.TripEntry.TABLE_NAME+" WHERE "+
		     TripContract.TripEntry._ID+"=?",new String[]{params[0].toString()});
		 Cursor getFriendsDetails=new MyTripDataBaseHelper(context).getWritableDatabase()
				                    .rawQuery("SELECT * FROM "+TripContract.PersonEntry.TABLE_NAME
				                    		   +" WHERE "+TripContract.PersonEntry._ID
				                    		   +" IN (SELECT "+TripContract.PersonTripEntry.COLUMN_NAME_PERSON_ID
				                    		    +" FROM "+TripContract.PersonTripEntry.TABLE_NAME+
				                    		    " WHERE "+TripContract.PersonTripEntry.COLUMN_NAME_TRIP_ID
				                    		    +"="+params[0]+")", null);
		 getTripDetails.moveToFirst();
		 while(!getTripDetails.isAfterLast()){
			String title=getTripDetails.getString(
					getTripDetails.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_TRIP_NAME));
			String location=getTripDetails.getString(
					getTripDetails.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_LOCATION));
			double lat=getTripDetails.getDouble(
					getTripDetails.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_LATITUDE));
			double lng=getTripDetails.getDouble(
					getTripDetails.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_LONGITUDE));
			 getFriendsDetails.moveToFirst();
		    while(!getFriendsDetails.isAfterLast()){
		    	String name=getFriendsDetails.getString(
		    			  getFriendsDetails.getColumnIndex(TripContract.PersonEntry.COLUMN_NAME_PERSON_NAME));
		    	String number=getFriendsDetails.getString(
		    			getFriendsDetails.getColumnIndex(TripContract.PersonEntry.COLUMN_NAME_PHONE));
		    	String friendLocation=getFriendsDetails.getString(
		    			getFriendsDetails.getColumnIndex(TripContract.PersonEntry.COLUMN_NAME_LOCATION));
		    	if(friendLocation!=null){
		    		personList.add(new Person(name,number,friendLocation));
		    	}else{
		    		personList.add(new Person(name,number));
		    	}
		    	getFriendsDetails.moveToNext();
		    }
		  
		List<String> stringList=  getTimeAndDateInTimeStamp(getTripDetails.getString(getTripDetails.getColumnIndex(
		    		                    TripContract.TripEntry.COLUMN_NAME_DATE)),getTripDetails.getString(getTripDetails.getColumnIndex(
				    		                    TripContract.TripEntry.COLUMN_NAME_TIME)));
		Calendar calender=new GregorianCalendar(Integer.valueOf(stringList.get(2)),Integer.valueOf(stringList.get(1)),Integer.valueOf(stringList.get(0)),Integer.valueOf(stringList.get(3)),Integer.valueOf(stringList.get(4)));
         trip=new Trip(title, location, calender.getTimeInMillis()/1000,lat, lng, personList);
        
         getTripDetails.moveToNext();
		 }
	    jsonTest=retriveJson();
	    JSONObject obj=null;
	    obj=connectToGoogleEngine(jsonTest);
	    
	    try {
			new MyTripDataBaseHelper(context).getWritableDatabase().execSQL(
					"UPDATE "+TripContract.TripEntry.TABLE_NAME+" "
							+ "SET "+TripContract.TripEntry.COLUMN_NAME_REMOTE_ID
							+"="+obj.getLong("trip_id")+
							" WHERE "+TripContract.TripEntry._ID+"="+params[0]);
			Log.d("long from server long",String.valueOf(obj.getLong("trip_id")));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			return String.valueOf(obj.getLong("trip_id"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	 		 		 		 
	}
	
	private JSONObject connectToGoogleEngine(String jsonData) {
		URL url=null;
		try {
			url=new URL(requestPackage.getURI());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection con=null;
		try {
			con=(HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			con.setRequestMethod(requestPackage.getMethod());
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		con.setDoOutput(true);
		con.setDoInput(true);
		OutputStreamWriter writer=null;
		try {
			writer=new OutputStreamWriter(con.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.write(jsonData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStreamReader reader = null;
		try {
			reader=new InputStreamReader(con.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader bufferedReader=new BufferedReader(reader);
		StringBuilder builder=new StringBuilder();
		String line = null;
		try {
			line=bufferedReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(line!=null){
		   builder.append(line);
		   try {
			line=bufferedReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		JSONObject reponseObj=null;
		try {
			reponseObj=new JSONObject(builder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reponseObj;
		
		
	}

	private List<String> getTimeAndDateInTimeStamp(String string, String string2) {
		List<String> dateTimeList=new ArrayList<String>();
		String date[]=string.split("/");
		String time[]=string2.split(":");
		for(int i=0;i<date.length;i++){
			dateTimeList.add(date[i]);
		}
		for(int i=0;i<time.length;i++){
			dateTimeList.add(time[i]);
		}
		return dateTimeList;
		
	}

	public String retriveJson(){
		requestPackage=new RequestPackage();
		requestPackage.setMethod("POST");
		requestPackage.setURI("http://cs9033-homework.appspot.com/");
		JSONObject obj=new JSONObject();
		try {
			obj.put("command", "CREATE_TRIP");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("location", new String[]{trip.getName(),trip.getLocation(),
					String.valueOf(trip.getLat()),String.valueOf(trip.getLng())});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("datetime",trip.getTimeLong());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> friends=new ArrayList<String>();
		for(int i=0;i<trip.getFriends().size();i++){
			friends.add(trip.getFriends().get(i).getFriendName());
		}
		try {
			obj.put("people", friends);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj.toString();
	}	
	@Override
	protected void onPostExecute(String result) {
		//Toast.makeText(context,result, Toast.LENGTH_LONG).show();
		Log.d("blah blah",result);
	}

}
