package com.operasoft.snowboard.database;

import java.util.UUID;

public class Uploads extends Dto {
	private static final long serialVersionUID = 1L;
	
	public Uploads() {
		super();
		id = UUID.randomUUID().toString();
	}
	
	@Column(name="company_id")
	private String companyId;
	

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	@Column(name="original_filename")
	private String original_filename;

	public String getOriginal_filename() {
		return original_filename;
	}

	public void setOriginal_filename(String original_filename) {
		this.original_filename = original_filename;
	}

	@Column(name="model")
	private String model;


	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column(name="filename")
	private String filename;	

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Column(name="gps_cordinates")
	private String gps_cordinates;


	public String getGps_cordinates() {
		return gps_cordinates;
	}

	public void setGps_cordinates(String gps_cordinates) {
		this.gps_cordinates = gps_cordinates;
	}
	
	@Column(name="url")
	private String url;


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Column(name="imei_no")
	private String imei_no;


	public String getImei_no() {
		return imei_no;
	}

	public void setImei_no(String imei_no) {
		this.imei_no = imei_no;
	}
	
	@Column(name="creator_id")
	private String creator_id;


	public String getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(String creator_id) {
		this.creator_id = creator_id;
	}
}
