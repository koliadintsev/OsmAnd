package com.operasoft.snowboard.database;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class WorkOrderDao extends Dao<WorkOrder> {

	private WorkOrderItemDao itemDao = new WorkOrderItemDao();
	private WorkOrderPictureDao pictureDao = new WorkOrderPictureDao();
	
	public WorkOrderDao() {
		super("sb_work_orders");
	}

	public final String model = "WorkOrder";
	
	@Override
	public void insert(WorkOrder dto) {
		insertDto(dto);
		pictureDao.insert(dto.getWorkOrderItemPictures());
		itemDao.insert(dto.getWorkOrderItemList());
	}

	@Override
	public void replace(WorkOrder dto) {
		replaceDto(dto);
		pictureDao.replace(dto.getWorkOrderItemPictures());
		itemDao.replace(dto.getWorkOrderItemList());
	}

	public void insertOrReplaceDto(WorkOrder dto) {
		insertOrReplace(dto);
		pictureDao.insertOrReplace(dto.getWorkOrderItemPictures());
		itemDao.insertOrReplace(dto.getWorkOrderItemList());
	}
	
	public void removeWorkOrder(String id){
		remove(id);
		itemDao.deleteListAttachedWithWorkorder(id);
		pictureDao.deleteListAttachedWithWorkorder(id);
	}
	
	
	@Override
	public WorkOrder buildDto(JSONObject json) throws JSONException {
		WorkOrder dto = new WorkOrder();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));		
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		dto.setComments(jsonParser.parseString(json, "comments"));
		dto.setCreatorId(jsonParser.parseString(json, "creator_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setRoute_id(jsonParser.parseString(json, "route_id"));

		dto.setDescription(jsonParser.parseString(json, "description"));
		try {
			dto.setDateTime(jsonParser.parseDate(json, "date_time"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			dto.setDueDate(jsonParser.parseDate(json, "due_date"));	
		} catch(Exception e) {
			e.printStackTrace();
		}

		dto.setForeignValue(jsonParser.parseString(json, "foreign_value"));
		dto.setForeignKey(jsonParser.parseString(json, "foreign_key"));
		dto.setGpsCoordinates(jsonParser.parseString(json, "gps_coordinates"));
		dto.setStatus(jsonParser.parseString(json, "status_code_id"));
		dto.setWorkToDo(jsonParser.parseString(json, "work_to_do"));
		dto.setWorkDone(jsonParser.parseString(json, "work_done"));
		dto.setWorkOrderNumber(jsonParser.parseString(json, "work_order_number"));
		dto.setDamageTypeId(jsonParser.parseString(json, "damage_type_id"));
		dto.setWorkOrderCreationType(jsonParser.parseString(json, "work_order_creation_type"));
		dto.setTemplateId(jsonParser.parseString(json, "template_id"));
		dto.setWorkType(jsonParser.parseString(json, "work_type"));
		
		
		return dto;
	}

	@Override
	protected WorkOrder buildDto(Cursor cursor) {
		WorkOrder dto = new WorkOrder();
		
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));	
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		dto.setComments(cursor.getString(cursor.getColumnIndexOrThrow("comments")));
		dto.setCreatorId(cursor.getString(cursor.getColumnIndexOrThrow("creator_id")));
		dto.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		dto.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
		dto.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		dto.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow("due_date")));		
		dto.setForeignValue(cursor.getString(cursor.getColumnIndexOrThrow("foreign_value")));
		dto.setForeignKey(cursor.getString(cursor.getColumnIndexOrThrow("foreign_key")));
		dto.setGpsCoordinates(cursor.getString(cursor.getColumnIndexOrThrow("gps_coordinates")));
		dto.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setWorkToDo(cursor.getString(cursor.getColumnIndexOrThrow("work_to_do")));
		dto.setWorkDone(cursor.getString(cursor.getColumnIndexOrThrow("work_done")));
		dto.setWorkOrderNumber(cursor.getString(cursor.getColumnIndexOrThrow("work_order_number")));
		dto.setDamageTypeId(cursor.getString(cursor.getColumnIndexOrThrow("damage_type_id")));
		dto.setWorkOrderCreationType(cursor.getString(cursor.getColumnIndexOrThrow("work_order_creation_type")));
		dto.setTemplateId(cursor.getString(cursor.getColumnIndexOrThrow("template_id")));
		dto.setWorkType(cursor.getString(cursor.getColumnIndexOrThrow("work_type")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setRoute_id(cursor.getString(cursor.getColumnIndexOrThrow("route_id")));
		dto.setWorkOrderPicturesList(pictureDao.getListAttachedWithWorkorder(dto.getId()));
		
		return dto;
	}
	

}
