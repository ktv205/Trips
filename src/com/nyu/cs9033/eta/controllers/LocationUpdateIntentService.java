package com.nyu.cs9033.eta.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.nyu.cs9033.eta.models.RequestPackage;

/**
 * Created by krishnateja on 11/15/2014.
 */
public class LocationUpdateIntentService extends Service implements GooglePlayServicesClient.OnConnectionFailedListener,
GooglePlayServicesClient.ConnectionCallbacks, LocationListener{
	public static volatile boolean shouldContinue = true;

	LocationClient mClient;
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;
	LocationRequest mLocationRequest;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		mClient = new LocationClient(this, this, this);
		while (mClient.isConnecting()) {
			try {
				Thread.sleep(1, 1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mClient.connect();
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		return START_STICKY;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("onLocationChanged","here");
		if(shouldContinue){
		new UpdateLocationAsyncTask().execute(location);
		}else{
			
		}
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		mClient.requestLocationUpdates(mLocationRequest,this);
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	class UpdateLocationAsyncTask extends AsyncTask<Location, Integer, String>{

		@Override
		protected String doInBackground(Location... params) {
			
			return connectToInternet(params[0].getLatitude(),params[0].getLongitude());
		} 
		public String connectToInternet(double lat,double lng){
			RequestPackage requestPackage = new RequestPackage();
			requestPackage.setMethod("POST");
			requestPackage.setURI("http://cs9033-homework.appspot.com/");
			JSONObject obj = new JSONObject();
			try {
				obj.put("command", "UPDATE_LOCATION");
				obj.put("latitude", lat);
				obj.put("longitude", lng);
				obj.put("datetime", new Timestamp(new Date().getTime()));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.d(obj.toString(),obj.toString());

			URL url = null;
			try {
				url = new URL(requestPackage.getURI());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpURLConnection con = null;
			try {
				con = (HttpURLConnection) url.openConnection();
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
			OutputStreamWriter writer = null;
			try {
				writer = new OutputStreamWriter(con.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer.write(obj.toString());
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
				reader = new InputStreamReader(con.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader bufferedReader = new BufferedReader(reader);
			StringBuilder builder = new StringBuilder();
			String line = null;
			try {
				line = bufferedReader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (line != null) {
				builder.append(line);
				try {
					line = bufferedReader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			JSONObject reponseObj = null;
			try {
				reponseObj = new JSONObject(builder.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return reponseObj.toString();
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d("onPostExecute",result);
		}	
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		mClient.disconnect();
	}
		
}
