package com.operasoft.snowboard.engine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.VehicleLastReport;
import com.operasoft.snowboard.dbsync.CommonUtils;

public class VehicleLocation {

	static private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static final private long MINUTES_5 = 5 * 60 * 1000;
	static final private long MINUTES_30 = 30 * 60 * 1000;
	static final private long MINUTES_60 = 60 * 60 * 1000;
	static final private long DAY = 24 * MINUTES_60;
	
	public enum Status {
		IN_MOVEMENT,
		STOPPED_LESS_THAN_30,
		STOPPED_LESS_THAN_60,
		STOPPED_LESS_THAN_DAY,
		STOPPED_MORE_THAN_DAY
	}
	
	private Vehicle vehicle;
	private VehicleLastReport lastReport;
	private Status status;
	private double latitude;
	private double longitude;
	
	public VehicleLocation(Vehicle vehicle) {
		super();
		this.vehicle = vehicle;
		this.status = Status.IN_MOVEMENT;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}
	
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	public String getName() {
		return vehicle.getName();
	}
	
	public VehicleLastReport getLastReport() {
		return lastReport;
	}
	
	public void setLastReport(VehicleLastReport lastReport) {
		this.lastReport = lastReport;
		if (lastReport.getLocation() != null) {
			String point = lastReport.getLocation().replace("POINT(", "").replace(")", "");
			int index = point.indexOf(' ');
			if (index > 0) {
				try {
					latitude = Double.valueOf(point.substring(0, index).trim());
					longitude = Double.valueOf(point.substring(index).trim());
				} catch (NumberFormatException e) {
					Log.e("VehicleInfo", "Invalid location found for vehicle " + lastReport.getId() + ": " + lastReport.getLocation());
				}
			}
		}
		updateStatus(CommonUtils.Now());
	}
	
	public void updateStatus(Date now) {
		try {
			Date lastUpdate = dateFormat.parse(lastReport.getModified());
			long delta = now.getTime() - lastUpdate.getTime();
			
			if (delta < MINUTES_30) {
				status = Status.IN_MOVEMENT;
			} else if (delta < MINUTES_30) {
				status = Status.STOPPED_LESS_THAN_30;
			} else if (delta < MINUTES_60) {
				status = Status.STOPPED_LESS_THAN_60;
			} else if (delta < DAY) {
				status = Status.STOPPED_LESS_THAN_DAY;
			}else if (delta < MINUTES_5) {
				status = Status.IN_MOVEMENT;
			} else {
				status = Status.STOPPED_MORE_THAN_DAY;
			}
		} catch (ParseException e) {
			Log.e("VehicleInfo", "Failed to parse date " + lastReport.getModified() + " for vehicle " + lastReport.getId());
		}
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getId() {
		return vehicle.getId();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof VehicleLocation) {
			VehicleLocation vl = (VehicleLocation) o;
			return getId().equals(vl.getId());
		}
		return super.equals(o);
	}
	
}
