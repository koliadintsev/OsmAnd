package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

/**
 * @author dounaka
 *
 */
public class WorksheetEquipmentDao extends WorksheetBaseDao<WorksheetEquipment> {

	public WorksheetEquipmentDao() {
		super("sb_worksheet_equipments");
	}

	@Override
	public WorksheetEquipment buildDto(JSONObject json) throws JSONException {
		WorksheetEquipment dto = new WorksheetEquipment();
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setWorksheetId(jsonParser.parseString(json, "worksheet_id"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setEquipmentId(jsonParser.parseString(json, "equipment_id"));
		dto.setProductId(jsonParser.parseString(json, "product_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setEquipmentDate(jsonParser.parseString(json, "date"));
		dto.setHours(jsonParser.parseFloat(json, "hours"));
		dto.setCreatorId(jsonParser.parseString(json, "creator_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		return dto;
	}

	@Override
	protected WorksheetEquipment buildDto(Cursor cursor) {
		WorksheetEquipment worksheetEquipment = new WorksheetEquipment();
		worksheetEquipment.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		worksheetEquipment.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		worksheetEquipment.setWorksheetId(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_id")));
		worksheetEquipment.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		worksheetEquipment.setEquipmentId(cursor.getString(cursor.getColumnIndexOrThrow("equipment_id")));
		worksheetEquipment.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("product_id")));
		worksheetEquipment.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		worksheetEquipment.setEquipmentDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		worksheetEquipment.setHours(cursor.getFloat(cursor.getColumnIndexOrThrow("hours")));
		worksheetEquipment.setCreatorId((cursor.getString(cursor.getColumnIndexOrThrow("creator_id"))));
		worksheetEquipment.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		worksheetEquipment.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		return worksheetEquipment;
	}

}
