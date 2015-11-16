package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.util.Log;

public class WorkOrder extends Dto {
	
	private static final long serialVersionUID = 1L;
	
	public final static String STATUS_WORKORDER_UNASSIGNED = "470c6da0-5501-11e4-b650-002186921b5b";
	public final static String STATUS_WORKORDER_ASSIGNED = "471092da-5501-11e4-b650-002186921b5b";
	public final static String STATUS_WORKORDER_COMPLETED = "59f9d7f4-5501-11e4-b650-002186921b5b";
	public final static String STATUS_WORKORDER_CLOSED = "1648e9c7-6644-11e4-aa3f-002186921b5b";
	public final static String STATUS_WORKORDER_ACCEPTED = "34226ef9-5502-11e4-b650-002186921b5b";
	
	public WorkOrder() {
		super();
		id = UUID.randomUUID().toString();
	}
	
	@Column(name = "company_id")
	private String companyId;

	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "comments")
	private String comments = "";
	
	@Column(name = "creator_id")
	private String creatorId;
	
	public String getCreatorId() {
		return creatorId;
	}
	
	@Column(name = "route_id")
	private String route_id;

	public String getRoute_id() {
		return route_id;
	}

	public void setRoute_id(String route_id) {
		this.route_id = route_id;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	@Column(name = "vehicle_id")
	private String vehicleId;
	
	@Column(name = "description")
	private String description = "";
	
	@Column(name="date_time")
	private String dateTime;
	
	@Column(name="due_date")
	private String dueDate;
	
	@Column(name="foreign_value")
	private String foreignValue = "";
	
	@Column(name="foreign_key")
	private String foreignKey = "";
	
	@Column(name="gps_coordinates")
	private String gpsCoordinates;
	
	@Column(name="status_code_id")
	private String status;
	
	@Column(name="work_to_do")
	private String workToDo;
	
	public String getWorkToDo() {
		return workToDo;
	}

	public void setWorkToDo(String workToDo) {
		this.workToDo = workToDo;
	}

	@Column(name="work_done")
	private String workDone;
	
	public String getWorkDone() {
		return workDone;
	}

	public void setWorkDone(String workDone) {
		this.workDone = workDone;
	}

	@Column(name="work_order_number")
	private String workOrderNumber;
	
	public String getWorkOrderNumber() {
		return workOrderNumber;
	}

	public void setWorkOrderNumber(String workOrderNumber) {
		this.workOrderNumber = workOrderNumber;
	}

	@Column(name="damage_type_id")
	private String damageTypeId;
	
	@Column(name="work_order_creation_type")
	private String workOrderCreationType;
	
	public String getWorkOrderCreationType() {
		return workOrderCreationType;
	}

	public void setWorkOrderCreationType(String workOrderCreationType) {
		this.workOrderCreationType = workOrderCreationType;
	}

	@Column(name="template_id")
	private String templateId;
	
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	@Column(name="work_type")
	private String workType;
	
	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status = STATUS_WORKORDER_UNASSIGNED;
	}

	public void setStatus(String status_code_id) {
		this.status = status_code_id;
	}
	
	public boolean isUnassigned() {
		return status.equals(STATUS_WORKORDER_UNASSIGNED);
	}
	public boolean isAssigned() {
		return status.equals(STATUS_WORKORDER_ASSIGNED);
	}
	public boolean isCompleted() {
		return status.equals(STATUS_WORKORDER_COMPLETED);
	}
	public boolean isClosed() {
		return status.equals(STATUS_WORKORDER_CLOSED);
	}
	public boolean isAccepted() {
		return status.equals(STATUS_WORKORDER_ACCEPTED);
	}

	public String getDamageTypeId() {
		return damageTypeId;
	}

	public void setDamageTypeId(String damage_type_id) {
		this.damageTypeId = damage_type_id;
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

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	
	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getGpsCoordinates() {
		return gpsCoordinates;
	}

	public void setGpsCoordinates(String gpsCoordinates) {
		this.gpsCoordinates = gpsCoordinates;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	public String getForeignValue() {
		return foreignValue;
	}

	public void setForeignValue(String foreignValue) {
		this.foreignValue = foreignValue;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	// --------------------------------------------
	public boolean isOpen() {
		return (status == null) || !(status.equalsIgnoreCase(STATUS_WORKORDER_COMPLETED) || status.equalsIgnoreCase(STATUS_WORKORDER_CLOSED));
	}
	
	// --------------------------------------------	

	private ArrayList<WorkOrderItem> workOrderItemList;
	private ArrayList<WorkOrderPicture> workOrderPicturesList;

	// --------------------------------------------	

	public List<WorkOrderItem> getWorkOrderItemList() {
		if (workOrderItemList == null) {
			WorkOrderItemDao workOrderItemDao = new WorkOrderItemDao();
			Log.d("woi", "id:" + id);
			workOrderItemList = workOrderItemDao.getListAttachedWithWorkorder(id);
		}
		return workOrderItemList;
	}
	public void setWorkOrderItemList(ArrayList<WorkOrderItem> workOrderItemList) {
		this.workOrderItemList = workOrderItemList;
	}
	
	public List<WorkOrderPicture> getWorkOrderItemPictures() {
		if (workOrderPicturesList == null) {
			WorkOrderPictureDao workOrderPicturesDao = new WorkOrderPictureDao();
			Log.d("woi", "id:" + id);
			workOrderPicturesList = workOrderPicturesDao.getListAttachedWithWorkorder(id);
		}
		return workOrderPicturesList;
	}
	public void setWorkOrderPicturesList(ArrayList<WorkOrderPicture> workOrderPicturesList) {
		this.workOrderPicturesList = workOrderPicturesList;
	}
	
	// ---ADD ----------------------------------
	public void add(WorkOrderItem item) {
		if (workOrderItemList == null) {
			workOrderItemList = new ArrayList<WorkOrderItem>();			
		}
		item.setWorkOrderId(id);
		workOrderItemList.add(item);
	}
	public void add(WorkOrderPicture picture) {
		if (workOrderPicturesList == null) {
			workOrderPicturesList = new ArrayList<WorkOrderPicture>();			
		}
		picture.setWorkOrderId(id);
		workOrderPicturesList.add(picture);
	}
	
	@Override
	public void setSyncFlag(int syncFlag){
		this.syncFlag = syncFlag;
		
		if(workOrderPicturesList != null){
			for (WorkOrderPicture picture : workOrderPicturesList)
				picture.setSyncFlag(syncFlag);
		}
		
		if(workOrderItemList != null){
			for (WorkOrderItem item : workOrderItemList)
				item.setSyncFlag(syncFlag);
		}
		
	}
}
