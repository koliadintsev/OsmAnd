package com.operasoft.snowboard.database;

public class Geofence extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="company_id")
	private String companyId;
	
	@Column(name="pcenter")
	private String center;
	
	@Column(name="comments")
	private String comments;
	
	@Column(name="geom")
	private String geom;
	
	@Column(name="color")
	private String color;
	
	@Column(name="status_code_id")
	private String status;

	@Column(name="name")
	private String name;

	@Column(name="deleted")
	private int deleted;

	@Column(name="input_threshold")
	private float inputThreshold;
	
	@Column(name="output_threshold")
	private float outputThreshold;
	
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCenter() {
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getGeom() {
		return geom;
	}

	public void setGeom(String geom) {
		this.geom = geom;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}	
	
	public boolean isDeleted() {
		return deleted > 0;
	}

	public float getInputThreshold() {
		return inputThreshold;
	}

	public void setInputThreshold(float inputThreshold) {
		this.inputThreshold = inputThreshold;
	}

	public float getOutputThreshold() {
		return outputThreshold;
	}

	public void setOutputThreshold(float outputThreshold) {
		this.outputThreshold = outputThreshold;
	}
}
