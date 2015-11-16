package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class WorkOrderPictureDao extends WorkOrderBaseDao<WorkOrderPicture> {
	
	public WorkOrderPictureDao() {
		super("sb_work_order_pictures");
	}
	
	public final String model = "WorkOrderPicture";

	@Override
	public WorkOrderPicture buildDto(JSONObject json) throws JSONException {
		WorkOrderPicture workOrderPictures = new WorkOrderPicture();
		
		workOrderPictures.setId(jsonParser.parseString(json, "id"));
		workOrderPictures.setWorkOrderId(jsonParser.parseString(json, "work_order_id"));
		workOrderPictures.setUploadId(jsonParser.parseString(json, "upload_id"));
		try{
			JSONObject jsonUpload = json.getJSONObject("Upload");
			if( jsonUpload != null ){
				workOrderPictures.setFilename(jsonParser.parseString(jsonUpload, "filename"));
				workOrderPictures.setUrl(jsonParser.parseString(jsonUpload, "url"));
			}
		}catch(Exception e){
			Log.e("Error WorkOrderPictureDao ", e.getMessage() );
		}

		workOrderPictures.setCreatorId(jsonParser.parseString(json, "creator_id"));
		workOrderPictures.setGps_cordinates(jsonParser.parseString(json, "gps_cordinates"));
		workOrderPictures.setImei_no(jsonParser.parseString(json, "imei_no"));
		workOrderPictures.setCreated(jsonParser.parseDate(json, "created"));
		workOrderPictures.setModified(jsonParser.parseDate(json, "modified"));
		
		return workOrderPictures;
	}

	@Override
	public WorkOrderPicture buildDto(Cursor cursor) {
		WorkOrderPicture workOrderPictures = new WorkOrderPicture();

		workOrderPictures.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		
		workOrderPictures.setWorkOrderId(cursor.getString(cursor.getColumnIndexOrThrow("work_order_id")));
		workOrderPictures.setUploadId(cursor.getString(cursor.getColumnIndexOrThrow("upload_id")));
		workOrderPictures.setFilename(cursor.getString(cursor.getColumnIndexOrThrow("filename")));
		workOrderPictures.setCreatorId(cursor.getString(cursor.getColumnIndexOrThrow("creator_id")));
		workOrderPictures.setUrl(cursor.getString(cursor.getColumnIndexOrThrow("url")));
		workOrderPictures.setGps_cordinates(cursor.getString(cursor.getColumnIndexOrThrow("gps_cordinates")));
		workOrderPictures.setImei_no(cursor.getString(cursor.getColumnIndexOrThrow("imei_no")));
		
		workOrderPictures.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		workOrderPictures.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		workOrderPictures.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return workOrderPictures;
	}
}
