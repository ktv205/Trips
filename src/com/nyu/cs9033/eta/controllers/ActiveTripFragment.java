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
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.models.MyTripDataBaseHelper;
import com.nyu.cs9033.eta.models.RequestPackage;
import com.nyu.cs9033.eta.models.TripContract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ActiveTripFragment extends Fragment {
	Button button;
	TextView textView;
	OnClickReached reached;
	View view;
	List<Integer> contactIDs = new ArrayList<Integer>();
	  public interface OnClickReached {
	        public void onClickReadchedButton(boolean reached);
	    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater
				.inflate(R.layout.activity_view_a_trip, container, false);
		textView=new TextView(getActivity());
		textView.setText("test");
		LinearLayout layout=(LinearLayout) view.findViewById(R.id.linearViewATrip);
		layout.addView(textView);
		button=new Button(getActivity());
		button.setText("reached destination");
		layout.addView(button);
		
		return view;
	}
	 @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        
	        // This makes sure that the container activity has implemented
	        // the callback interface. If not, it throws an exception
	        try {
	        	Log.d("connected","in onAttach");
	            reached = (OnClickReached) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement OnHeadlineSelectedListener");
	        }
	    }
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("clicked","reached destination");
				LocationUpdateIntentService.shouldContinue=false;
				Intent intent=new Intent(getActivity(),LocationUpdateIntentService.class);
				getActivity().stopService(intent);
				reached.onClickReadchedButton(true);
				
			}
		});
		Bundle bundle = getArguments();
		int id = bundle.getInt("ACTIVEFRAGMENTID");
		MyTripDataBaseHelper tripHelper;
		SQLiteDatabase tripDatabase;
		ListView listView;
		TextView title, location, when;

		listView = (ListView) view.findViewById(R.id.viewATripList);
		title = (TextView) view.findViewById(R.id.trip_title_single_trip);
		location = (TextView) view
				.findViewById(R.id.trip_location_single_trip_text);
		when = (TextView) view.findViewById(R.id.trip_when_single_trip_text);
		tripHelper = new MyTripDataBaseHelper(getActivity());
		tripDatabase = tripHelper.getWritableDatabase();
		Cursor tripCursor = tripDatabase.rawQuery("SELECT * FROM "
				+ TripContract.TripEntry.TABLE_NAME + " WHERE "
				+ TripContract.TripEntry._ID + "=" + id, null);
		tripCursor.moveToFirst();
		title.setText(tripCursor.getString(tripCursor
				.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_TRIP_NAME)));
		location.setText(tripCursor.getString(tripCursor
				.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_LOCATION)));
		when.setText(tripCursor.getString(tripCursor
				.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_DATE))
				+ ","
				+ tripCursor.getString(tripCursor
						.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_TIME)));
		Cursor friendCursor = tripDatabase.rawQuery("SELECT * FROM "
				+ TripContract.PersonEntry.TABLE_NAME + " WHERE "
				+ TripContract.PersonEntry._ID + " IN (" + "SELECT "
				+ TripContract.PersonTripEntry.COLUMN_NAME_PERSON_ID + " FROM "
				+ TripContract.PersonTripEntry.TABLE_NAME + " WHERE "
				+ TripContract.PersonTripEntry.COLUMN_NAME_TRIP_ID + "=" + id
				+ ")", null);
		if (friendCursor.getCount() > 0) {
			SimpleCursorAdapter friendAdapter = new SimpleCursorAdapter(
					getActivity(),
					R.layout.view_friends_list_contents,
					friendCursor,
					new String[] {
							TripContract.PersonEntry.COLUMN_NAME_PERSON_NAME,
							TripContract.PersonEntry.COLUMN_NAME_LOCATION,
							TripContract.PersonEntry.COLUMN_NAME_PHONE },
					new int[] { R.id.friend_name_view,
							R.id.friend_location_view, R.id.friend_phone_view },
					0);
			friendCursor.moveToFirst();
			while (!friendCursor.isAfterLast()) {
				contactIDs
						.add(friendCursor.getInt(friendCursor
								.getColumnIndex(TripContract.PersonEntry.COLUMN_NAME_CONTACT_ID)));
				friendCursor.moveToNext();
			}
			listView.setAdapter(friendAdapter);
			new FriendsDistanceAsyncTask(getActivity()).execute(Long.valueOf(id));
		}

	}

	class FriendsDistanceAsyncTask extends
			AsyncTask<Long, Integer, JSONObject> {
		Context context;

		public FriendsDistanceAsyncTask(Context context) {
			this.context = context;
		}

		@Override
		protected JSONObject doInBackground(Long... params) {
			Log.d("long local id",String.valueOf(params[0]));
			Cursor cursor = new MyTripDataBaseHelper(context)
					.getWritableDatabase()
					.rawQuery(
							"SELECT "
									+ TripContract.TripEntry.COLUMN_NAME_REMOTE_ID
									+ " FROM "
									+ TripContract.TripEntry.TABLE_NAME
									+ " WHERE " + TripContract.TripEntry._ID
									+ "=?",
							new String[] { String.valueOf(params[0]) });
			cursor.moveToFirst();
			long remoteId = cursor
					.getLong(cursor
							.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_REMOTE_ID));
			Log.d("long long",String.valueOf(remoteId));
			String toSend = retriveJson(remoteId);
			JSONObject obj = connectToGoogleEngine(toSend);
			return obj;
		}

		public String retriveJson(Long remoteId) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("command", "TRIP_STATUS");
				obj.put("trip_id", remoteId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return obj.toString();

		}

		private JSONObject connectToGoogleEngine(String jsonData) {
			URL url = null;
			RequestPackage requestPackage = new RequestPackage();
			requestPackage.setMethod("POST");
			requestPackage.setURI("http://cs9033-homework.appspot.com/");
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
			return reponseObj;

		}
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			Toast.makeText(context,result.toString(), Toast.LENGTH_LONG).show();
			textView.setText(result.toString());
		}
	}
	

}
