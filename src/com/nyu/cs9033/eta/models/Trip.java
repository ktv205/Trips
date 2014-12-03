package com.nyu.cs9033.eta.models;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Trip implements Parcelable {

	// Member fields should exist here, what else do you need for a trip?
	// Please add additional fields
	private String name;
	private String date;
	private String time;
	private String location; 
	private double lat;
	private double lng;
	private long timeLong;
	private ArrayList<Person> friends=new ArrayList<Person>();
	public Trip(Parcel p) {

		readFromParcel(p);
	}
	public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
		public Trip createFromParcel(Parcel p) {
			return new Trip(p);
		}

		public Trip[] newArray(int size) {
			return new Trip[size];
		}
	};
	@SuppressWarnings("unchecked")
	public void readFromParcel(Parcel p) {
		name=p.readString();
		location=p.readString();
		date=p.readString();
		time=p.readString();
		friends=p.readArrayList(Person.class.getClassLoader());

	}

	


		public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public String getDate() {
		return date;
	}




	public void setDate(String date) {
		this.date = date;
	}




	public String getTime() {
		return time;
	}




	public void setTime(String time) {
		this.time = time;
	}




	public String getLocation() {
		return location;
	}




	public void setLocation(String location) {
		this.location = location;
	}




	public double getLat() {
		return lat;
	}




	public void setLat(double lat) {
		this.lat = lat;
	}




	public double getLng() {
		return lng;
	}




	public void setLng(double lng) {
		this.lng = lng;
	}




	public long getTimeLong() {
		return timeLong;
	}




	public void setTimeLong(long timeLong) {
		this.timeLong = timeLong;
	}




	public ArrayList<Person> getFriends() {
		return friends;
	}




	public void setFriends(ArrayList<Person> friends) {
		this.friends = friends;
	}




		@Override
	public String toString() {
		return "Trip [name=" + name + ", date=" + date + ", time=" + time
				+ ", location=" + location + ", friends=" + friends.get(0).toString() + "]";
	}
    public String title(){
    	return name;
    }
    public String when(){
    	return date+","+time;
    }
    public String location(){
    	return location;
    }
    public ArrayList<Person> friends(){
    	return friends;
    }




		public Trip(String name, String location, String date, String time,double lat,double lng, 
				ArrayList<Person> friends) {
			super();
			this.name = name;
			this.location = location;
			this.date = date;
			this.time = time;
			this.lat=lat;
			this.lng=lng;
			this.friends = friends;
		}
	public Trip(String name, String location, String date, String time) {
		super();
		this.name = name;
		this.location = location;
		this.date = date;
		this.time = time;
	}
	
	public Trip(String name, String location,Long time,double lat,double lng, ArrayList<Person> friends) {
		super();
		this.name = name;
		this.location = location;
		this.timeLong=time;
		this.lat=lat;
		this.lng=lng;
		this.friends=friends;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(location);
		dest.writeString(date);
		dest.writeString(time);
		dest.writeList(friends);
		dest.writeDouble(lat);
		dest.writeDouble(lng);
		dest.writeLong(timeLong);

	}


}
