package com.operasoft.snowboard.database;

public class WorkOrderPicture extends Dto {
	private static final long serialVersionUID = 1L;
	
	@Column(name="work_order_id")
	private String workOrderId;
	
	public String getWorkOrderId() {
		return workOrderId;
	}

	public void setWorkOrderId(String workOrderId) {
		this.workOrderId = workOrderId;
	}
	
	@Column(name="filename")
	private String filename;	

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Column(name="upload_id")
	private String uploadId;
	
	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	@Column(name="creator_id")
	private String creatorId;
	
	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Column(name="url")
	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name="gps_cordinates")
	private String gps_cordinates;
	
	public String getGps_cordinates() {
		return gps_cordinates;
	}

	public void setGps_cordinates(String gps_cordinates) {
		this.gps_cordinates = gps_cordinates;
	}

	@Column(name="imei_no")
	private String imei_no;

	public String getImei_no() {
		return imei_no;
	}

	public void setImei_no(String imei_no) {
		this.imei_no = imei_no;
	}
	
	final public static String model = "WorkOrderPicture";
}
