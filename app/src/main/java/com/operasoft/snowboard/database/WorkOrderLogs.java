package com.operasoft.snowboard.database;

public class WorkOrderLogs extends Dto {
	private static final long serialVersionUID = 1L;
	
	@Column(name="work_order_id")
	private String workOrderId;
	
	@Column(name="date_time")
	private String dateTime;
	
	@Column(name="vehicle_id")
	private String vehicleId;
	
	@Column(name="user_id")
	private String userId;
	
	@Column(name="status_code_id")
	private String status;
	
	public boolean isUnassigned() {
		return status.equals(WorkOrder.STATUS_WORKORDER_UNASSIGNED);
	}
	public boolean isAssigned() {
		return status.equals(WorkOrder.STATUS_WORKORDER_ASSIGNED);
	}
	public boolean isCompleted() {
		return status.equals(WorkOrder.STATUS_WORKORDER_COMPLETED);
	}
	public boolean isClosed() {
		return status.equals(WorkOrder.STATUS_WORKORDER_CLOSED);
	}
	public boolean isAccepted() {
		return status.equals(WorkOrder.STATUS_WORKORDER_ACCEPTED);
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
