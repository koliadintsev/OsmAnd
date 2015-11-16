package com.operasoft.snowboard.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

import com.operasoft.snowboard.dbsync.CommonUtils;

public class ServiceLocation extends Dto implements Polygonable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "contact_id")
	private String contactId;

	@Column(name = "street_number")
	private String streetNumber;

	@Column(name = "street_name")
	private String streetName;

	@Column(name = "address")
	private String address;

	@Column(name = "city_name")
	private String cityName;

	@Column(name = "zip")
	private String zip;

	@Column(name = "comments")
	private String comments;

	@Column(name = "latitude")
	private double latitude;

	@Column(name = "longitude")
	private double longitude;

	@Column(name = "status_code_id")
	private String Status;

	@Column(name = "time_of_last_completed_sa")
	private String timeLastSA;

	@Column(name = "last_visit_date")
	private String lastVisitDate;

	@Column(name = "name")
	private String name;

	@Column(name = "polygon")
	private String polygon;

	@Column(name = "polycentroid")
	private String polyCentroid;

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getAddress() {
		if ((address == null) || address.trim().equals("")) {
			if ((streetNumber != null) && (streetName != null)) {
				address = streetNumber + " " + streetName;
			}
		}
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getStatus() {
		return Status;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setPolygon(String polygon) {
		this.polygon = polygon;
	}

	@Override
	public String getPolygon() {
		return polygon;
	}

	public void setPolyCentroid(String polyCentroid) {
		this.polyCentroid = polyCentroid;

		if (polyCentroid != null) {
			String geom = polyCentroid.replace("POINT(", "");
			geom = geom.replace(")", "");
			String[] coordinates = geom.split(" ");
			if (coordinates.length >= 2) {
				latitude = Double.parseDouble(coordinates[0]);
				longitude = Double.parseDouble(coordinates[1]);
			} else {
				// Log.e("ServiceLocation", "ServiceLocation " + id +
				// ": no longitude in centroid");
			}
		}
	}

	public String getPolyCentroid() {
		return polyCentroid;
	}

	public String getTimeLastSA() {
		return timeLastSA;
	}

	public void setTimeLastSA(String timeLastSA) {
		this.timeLastSA = timeLastSA;
	}

	public String getLastVisitDate() {
		return lastVisitDate;
	}

	public void setLastVisitDate(String lastVisitDate) {
		this.lastVisitDate = lastVisitDate;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@SuppressLint("SimpleDateFormat")
	public boolean isVisited2DaysAgo() {
		if (getLastVisitDate() == null || getLastVisitDate().trim().length() == 0)
			return false;
		try {
			Date lastDateTime = new SimpleDateFormat("yyyy-MM-dd").parse(getLastVisitDate());
			Date currentDateTime = new SimpleDateFormat("yyyy-MM-dd").parse(CommonUtils.UtcDateNow());
			final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
			int diffInDays = (int) ((currentDateTime.getTime() - lastDateTime.getTime()) / DAY_IN_MILLIS);
			if (diffInDays < 2)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressLint("SimpleDateFormat")
	public boolean isCompleted2DaysAgo() {
		if (getTimeLastSA() == null || getTimeLastSA().trim().length() == 0)
			return false;
		try {
			Date lastDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getTimeLastSA());
			//TODO 00 REFACTORING : CommonUtils.UtcDateNow() return a String from new Date() and SimpleDateFormat.parse return a new Date() - WHY???
			Date currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(CommonUtils.UtcDateNow());
			double hours = (currentDateTime.getTime() - lastDateTime.getTime()) / 1000 / 60 / 60;
			if (hours < 48)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isCompletedAfter(Date fromDate) {
		if (getTimeLastSA() == null || getTimeLastSA().trim().length() == 0)
			return false;
		try {
			final Date lastDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getTimeLastSA());
			if (lastDateTime.before(fromDate))
				return false;
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isVisitedAfter(Date fromDate) {
		if (getLastVisitDate() == null || getLastVisitDate().trim().length() == 0)
			return false;
		try {
			final Date lastDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getLastVisitDate());
			if (lastDateTime.before(fromDate))
				return false;
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isNotCompleted() {
		return (getTimeLastSA() == null || getTimeLastSA().trim().length() == 0);

	}

	public boolean isCompleted() {
		return (getTimeLastSA() != null && getTimeLastSA().trim().length() > 0);
	}

	public boolean isVisited() {
		return (getLastVisitDate() != null && getLastVisitDate().trim().length() > 0);
	}

}
