package com.operasoft.snowboard.maplayers;

public class TIT_RoutePoint {
	private double latitude;
	private double longitude;

	public TIT_RoutePoint() {
	}

	public TIT_RoutePoint(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}