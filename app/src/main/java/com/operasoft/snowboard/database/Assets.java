package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Assets extends Dto {
	private static final long serialVersionUID = 1L;
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Column(name = "company_id")
	private String companyId;
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	@Column(name = "asset_type_id")
	private String asset_type_id;
	public String getAsset_type_id() {
		return asset_type_id;
	}
	public void setAsset_type_id(String asset_type_id) {
		this.asset_type_id = asset_type_id;
	}
	
	@Column(name = "number")
	private String number;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	@Column(name = "name")
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "description")
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name = "latitude")
	private double latitude;
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	@Column(name = "longitude")
	private double longitude;
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	@Column(name = "status_code_id")
	private String status_code_id;
	public String getStatus_code_id() {
		return status_code_id;
	}
	public void setStatus_code_id(String status_code_id) {
		this.status_code_id = status_code_id;
	}
	
	@Column(name = "last_inspection_date")
	private String last_inspection_date;
	public String getLast_inspection_date() {
		return last_inspection_date;
	}
	public void setLast_inspection_date(String last_inspection_date) {
		this.last_inspection_date = last_inspection_date;
	}
	
	@Column(name = "asset_inspection_sheet_count")
	private String asset_inspection_sheet_count;
	public String getAsset_inspection_sheet_count() {
		return asset_inspection_sheet_count;
	}
	public void setAsset_inspection_sheet_count(String asset_inspection_sheet_count) {
		this.asset_inspection_sheet_count = asset_inspection_sheet_count;
	}
	
//	@Column(name = "polygon")
//	private JGeometry polygon;	
	
	// --------------------------------------------	

	private ArrayList<AssetPictures> assetPicturesList;

	// --------------------------------------------	
	
	public List<AssetPictures> getAssetPicturesList() {
		if (assetPicturesList == null) {
			AssetPicturesDao workOrderPicturesDao = new AssetPicturesDao();
			Log.d("woi", "id:" + id);
			assetPicturesList = workOrderPicturesDao.getListAttachedWithWorkorder(id);
		}
		return assetPicturesList;
	}
	public void setAssetPicturesList(ArrayList<AssetPictures> assetPicturesList) {
		this.assetPicturesList = assetPicturesList;
	}
	
	// ---ADD ----------------------------------
	
	public void add(AssetPictures picture) {
		if (assetPicturesList == null) {
			assetPicturesList = new ArrayList<AssetPictures>();			
		}
		picture.setAsset_id(id);
		assetPicturesList.add(picture);
	}
}
