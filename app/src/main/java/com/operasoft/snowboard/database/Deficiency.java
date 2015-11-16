package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.util.Log;

public class Deficiency extends Dto {
	private static final long serialVersionUID = 1L;
	
	@Column(name="route_id")
	private String routeId;
	
	@Column(name="route_selection_id")
	private String route_selection_id;
	
	public String getRoute_selection_id() {
		return route_selection_id;
	}

	public void setRoute_selection_id(String route_selection_id) {
		this.route_selection_id = route_selection_id;
	}

	@Column(name="user_id")
	private String userId;

	@Column(name="vehicle_id")
	private String vehicleId;

	@Column(name="company_id")
	private String companyId;
	
	@Column(name="date_time")
	private String date;
		
	@Column(name="deficiency_type_id")
	private String deficiencyTypeId;
	
	@Column(name="gps_coordinates")
	private String gpsCoordinates;

	@Column(name="air_T")
	private String airT;
	
	@Column(name="ground_T")
	private String groundT;
	
	@Column(name="notes")
	private String notes;
	
	public Deficiency(){
		super();
		id = UUID.randomUUID().toString();
	}
	
	public String getDeficiencyTypeId() {
		return deficiencyTypeId;
	}

	public void setDeficiencyTypeId(String deficiencyTypeId) {
		this.deficiencyTypeId = deficiencyTypeId;
	}

	public String getGpsCoordinates() {
		return gpsCoordinates;
	}

	public void setGpsCoordinates(String gpsCoordinates) {
		this.gpsCoordinates = gpsCoordinates;
	}

	public String getAirT() {
		return airT;
	}

	public void setAirT(String airT) {
		this.airT = airT;
	}

	public String getGroundT() {
		return groundT;
	}

	public void setGroundT(String groundT) {
		this.groundT = groundT;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	private List<DeficiencyPicture> pictures;

	public List<DeficiencyPicture> getPictures() {
		return pictures;
	}

	public void setPictures(List<DeficiencyPicture> pictures) {
		this.pictures = pictures;
	}
	
	@Override
	public void setSyncFlag(int syncFlag) {
		this.syncFlag = syncFlag;
		if (pictures != null ){
			for(DeficiencyPicture picture : pictures)
				picture.setSyncFlag(syncFlag);
		}
	}

	public void add(DeficiencyPicture picture){
		if (pictures == null )
			pictures = new ArrayList<DeficiencyPicture>();
		picture.setRouteDeficiencyId(id);
		pictures.add(picture);
	}
}
