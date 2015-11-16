package com.operasoft.snowboard.database;

public class DeficiencyPicture extends Dto {
	private static final long serialVersionUID = 1L;
	public static final String MODEL = "RouteDeficiencyPicture";
	
	@Column(name="gps_cordinates")
	private String gps_cordinates;
	
	@Column(name="route_deficiency_id")
	private String routeDeficiencyId;
	
	@Column(name="upload_id")
	private String uploadId;
	
	@Column(name="creator_id")
	private String creatorId;
	
	@Column(name="filename")
	private String filename;
	
	@Column(name="url")
	private String url;
	
	@Column(name="imei_no")
	private String imeiNo;

	public String getGps_cordinates() {
		return gps_cordinates;
	}

	public void setGps_cordinates(String gps_cordinates) {
		this.gps_cordinates = gps_cordinates;
	}

	public String getRouteDeficiencyId() {
		return routeDeficiencyId;
	}

	public void setRouteDeficiencyId(String routeDeficiencyId) {
		this.routeDeficiencyId = routeDeficiencyId;
	}

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImeiNo() {
		return imeiNo;
	}

	public void setImeiNo(String imeiNo) {
		this.imeiNo = imeiNo;
	}

}
