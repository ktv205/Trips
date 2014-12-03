package com.nyu.cs9033.eta.controllers;

import com.nyu.cs9033.eta.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi")
public class NoActiveTripsFragment extends Fragment {
  @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	  return inflater.inflate(R.layout.no_active_trips_fragment, container, false);
}

}
