package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class EquipmentTypesDao extends Dao<EquipmentTypes> {

	public EquipmentTypesDao() {
		super("sb_Equipment_types");
		this.orderByFields = " equipment_name, equipment_number ";
	}

	@Override
	public void insert(EquipmentTypes dto) {
		insertDto(dto);
	}

	@Override
	public void replace(EquipmentTypes dto) {
		replaceDto(dto);
	}

	@Override
	protected EquipmentTypes buildDto(Cursor cursor) {
		EquipmentTypes EquipmentTypes = new EquipmentTypes();
		EquipmentTypes.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		EquipmentTypes.setEquipmentName(cursor.getString(cursor.getColumnIndexOrThrow("equipment_name")));
		EquipmentTypes.setEquipmentNumber(cursor.getString(cursor.getColumnIndexOrThrow("equipment_number")));
		EquipmentTypes.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		EquipmentTypes.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		EquipmentTypes.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		return EquipmentTypes;
	}

	@Override
	public EquipmentTypes buildDto(JSONObject json) throws JSONException {
		EquipmentTypes dto = new EquipmentTypes();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setEquipmentName(jsonParser.parseString(json, "equipment_name"));
		dto.setEquipmentNumber(jsonParser.parseString(json, "equipment_number"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}