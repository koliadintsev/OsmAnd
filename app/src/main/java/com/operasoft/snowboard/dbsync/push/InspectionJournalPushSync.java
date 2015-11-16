package com.operasoft.snowboard.dbsync.push;

import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.Activity;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.InspectionJournal;
import com.operasoft.snowboard.database.InspectionJournalDao;
import com.operasoft.snowboard.database.InspectionJournalDefect;
import com.operasoft.snowboard.database.ServiceActivityDao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.VehicleRefuelLog;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.util.Session;

public class InspectionJournalPushSync extends AbstractPushSync<InspectionJournal> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private InspectionJournalDao dao = null;

	// Singleton pattern
	static private InspectionJournalPushSync instance_s = new InspectionJournalPushSync();
	
	static public InspectionJournalPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new InspectionJournalPushSync();
		}
		
		return instance_s;
	}
	
	private InspectionJournalPushSync() {
		super("InspectionJournal");
	}

	@Override
	protected Dao<InspectionJournal> getDao() {
		if (dao == null) {
			dao = new InspectionJournalDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params,
			InspectionJournal dto) {
		params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[vehicle_id]", dto.getVehicleId()));
		params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
		params.add(new BasicNameValuePair(model + "[type]", dto.getType()));
		params.add(new BasicNameValuePair(model + "[date]", dto.getDate()));
		
		// Specify the data to put in the Activity table
		List<InspectionJournalDefect> defects = dto.listDefects();

		for (int i = 0; i < defects.size(); i++) {
			InspectionJournalDefect defect = defects.get(i);
			params.add(new BasicNameValuePair("InspectionJournalDefect[" + i + "][company_id]", dto.getCompanyId()));
			params.add(new BasicNameValuePair("InspectionJournalDefect[" + i + "][inspection_checklist_item_id]", defect.getItemId()));
			params.add(new BasicNameValuePair("InspectionJournalDefect[" + i + "][notes]", defect.getNotes()));
		}
	}

	@Override
	protected boolean processServerResponse(String value,
			InspectionJournal dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.i("InspectionJournalPushSync", "empty JSON response received: " + value);
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
			InspectionJournal snowmanDto = dtoParser.parseSnowmanInspectionJournal(jsonObject);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(snowmanDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(snowmanDto.getId());
			}
			return true;
		}
		
		Log.e("InspectionJournalPushSync", "Invalid response received/No object found in response: " + value);
		return false;
	}

}
