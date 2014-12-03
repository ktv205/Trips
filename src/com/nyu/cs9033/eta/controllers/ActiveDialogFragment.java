package com.nyu.cs9033.eta.controllers;
import com.nyu.cs9033.eta.R;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressLint("NewApi")
public class ActiveDialogFragment extends DialogFragment {
	Button ok,cancel;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	      View view=inflater.inflate(R.layout.fragment_acitive_dialog, null);
	      ok=(Button) view.findViewById(R.id.buttonok);
	      cancel=(Button) view.findViewById(R.id.buttoncancel);
	      return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getActivity();
		Bundle bundle=getArguments();
		final int id=bundle.getInt("ID");
		SharedPreferences pref=getActivity().getSharedPreferences("ACTIVE",Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor=pref.edit();
		super.onActivityCreated(savedInstanceState);
		getDialog().setTitle("Do you want to start this trip");
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getActivity(),MainActivity.class);
				editor.putInt("SHAREDID", id+1);
				editor.commit();
				intent.putExtra("ACTIVETRIPID", id+1);
				getActivity().startActivity(intent);
				dismiss();
				
				
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				
			}
		});
	}

}
