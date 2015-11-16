package com.operasoft.snowboard.dbsync.push;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.Uploads;
import com.operasoft.snowboard.database.UploadsDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;
import com.operasoft.snowboard.util.ImageUtils;

public class UploadsPushSync extends AbstractPushSync<Uploads> {
	private JsonDtoParser dtoParser = new JsonDtoParser();
	private UploadsDao UploadDao = null;

	// Singleton pattern
	static private UploadsPushSync instance_s = new UploadsPushSync();

	static public UploadsPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new UploadsPushSync();
		}

		return instance_s;
	}

	private UploadsPushSync() {
		super("Upload");
		actionParam = new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "uploadPic");
		getDao();
	}

	@Override
	protected Dao<Uploads> getDao() {
		if (UploadDao == null) {
			UploadDao = new UploadsDao();
		}
		return UploadDao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, Uploads dto) {
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "uploadPic"));

		params.add(new BasicNameValuePair(model + "[" + "company_id" + "]", dto.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[" + "id" + "]", dto.getId()));
		params.add(new BasicNameValuePair(model + "[" + "filename" + "]", dto.getFilename()));
		params.add(new BasicNameValuePair(model + "[" + "model" + "]", dto.getModel()));
		params.add(new BasicNameValuePair(model + "[" + "imei_no" + "]", dto.getImei_no()));
		try{
			params.add(new BasicNameValuePair(model + "[" + "base_data" + "]", ImageUtils.encodeBase64(dto.getFilename())));
		}catch(Exception e){
			params.add(new BasicNameValuePair(model + "[" + "base_data" + "]", ""));
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveClearDto(Uploads dto) {
		// we do the save in database by ourself in processServerResponse
	}

	@Override
	protected boolean processServerResponse(String value, Uploads dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.w("Uploads Push", "No details received from server: " + value);
			if (dto.isNew()) {
				// Fake a created date...
				dto.setCreated(dateFormat.format(new Date()));
			}
			return true;
		}

		// Let's make sure the DTO have been properly created on the server...
		// Parse the JSON data received
		boolean success = true;
		try {
			// Check the response received from the server:
			JSONObject response = new JSONObject(value);
			int status = response.optInt("status");
			String message = response.optString("message");

			boolean parseDto = false;

			switch (status) {
			case HttpStatus.SC_OK:
			case HttpStatus.SC_ACCEPTED:
				// Request successfully processed by the server.
				// Remove our old local copy of the workorder as we should have received the
				// latest one in the response. If not, we will get it in the next periodic sync
				//dao.remove(dto.getId());
				parseDto = true;
				break;
			case HttpStatus.SC_FORBIDDEN: // Unauthorized request
			case HttpStatus.SC_NOT_FOUND: // WorkOrder / Contract not found
				Log.e("Upload Push", "Client-side error received: " + status + " - " + message);
				//dao.remove(dto.getId());
				// Alert the user about the failure
				alertUser("Failed to upload changes to server. Status: " + status + ", details: " + message);
				break;
			default:
				Log.e("Upload Push", "Server-side error received: " + status + " - " + message);
				// Request failed to be processed on the server. 
				// Alert the user about the failure
				alertUser("Failed to upload changes to server. Will try again later. Status: " + status + ", details: " + message);
				// Mark DTO as dirty and try again later
				success = false;
			}


			if(parseDto){
				JSONArray jsArray = response.getJSONArray("data");
				if (jsArray.length() > 0) {
					Uploads serverDto = dtoParser.parseUploads(jsArray.getJSONObject(0), "Upload");
					UploadDao.insertOrReplace(serverDto);

					if(dto != null && dto.getFilename() != null ){
						try {
							deleteFile(dto.getFilename());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					return true;
				} 			
			}
		}catch(JSONException jse){
			jse.printStackTrace();
		}
		Log.e("Uploads Push", "No object found in response: " + value);
		return false;
	}
	
	public void deleteFile(String path) throws Exception{
		File deleteFile = new File(path);
		if (deleteFile.exists()) {
			boolean isDeleted = deleteFile.delete();
	
			if(!isDeleted)
				throw new Exception("Couldn't delete the file: " + path);
		}
	}
}
