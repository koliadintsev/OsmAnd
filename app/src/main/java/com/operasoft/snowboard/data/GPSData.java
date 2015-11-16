package com.operasoft.snowboard.data;

public class GPSData {
	private boolean ggaDataCollected;

	public boolean isGgaDataCollected() {
		return ggaDataCollected;
	}

	public void setGgaDataCollected(boolean ggaDataCollected) {
		this.ggaDataCollected = ggaDataCollected;
	}

	public boolean isVtgdataCollected() {
		return vtgdataCollected;
	}

	public void setVtgdataCollected(boolean vtgdataCollected) {
		this.vtgdataCollected = vtgdataCollected;
	}

	private boolean vtgdataCollected;
	private String latitude;
	private String longitude;
	private String altitude;
	private String gpsfix;
	private String heading;
	private String speed;
	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	private String date;

	public String getGpsfix() {
		return gpsfix;
	}

	public void setGpsfix(String gpsfix) {
		this.gpsfix = gpsfix;
	}

	public String getAltitude() {
		return altitude;
	}

	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
		if (speed != null && !"".equalsIgnoreCase(speed)) {
			try {

				Double knot = Double.parseDouble(speed);
				this.speed = 1.85200 * knot + "";
			} catch (Exception e) {
				this.speed = speed;
			}
		}
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

}
