package com.operasoft.snowboard.dbsync.push;

import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.android.db.sync.DbSyncFailureException;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.Deficiency;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.WorkOrder;
import com.operasoft.snowboard.database.WorkOrderDao;
import com.operasoft.snowboard.database.WorkOrderItem;
import com.operasoft.snowboard.database.WorkOrderItemDao;
import com.operasoft.snowboard.database.WorkOrderPicture;
import com.operasoft.snowboard.database.WorkOrderPictureDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

public class WorkOrderPushSync extends AbstractPushSync<WorkOrder> {
	private final JsonDtoParser dtoParser = new JsonDtoParser();
	private WorkOrderDao workOrderDao = null;

	// Singleton pattern
	static private WorkOrderPushSync instance_s = new WorkOrderPushSync();

	static public WorkOrderPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new WorkOrderPushSync();
		}

		return instance_s;
	}

	protected WorkOrderPushSync() {
		super("WorkOrder");
		getDao();
	}
	
	@Override
	protected Dao<WorkOrder> getDao() {
		if (workOrderDao == null) {
			workOrderDao = new WorkOrderDao();
		}
		return workOrderDao;
	}
	
	private void removeWorkOrder(WorkOrder dto) {
		workOrderDao.removeWorkOrder(dto.getId());		
	}
	
	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, WorkOrder workOrder) {
		params.remove(actionParam);
		

		/*if (!workOrder.isNew()) {
			params.remove(actionParam);
			params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "edit"));
			params.add(new BasicNameValuePair(model + "[id]", workOrder.getId()));
		}else{
			params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "add"));
		}*/
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "add"));
		
		params.add(new BasicNameValuePair(model + "[company_id]", workOrder.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[user_id]", workOrder.getUserId()));
		params.add(new BasicNameValuePair(model + "[comments]", workOrder.getComments()));
		params.add(new BasicNameValuePair(model + "[creator_id]", workOrder.getCreatorId()));
		params.add(new BasicNameValuePair(model + "[vehicle_id]", workOrder.getVehicleId()));
		params.add(new BasicNameValuePair(model + "[description]", workOrder.getDescription()));
		params.add(new BasicNameValuePair(model + "[date_time]", workOrder.getDateTime()));
		params.add(new BasicNameValuePair(model + "[due_date]", workOrder.getDueDate()));
		params.add(new BasicNameValuePair(model + "[foreign_value]", workOrder.getForeignValue()));
		params.add(new BasicNameValuePair(model + "[foreign_key]", workOrder.getForeignKey()));
		params.add(new BasicNameValuePair(model + "[gps_coordinates]", workOrder.getGpsCoordinates()));
		params.add(new BasicNameValuePair(model + "[status_code_id]", workOrder.getStatus()));
		params.add(new BasicNameValuePair(model + "[work_to_do]", workOrder.getWorkToDo()));
		params.add(new BasicNameValuePair(model + "[work_done]", workOrder.getWorkDone()));
		params.add(new BasicNameValuePair(model + "[work_order_number]", workOrder.getWorkOrderNumber()));
		params.add(new BasicNameValuePair(model + "[damage_type_id]", workOrder.getDamageTypeId()));
		params.add(new BasicNameValuePair(model + "[work_order_creation_type]", workOrder.getWorkOrderCreationType()));
		params.add(new BasicNameValuePair(model + "[template_id]", workOrder.getTemplateId()));
		params.add(new BasicNameValuePair(model + "[route_id]", workOrder.getRoute_id()));
		params.add(new BasicNameValuePair(model + "[work_type]", workOrder.getWorkType()));
		
		int counter = 0;
//		for (WorkOrderItem item : workOrder.getWorkOrderItemList()) {
//			if (!item.isNew()) {
//				params.add(new BasicNameValuePair("WorkOrderItem[" + counter + "][id]", item.getId()));
//				params.add(new BasicNameValuePair("WorkOrderItem[" + counter + "][work_order_id]", item.getWorkOrderId()));
//			}
//			params.add(new BasicNameValuePair("WorkOrderItem[" + counter + "][product_id]", item.getProductId()));
//			params.add(new BasicNameValuePair("WorkOrderItem[" + counter + "][result]", item.getResult()));
//			params.add(new BasicNameValuePair("WorkOrderItem[" + counter + "][quantity]", Float.toString(item.getQuantity())));
//
//			counter++;
//		}
//		counter = 0;
		List<WorkOrderPicture> pictures = workOrder.getWorkOrderItemPictures();
		if (pictures != null)
		{
			for (WorkOrderPicture picture : pictures) {
				if (!picture.isNew()) {
					params.add(new BasicNameValuePair("WorkOrderPicture[" + counter + "][id]", picture.getId()));
					params.add(new BasicNameValuePair("WorkOrderPicture[" + counter + "][work_order_id]", picture.getWorkOrderId()));
				}
				params.add(new BasicNameValuePair("WorkOrderPicture[" + counter + "][creator_id]", picture.getCreatorId()));
				params.add(new BasicNameValuePair("WorkOrderPicture[" + counter + "][filename]", picture.getFilename()));
				params.add(new BasicNameValuePair("WorkOrderPicture[" + counter + "][upload_id]", picture.getUploadId()));
				counter++;
			}
		}
	}
	
	@Override
	protected void saveClearDto(WorkOrder dto) {
	}

	@Override
	protected boolean processServerResponse(String value, WorkOrder dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			if (dto.isNew()) {
				// Fake a created date...
				dto.setCreated(dateFormat.format(new Date()));
			}

			return true;
		}
		String message =null;
		try{
			
			JSONObject response = new JSONObject(value);
			message = response.optString("error");
			removeWorkOrder(dto);
			if(message == null || message.isEmpty()) 
				return processDataReceived(dto, response);
			
		}catch(Exception e){
			Log.e("PushSync - " + model, "Exception in  processServerResponse " + e);
		}
		alertUser("Failed to push workorder changes to server" +((message!=null)?", details: " + message:"."));
		return false;
	}
	
	protected boolean processDataReceived(WorkOrder dto, JSONObject jsonObject) throws DbSyncFailureException, JSONException {
		
		// Let's make sure the DTO have been properly created on the server...
		// Parse the JSON data received
		WorkOrder serverDto = dtoParser.parseWorkOrder(jsonObject, model);
		
		if (serverDto != null) {
			// We need to replace our local copy of the workorder (and any dependant tables)
			// with this new copy.
			workOrderDao.insertOrReplaceDto(serverDto);
			
			List<WorkOrderPicture> pictures = dto.getWorkOrderItemPictures();
			if(pictures != null){
				WorkOrderPictureDao workOrderPictureDao = new WorkOrderPictureDao();
				workOrderPictureDao.removeWorkOrderAttachements(dto.getId());
			}
			
			List<WorkOrderItem> items = dto.getWorkOrderItemList();
			if(items != null){
				WorkOrderItemDao workOrderItemDao = new WorkOrderItemDao();
				workOrderItemDao.removeWorkOrderAttachements(dto.getId());
			}

		} else {			
			Log.w("WorkOrder Push", "No DTO received from server: " + jsonObject.toString());
		}

		return true;
	}

}
