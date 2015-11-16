package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

public class InspectionJournal extends Dto {

	static public final String PRE_DEPARTURE_TYPE = "Pre-Departure";
	static public final String END_OF_DAY_TYPE = "End of Day";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="date")
	private String date;

	@Column(name="user_id")
	private String userId;

	@Column(name="vehicle_id")
	private String vehicleId;
	
	@Column(name="company_id")
	private String companyId;

	@Column(name="type")
	private String type;
	
	private List<InspectionJournalDefect> defects;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addDefect(InspectionJournalDefect defect) {
		if (defects == null) {
			defects = new ArrayList<InspectionJournalDefect>();
		}
		
		defects.add(defect);
	}
	
	public List<InspectionJournalDefect> listDefects() {
		if (defects == null) {
			InspectionJournalDefectDao dao = new InspectionJournalDefectDao();
			defects = dao.listAllForJournal(id);
		}
		
		return defects;
	}

	@Override
	public void setId(String id) {
		super.setId(id);
		if (defects != null) {
			for (InspectionJournalDefect defect: defects) {
				defect.setJournalId(id);
			}
		}
	}

	@Override
	public void setNewId(String newId) {
		super.setNewId(newId);
		if (defects != null) {
			for (InspectionJournalDefect defect: defects) {
				defect.setJournalId(id);
			}
		}
	}
}
