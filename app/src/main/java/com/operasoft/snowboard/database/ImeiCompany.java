package com.operasoft.snowboard.database;

/**
 * TODO 00 refactoring - rename this class for Device or Tablet ??
 *
 */
public class ImeiCompany extends Dto {

	final protected static int SUSPENDED = 0;
	final protected static int NOT_SUSPENDED = 1;
	private static final long serialVersionUID = 1L;

	@Column(name = "imei_no")
	private String imeiNo;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "type")
	private String type;

	@Column(name = "name")
	private String name;

	@Column(name = "config")
	private String config;

	@Column(name = "gps_config_id")
	private String gpsConfigId;

	@Column(name = "version")
	private int version;

	@Column(name = "notes")
	private String notes;

	@Column(name = "status")
	private int status;

	public boolean equalsGpsConfig(ImeiCompany another) {
		if (gpsConfigId == null && another.gpsConfigId == null)
			return true;
		else if (gpsConfigId == null || another.gpsConfigId == null)
			return false;
		else
			return gpsConfigId.equals(another.gpsConfigId);
	}

	public String getImeiNo() {
		return imeiNo;
	}

	public void setImeiNo(String imeiNo) {
		this.imeiNo = imeiNo;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getGpsConfigId() {
		return gpsConfigId;
	}

	public void setGpsConfigId(String gpsConfigId) {
		this.gpsConfigId = gpsConfigId;
	}

	@Override
	public String toString() {
		return getImeiNo() + " " + getId();
	}

}
