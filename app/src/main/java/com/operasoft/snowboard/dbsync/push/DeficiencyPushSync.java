package com.operasoft.snowboard.dbsync.push;


import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.Deficiency;
import com.operasoft.snowboard.database.DeficiencyDao;
import com.operasoft.snowboard.database.DeficiencyPicture;
import com.operasoft.snowboard.database.DeficiencyPictureDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;


public class DeficiencyPushSync extends AbstractPushSync<Deficiency> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private DeficiencyDao dao = null;

	// Singleton pattern
	static private DeficiencyPushSync instance_s = new DeficiencyPushSync();

	static public DeficiencyPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new DeficiencyPushSync();
		}

		return instance_s;
	}

	private DeficiencyPushSync() {
		super("RouteDeficiency");
		getDao();
	}

	@Override
	protected Dao<Deficiency> getDao() {
		if (dao == null) {
			dao = new DeficiencyDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, Deficiency dto) {
		
		/*if (!dto.isNew()) {
			params.remove(actionParam);
			params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "edit"));
			params.add(new BasicNameValuePair(model + "[id]", dto.getId()));
		}else{
			params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "add"));
		}*/
		
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "add"));
		
		params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
		params.add(new BasicNameValuePair(model + "[route_selection_id]", dto.getRoute_selection_id()));
		params.add(new BasicNameValuePair(model + "[vehicle_id]", dto.getVehicleId()));
		params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[date_time]", dto.getDate()));
		params.add(new BasicNameValuePair(model + "[air_T]", dto.getAirT()));
		params.add(new BasicNameValuePair(model + "[ground_T]", dto.getGroundT()));
		params.add(new BasicNameValuePair(model + "[notes]", dto.getNotes()));
		params.add(new BasicNameValuePair(model + "[gps_coordinates]", dto.getGpsCoordinates()));
		params.add(new BasicNameValuePair(model + "[deficiency_type_id]", dto.getDeficiencyTypeId()));
		params.add(new BasicNameValuePair(model + "[route_id]", dto.getRouteId()));
		
		int counter = 0; 
		List<DeficiencyPicture> pictures = dto.getPictures();
		if(pictures != null){
			for(DeficiencyPicture picture : pictures){
				if (!picture.isNew()) {
					params.add(new BasicNameValuePair(DeficiencyPicture.MODEL + "[" + counter + "][id]", picture.getId()));
					params.add(new BasicNameValuePair(DeficiencyPicture.MODEL + "[" + counter + "][route_deficiency_id]", picture.getRouteDeficiencyId()));
				}
				params.add(new BasicNameValuePair(DeficiencyPicture.MODEL + "[" + counter + "][creator_id]", picture.getCreatorId()));
				params.add(new BasicNameValuePair(DeficiencyPicture.MODEL + "[" + counter + "][filename]", picture.getFilename()));
				params.add(new BasicNameValuePair(DeficiencyPicture.MODEL + "[" + counter + "][upload_id]", picture.getUploadId()));
				
				counter++;
			}
		}
	}

	@Override
	protected boolean processServerResponse(String value, Deficiency dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.i("DeficiencyPushSync", "empty JSON response received: " + value);
			if (dto.isNew()) {
				// Fake a created date...
				dto.setCreated(dateFormat.format(new Date()));
			}
			return true;
		} 

		// Let's make sure the DTO have been properly created on the server...
		// Parse the JSON data received
		JSONArray jsArray = new JSONArray(value);
		if (jsArray.length() > 0) {
			JSONObject jsonObject = jsArray.getJSONObject(0);
			Deficiency snowmanDto = dtoParser.parseDeficiency(jsonObject);
			
			/*if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(snowmanDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(snowmanDto.getId());
			}*/
			
			if(jsonObject != null){
				List<DeficiencyPicture> pictures = dto.getPictures();
				if(pictures != null){
					DeficiencyPictureDao deficiencyPictureDao = new DeficiencyPictureDao();
					deficiencyPictureDao.removeDeficiencyPictures(dto.getId());
				}
				
				dao.remove(dto.getId());
				dao.insertOrReplace(snowmanDto);
			}
			
			return true;
		} 

		Log.e("DeficiencyPushSync", "No object found in response: " + value);
		return false;
	}
	
	@Override
	public void saveClearDto(Deficiency dto) {
		// we do the save by ourself
	}
	
}
