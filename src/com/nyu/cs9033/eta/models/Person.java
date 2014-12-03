package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
	private String friendName;
	private String friendPhone;
	private String friendLocation;
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(friendName);
		dest.writeString(friendPhone);
		dest.writeString(friendLocation);

	}
	public Person(String friendName, String friendLocation, String friendPhone) {
		super();
		this.friendName = friendName;
		this.friendLocation = friendLocation;
		this.friendPhone = friendPhone;
	}
	public Person(String friendName,String friendPhone) {
		super();
		this.friendName = friendName;
		this.friendPhone = friendPhone;
	}
	
	public String getFriendName() {
		return friendName;
	}
	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}
	public String getFriendPhone() {
		return friendPhone;
	}
	public void setFriendPhone(String friendPhone) {
		this.friendPhone = friendPhone;
	}
	public String getFriendLocation() {
		return friendLocation;
	}
	public void setFriendLocation(String friendLocation) {
		this.friendLocation = friendLocation;
	}
	public Person(Parcel p) {
		readFromParcel(p);
	}
	public void readFromParcel(Parcel p){
		this.friendName=p.readString();
		this.friendPhone=p.readString();
		this.friendLocation=p.readString();
	}
	@Override
	public String toString() {
		return "Person [friendName=" + friendName + ", friendPhone="
				+ friendPhone + ", friendLocation=" + friendLocation + "]";
	}
	public String name(){
		return friendName;
	}
	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
		public Person createFromParcel(Parcel p) {

			return new Person(p);
		}

		public Person[] newArray(int size) {
			return new Person[size];
		}
	};

}
