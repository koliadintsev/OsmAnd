package com.operasoft.snowboard.database;

public class User extends Dto {

	private static final long serialVersionUID = 1L;

	public static final String STATUS_INACTIVE = "inactive";
	public static final String STATUS_ON_SITE = "onsite";
	public static final String STATUS_IN_VEHICLE = "invehicle";

	@Column(name = "company_id")
	private String company_id;

	@Column(name = "group_id")
	private String group_id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "pin")
	private String pin;

	@Column(name = "supplier")
	private String supplier;

	@Column(name = "locale")
	private String locale;

	@Column(name = "is_superdriver")
	private int superDriver;

	@Column(name = "is_foreman")
	private int foreman;

	@Column(name = "work_status")
	private String workStatus;

	@Column(name = "work_status_date")
	private String workStatusDate;

	@Column(name = "current_vehicle_id")
	private String currentVehicleId;

	@Column(name = "current_service_location_id")
	private String currentServiceLocationId;

	// should not be mapped in database or server sync
	// theses attributes are a convenient shortcut
	public transient String workStatusLabel;
	public transient Vehicle vehicle;
	public transient ServiceLocation serviceLocation;

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getFirstName() {
		return firstName == null ? "" : firstName;
	}

	public void setFirstName(String first_name) {
		this.firstName = first_name;
	}

	public String getLastName() {
		return lastName == null ? "" : lastName;
	}

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

	public void setLastName(String last_name) {
		this.lastName = last_name;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public int getSuperDriver() {
		return superDriver;
	}

	public void setSuperDriver(int superDriver) {
		this.superDriver = superDriver;
	}

	public boolean isSuperDriver() {
		return (superDriver != 0);
	}

	public int getForeman() {
		return foreman;
	}

	public void setForeman(int foreman) {
		this.foreman = foreman;
	}

	public boolean isForeman() {
		return (foreman != 0);
	}

	public boolean isInVehicle() {
		return (this.workStatus != null && this.workStatus.equals(STATUS_IN_VEHICLE));
	}

	public boolean isOnSite() {
		return (this.workStatus != null && this.workStatus.equals(STATUS_ON_SITE));		
	}
	
	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public String getWorkStatusDate() {
		return workStatusDate;
	}

	public void setWorkStatusDate(String workStatusDate) {
		this.workStatusDate = workStatusDate;
	}

	public String getCurrentVehicleId() {
		return currentVehicleId;
	}

	public void setCurrentVehicleId(String currentVehicleId) {
		this.currentVehicleId = currentVehicleId;
	}

	public String getCurrentServiceLocationId() {
		return currentServiceLocationId;
	}

	public void setCurrentServiceLocationId(String currentServiceLocationId) {
		this.currentServiceLocationId = currentServiceLocationId;
	}

	// open field 
	public Object tag;
}
