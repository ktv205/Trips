package com.nyu.cs9033.eta.controllers;
import com.nyu.cs9033.eta.*;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("NewApi") public class MyDialogFragment extends DialogFragment implements OnItemClickListener {
	ListView list;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,new String[]{"call friend","view friend details","message friend"});
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);


	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		return super.onCreateDialog(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_friend_contact_options, null);
		getDialog().setTitle("options");
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		list=(ListView)view.findViewById(R.id.listViewOptions);
		return view;
	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Toast.makeText(getActivity(), "clicked", Toast.LENGTH_LONG).show();
		String number=getArguments().getString("Phone");
		if(arg2==0){
			Log.i("phone in fragment", number);
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:"+number));
			startActivity(intent); 
			dismiss();
		}else if(arg2==1){
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(getArguments().getInt("contactID")));
			intent.setData(uri);
			startActivity(intent);
			dismiss();
		}else if(arg2==2){
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
			sendIntent.setData(Uri.parse("sms:"+number));
			startActivity(sendIntent);
			dismiss();
		}
	}
	@Override
	public boolean isCancelable() {
		return true;
	}
	@Override
	public void dismiss() {

		super.dismiss();
	}

}
