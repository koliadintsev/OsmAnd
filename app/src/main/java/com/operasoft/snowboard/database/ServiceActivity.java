package com.operasoft.snowboard.database;

import java.util.List;

import android.util.Log;

import com.operasoft.snowboard.util.Session;

public class ServiceActivity extends Dto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String SA_CREATED = "4fb4a78c-aa5c-410d-bb03-54c6ae8ed672";
	public final static String SA_UNASSIGNED = "3131c08c-f275-11e1-ac5c-0025900e9333";
	public final static String SA_CANCELLED = "4fb4a7d3-e508-487c-a787-54c6ae8ed672";
	public final static String SA_ASSIGNED = "4fb4a7b8-4ed4-4ed2-a38b-54c6ae8ed672";
	public final static String SA_ACCEPTED = "4fb4a7d9-0098-4b3d-ae91-54c6ae8ed672";
	public final static String SA_REJECTED = "4fb4a7cd-348c-40db-9eb2-54c6ae8ed672";
	public final static String SA_IN_DIRECTION = "4fb4a7e1-45c0-4984-ab32-54c6ae8ed672";
	public final static String SA_COMPLETED = "7e0d8798-fe08-102f-a906-0025900e9333";

	public final static String ALL_VEHICLE = "00000000-0000-0000-0000-00000000";

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "contract_id")
	private String contractId;

	@Column(name = "date_time")
	private String dateTime;

	@Column(name = "client_notes")
	private String clientNotes;

	@Column(name = "job_notes")
	private String jobNotes;

	@Column(name = "vehicle_id")
	private String vehicleId;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "sequence_number")
	private int sequenceNumber = -1;

	@Column(name = "status_code_id")
	private String status;

	@Column(name = "season_id")
	private String seasonId;

	@Column(name = "enroute_time")
	private String enrouteTime;

	@Column(name = "arrived_time")
	private String arrivedTime;

	@Column(name = "time_on_site")
	private String timeOnSite;

	@Column(name = "enroute_latitude")
	private String enrouteLatitude;

	@Column(name = "enroute_longitude")
	private String enrouteLongitude;

	public String getEnrouteTime() {
		return enrouteTime;
	}

	public void setEnrouteTime(String enrouteTime) {
		this.enrouteTime = enrouteTime;
	}

	public String getArrivedTime() {
		return arrivedTime;
	}

	public void setArrivedTime(String arrivedTime) {
		this.arrivedTime = arrivedTime;
	}

	public String getTimeOnSite() {
		return timeOnSite;
	}

	public void setTimeOnSite(String timeOnSite) {
		this.timeOnSite = timeOnSite;
	}

	public String getEnrouteLatitude() {
		return enrouteLatitude;
	}

	public void setEnrouteLatitude(String enrouteLatitude) {
		this.enrouteLatitude = enrouteLatitude;
	}

	public String getEnrouteLongitude() {
		return enrouteLongitude;
	}

	public void setEnrouteLongitude(String enrouteLongitude) {
		this.enrouteLongitude = enrouteLongitude;
	}

	private String dateTo;
	private String serviceLocationId = null;

	private ServiceLocation serviceLocation = null;
	private List<Activity> services = null;
	private List<ServiceActivityLog> logs = null;
	private ServiceActivityDetails saDetailsForm = null;

	/**
	 * This methods checks is some work is required based on the SA status
	 * 
	 * @return
	 */
	public boolean isWorkRequired() {
		return (status.equals(SA_ASSIGNED) || status.equals(SA_ACCEPTED) || status
				.equals(SA_IN_DIRECTION));
	}

	/**
	 * This method checks if a SA is currently assigned to me based on its
	 * userId or vehicleId
	 * 
	 * @return
	 */
	public boolean isMine() {
		if (vehicleId == null) {
			return false;
		}

		if (vehicleId.equals(ALL_VEHICLE)) {
			return true;
		}

		if (Session.getVehicle() != null) {
			if (Session.getVehicle().getId().equals(vehicleId)) {
				return true;
			}
		} else {
			Log.e("SA",
					"Failed to identify the current vehicle while evaluating SA "
							+ id);
		}

		return false;
	}

	/**
	 * Checks whether a SA is a "mission" SA based on its sequence number.
	 * "Regular" SA have a negative sequence number
	 */
	public boolean isMissionSA() {
		return (sequenceNumber >= 0);
	}

	/**
	 * Checks whether a SA is a prioritized "mission" SA based on its sequence
	 * number. Prioritized mission SAs have a sequence number greater than 0 to
	 * indicate the order in which they need to be performed by the driver.
	 */
	public boolean isPrioritizedMissionSA() {
		return (sequenceNumber > 0);
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getStatus() {
		return status;
	}

	public ServiceLocation getServiceLocation() {
		// We use a late binding approach for this... we only allocate the DTO
		// object when explicitly required.
		if ((serviceLocation == null) && (serviceLocationId != null)) {
			ServiceLocationDao dao = new ServiceLocationDao();
			serviceLocation = dao.getById(serviceLocationId);
		}
		return serviceLocation;
	}

	public void setServiceLocation(ServiceLocation serviceLocation) {
		this.serviceLocation = serviceLocation;
	}

	public List<Activity> listRequestedServices() {
		// We use a late binding approach for this... we only allocate the DTO
		// object when explicitly required.
		if ((services == null) || (services.isEmpty())) {
			ActivityDao dao = new ActivityDao();
			services = dao.listAllForServiceActivity(id);
		}
		return services;
	}

	public List<ServiceActivityLog> listLogs() {
		// We use a late binding approach for this... we only allocate the DTO
		// object when explicitly required.
		if ((logs == null) || (logs.isEmpty())) {
			ServiceActivityLogDao dao = new ServiceActivityLogDao();
			logs = dao.listAllForServiceActivity(id);
		}
		return logs;
	}

	public void setLogs(List<ServiceActivityLog> logs) {
		this.logs = logs;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatus(String status, boolean createLog) {
		this.status = status;
		if (createLog) {
			ServiceActivityLog saLog = new ServiceActivityLog(this);
			if (logs == null) {
				listLogs();
			}
			logs.add(saLog);
		}
	}

	public String getClientNotes() {
		return clientNotes;
	}

	public void setClientNotes(String clientNotes) {
		this.clientNotes = clientNotes;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getJobNotes() {
		return jobNotes;
	}

	public void setJobNotes(String jobNotes) {
		this.jobNotes = jobNotes;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(String seasonId) {
		this.seasonId = seasonId;
	}

	public String getServiceLocationId() {
		return serviceLocationId;
	}

	public void setServiceLocationId(String serviceLocationId) {
		this.serviceLocationId = serviceLocationId;
	}

	public void setServices(List<Activity> services) {
		this.services = services;
	}

	public ServiceActivityDetails getSaDetailsForm() {
		if (saDetailsForm == null) {
			ServiceActivityDetailsDao dao = new ServiceActivityDetailsDao();
			saDetailsForm = dao.getSaDetailsFormDB(id);
		}
		return saDetailsForm;
	}

	public void setSaDetailsForm(ServiceActivityDetails saDetailsForm) {
		this.saDetailsForm = saDetailsForm;
	}
}
