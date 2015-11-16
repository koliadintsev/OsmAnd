package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class WorkOrderItemDao extends WorkOrderBaseDao<WorkOrderItem>{

	public WorkOrderItemDao() {
		super("sb_work_order_items");
	}

	public final String model = "WorkOrderItem";
	
	@Override
	public WorkOrderItem buildDto(JSONObject json) throws JSONException {
		WorkOrderItem workOrderItem = new WorkOrderItem();
		
		workOrderItem.setId(jsonParser.parseString(json, "id"));
		
		workOrderItem.setWorkOrderId(jsonParser.parseString(json, "work_order_id"));
		workOrderItem.setProductId(jsonParser.parseString(json, "product_id"));
		workOrderItem.setResult(jsonParser.parseString(json, "result"));
		workOrderItem.setQuantity(jsonParser.parseFloat(json, "quantity"));

		workOrderItem.setCreated(jsonParser.parseDate(json, "created"));
		workOrderItem.setModified(jsonParser.parseDate(json, "modified"));
		
		return workOrderItem;
	}

	@Override
	public WorkOrderItem buildDto(Cursor cursor) {
		WorkOrderItem workOrderItem = new WorkOrderItem();

		workOrderItem.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		
		workOrderItem.setWorkOrderId(cursor.getString(cursor.getColumnIndexOrThrow("work_order_id")));
		workOrderItem.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("product_id")));
		workOrderItem.setResult(cursor.getString(cursor.getColumnIndexOrThrow("result")));
		workOrderItem.setQuantity(cursor.getFloat(cursor.getColumnIndexOrThrow("quantity")));
		
		workOrderItem.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		workOrderItem.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		workOrderItem.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return workOrderItem;
	}

}
