package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.R;
import com.nyu.cs9033.eta.controllers.ActiveTripFragment.OnClickReached;
import com.nyu.cs9033.eta.models.MyTripDataBaseHelper;
import com.nyu.cs9033.eta.models.TripContract;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

public class MainActivity extends Activity implements OnClickReached {
	Button createTrip;
	Button viewTrip;
	private final static String TAG="MainActivity";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    createTrip = (Button) findViewById(R.id.create_trip);
		viewTrip = (Button) findViewById(R.id.view_trip);
		createTrip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startCreateTripActivity();

			}
		});
		viewTrip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startViewTripActivity();

			}
		});
	}
    @SuppressLint("NewApi")
	@Override
    protected void onResume() {
    	super.onResume();
    	SharedPreferences shared = getSharedPreferences("ACTIVE", MODE_PRIVATE);
		int id = shared.getInt("SHAREDID", -1);

		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (id == -1) {
			NoActiveTripsFragment fragment = new NoActiveTripsFragment();
			transaction.add(R.id.mainlinear, fragment,"not");
			transaction.commit();
		} else {
			if(isMyServiceRunning(LocationUpdateIntentService.class)){
				Log.d(TAG,"running");
			}else{
				Log.d(TAG,"not running");
				startService(new Intent(this,LocationUpdateIntentService.class));
			}
			
			ActiveTripFragment fragment=new ActiveTripFragment();
			Bundle bundle=new Bundle();
			bundle.putInt("ACTIVEFRAGMENTID", id);
			fragment.setArguments(bundle);
		  
			transaction.add(R.id.mainlinear,fragment,"active");
			transaction.commit();
			
		}

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
	public void startCreateTripActivity() {
		Intent intent = new Intent(this, CreateTripActivity.class);
		startActivity(intent);
	}

	public void startViewTripActivity() {
		Intent intent = new Intent(this, TripsList.class);
		startActivity(intent);
	}
	@Override
	protected void onDestroy() {
				super.onDestroy();	
	}

	@SuppressLint("NewApi")
	@Override
	public void onClickReadchedButton(boolean reached) {
		if(reached){
			SharedPreferences shared = getSharedPreferences("ACTIVE", MODE_PRIVATE);
			SharedPreferences.Editor edit=shared.edit();
			edit.putInt("SHAREDID", -1);
			edit.commit();
			FragmentManager manager = getFragmentManager();
			NoActiveTripsFragment fragment=new NoActiveTripsFragment();;
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.mainlinear,fragment);
			transaction.commit();
			
			
		}
		
		
	}
}
