package com.operasoft.snowboard.dbsync.push;

import java.util.Date;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.WorksheetEquipment;
import com.operasoft.snowboard.database.WorksheetEquipmentDao;
import com.operasoft.snowboard.database.WorksheetLabour;
import com.operasoft.snowboard.database.WorksheetLabourDao;
import com.operasoft.snowboard.database.WorksheetMaterial;
import com.operasoft.snowboard.database.WorksheetMaterialDao;
import com.operasoft.snowboard.database.WorksheetTravelTime;
import com.operasoft.snowboard.database.WorksheetTravelTimeDao;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.database.WorksheetsDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

/*
*/
public class WorksheetPushSync extends AbstractPushSync<Worksheets> {
	private final JsonDtoParser dtoParser = new JsonDtoParser();
	private WorksheetsDao worksheetsDao = null;
	private WorksheetLabourDao labourDao = null;
	private WorksheetEquipmentDao equipmentDao = null;
	private WorksheetMaterialDao materialDao = null;
	private WorksheetTravelTimeDao travelTimeDao = null;

	// Singleton pattern
	static private WorksheetPushSync instance_s = new WorksheetPushSync();

	static public WorksheetPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new WorksheetPushSync();
		}

		return instance_s;
	}

	protected WorksheetPushSync() {
		super("Worksheet");
		getDao();
	}

	@Override
	protected Dao<Worksheets> getDao() {
		if (worksheetsDao == null) {
			worksheetsDao = new WorksheetsDao();
			labourDao = new WorksheetLabourDao();
			equipmentDao = new WorksheetEquipmentDao();
			materialDao = new WorksheetMaterialDao();
			travelTimeDao = new WorksheetTravelTimeDao();
		}
		return worksheetsDao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, Worksheets worksheet) {
		params.remove(actionParam);
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "saveWorksheet"));

		if (!worksheet.isNew()) {
			params.add(new BasicNameValuePair(model + "[id]", worksheet.getId()));
		}
		params.add(new BasicNameValuePair(model + "[company_id]", worksheet.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[user_id]", worksheet.getUserId()));
		params.add(new BasicNameValuePair(model + "[start_date]", worksheet.getStartDate()));
		params.add(new BasicNameValuePair(model + "[visitors]", worksheet.getVisitors()));
		params.add(new BasicNameValuePair(model + "[notes]", worksheet.getNotes()));
		params.add(new BasicNameValuePair(model + "[desc_work_performed]", worksheet.getWorkPerformed()));
		params.add(new BasicNameValuePair(model + "[weather]", worksheet.getWeather()));
		params.add(new BasicNameValuePair(model + "[comments]", worksheet.getComments()));
		params.add(new BasicNameValuePair(model + "[notes_acident_incident]", worksheet.getAccidentNotes()));
		params.add(new BasicNameValuePair(model + "[temperature]", worksheet.getTemperature()));
		params.add(new BasicNameValuePair(model + "[contract_id]", worksheet.getContractId()));
		params.add(new BasicNameValuePair(model + "[creator_id]", worksheet.getCreatorId()));

		int counter = 0;

		for (WorksheetLabour labour : worksheet.getWorksheetLabourList()) {

			String hourzz = String.valueOf(labour.getHours());
			if (!labour.isNew()) {
				params.add(new BasicNameValuePair("WorksheetLabour[" + counter + "][id]", labour.getId()));
			}
			//params.add(new BasicNameValuePair("WorksheetLabour[" + counter + "][worksheet_id]", labour.getWorksheetId()));
			params.add(new BasicNameValuePair("WorksheetLabour[" + counter + "][company_id]", labour.getCompanyId()));
			params.add(new BasicNameValuePair("WorksheetLabour[" + counter + "][user_id]", labour.getUserId()));
			params.add(new BasicNameValuePair("WorksheetLabour[" + counter + "][product_id]", labour.getProductId()));
			params.add(new BasicNameValuePair("WorksheetLabour[" + counter + "][date]", labour.getLabourDate()));
			params.add(new BasicNameValuePair("WorksheetLabour[" + counter + "][hours]", String.valueOf(labour.getHours())));
			params.add(new BasicNameValuePair("WorksheetLabour[" + counter + "][creator_id]", labour.getCreatorId()));
			counter++;
		}
		counter = 0;
		for (WorksheetEquipment equipment : worksheet.getWorksheetEquipmentList()) {
			if (!equipment.isNew()) {
				params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][id]", equipment.getId()));
			}
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][company_id]", equipment.getCompanyId()));
			//params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][worksheet_id]", equipment.getWorksheetId()));
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][equipment_id]", equipment.getEquipmentId()));
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][vehicle_id]", equipment.getVehicleId()));
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][product_id]", equipment.getProductId()));
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][user_id]", equipment.getUserId()));
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][date]", equipment.getEquipmentDate()));
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][hours]", String.valueOf(equipment.getHours())));
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][creator_id]", equipment.getCreatorId()));
			counter++;
		}
		counter = 0;
		for (WorksheetMaterial material : worksheet.getWorksheetMaterialList()) {
			if (!material.isNew()) {
				params.add(new BasicNameValuePair("WorksheetMaterial[" + counter + "][id]", material.getId()));				
			}
			params.add(new BasicNameValuePair("WorksheetMaterial[" + counter + "][company_id]", material.getCompanyId()));
			//params.add(new BasicNameValuePair("WorksheetMaterial[" + counter + "][worksheet_id]", material.getWorksheetId()));
			params.add(new BasicNameValuePair("WorksheetMaterial[" + counter + "][product_id]", material.getProductId()));
			params.add(new BasicNameValuePair("WorksheetMaterial[" + counter + "][date]", material.getMaterialDate()));
			params.add(new BasicNameValuePair("WorksheetMaterial[" + counter + "][quantity]", String.valueOf(material.getQuantity())));
			params.add(new BasicNameValuePair("WorksheetMaterial[" + counter + "][unit_of_measure_id]", material.getUnitOfMesureId()));
			params.add(new BasicNameValuePair("WorksheetMaterial[" + counter + "][creator_id]", material.getCreatorId()));
			counter++;
		}
		counter = 0;
		for (WorksheetTravelTime traveltime : worksheet.getWorksheetTravelTimeList()) {
			if (!traveltime.isNew()) {
				params.add(new BasicNameValuePair("WorksheetTravelTime[" + counter + "][id]", traveltime.getId()));
			}
			params.add(new BasicNameValuePair("WorksheetTravelTime[" + counter + "][company_id]", traveltime.getCompanyId()));
			//params.add(new BasicNameValuePair("WorksheetTravelTime[" + counter + "][worksheet_id]", traveltime.getWorksheetId()));
			params.add(new BasicNameValuePair("WorksheetTravelTime[" + counter + "][user_id]", traveltime.getUserId()));
			params.add(new BasicNameValuePair("WorksheetTravelTime[" + counter + "][date]", traveltime.getTravelDate()));
			params.add(new BasicNameValuePair("WorksheetTravelTime[" + counter + "][hours]", String.valueOf(traveltime.getHours())));
			params.add(new BasicNameValuePair("WorksheetTravelTime[" + counter + "][creator_id]", traveltime.getCreatorId()));
			counter++;
		}

	}

	@Override
	protected boolean processServerResponse(String value, Worksheets dto) throws JSONException {
		boolean success = true;

		if (isEmptyJsonResponse(value)) {
			Log.w("Worksheet Push", "No details received from server: " + value);
			if (dto.isNew()) {
				removeWorksheet(dto);
			}
			return success;
		}

		// Check the response received from the server:
		JSONObject response = new JSONObject(value);
		int status = response.optInt("type");
		String message = response.optString("message");
		
		boolean parseDto = false;
		
		switch (status) {
			case HttpStatus.SC_OK:
			case HttpStatus.SC_ACCEPTED:
				// Request successfully processed by the server.
				// Remove our old local copy of the worksheet as we should have received the
				// latest one in the response. If not, we will get it in the next periodic sync
				removeWorksheet(dto);
				parseDto = true;
				break;
			case HttpStatus.SC_FORBIDDEN: // Unauthorized request
			case HttpStatus.SC_NOT_FOUND: // Worksheet / Contract not found
				Log.e("Worksheet Push", "Client-side error received: " + status + " - " + message);
				removeWorksheet(dto);
				// Alert the user about the failure
				alertUser("Failed to push worksheet changes to server. Status: " + status + ", details: " + message);
				break;
			default:
				Log.e("Worksheet Push", "Server-side error received: " + status + " - " + message);
				// Request failed to be processed on the server. 
				// Alert the user about the failure
				alertUser("Failed to push worksheet changes to server. Will try again later. Status: " + status + ", details: " + message);
				// Mark DTO as dirty and try again later
				success = false;
		}
		
		if (parseDto) {
			// Let's replace our local copy with the latest version received from the server
			JSONObject jsonObject = response.getJSONObject("data");
			Worksheets serverDto = dtoParser.parseWorksheet(jsonObject, "Worksheet");
			
			if (serverDto != null) {
				// We need to replace our local copy of the worksheet (and any dependant tables)
				// with this new copy.
				worksheetsDao.insertOrReplace(serverDto);
				for (WorksheetLabour labour : serverDto.getWorksheetLabourList()) {
					labourDao.insertOrReplace(labour);
				}
				for (WorksheetEquipment equipment : serverDto.getWorksheetEquipmentList()) {
					equipmentDao.insertOrReplace(equipment);
				}
				for (WorksheetMaterial material : serverDto.getWorksheetMaterialList()) {
					materialDao.insertOrReplace(material);
				}
				for (WorksheetTravelTime traveltime : serverDto.getWorksheetTravelTimeList()) {
					travelTimeDao.insertOrReplace(traveltime);
				}
			} else {			
				Log.w("Worksheet Push", "No DTO received from server: " + value);
			}
		}

		return success;
	}
	
	private void removeWorksheet(Worksheets dto) {
		equipmentDao.deleteListAttachedWithWorksheet(dto.getId());
		labourDao.deleteListAttachedWithWorksheet(dto.getId());
		materialDao.deleteListAttachedWithWorksheet(dto.getId());
		travelTimeDao.deleteListAttachedWithWorksheet(dto.getId());
		worksheetsDao.remove(dto.getId());		
	}
}
