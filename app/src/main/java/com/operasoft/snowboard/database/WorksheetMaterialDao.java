package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

/**
 * @author dounaka
 *
 */
public class WorksheetMaterialDao extends WorksheetBaseDao<WorksheetMaterial> {

	public WorksheetMaterialDao() {
		super("sb_worksheet_materials");
	}

	@Override
	public WorksheetMaterial buildDto(JSONObject json) throws JSONException {
		WorksheetMaterial wMaterial = new WorksheetMaterial();

		wMaterial.setId(jsonParser.parseString(json, "id"));
		wMaterial.setCompanyId(jsonParser.parseString(json, "company_id"));
		wMaterial.setWorksheetId(jsonParser.parseString(json, "worksheet_id"));
		wMaterial.setProductId(jsonParser.parseString(json, "product_id"));
		wMaterial.setMaterialDate(jsonParser.parseString(json, "date"));
		wMaterial.setQuantity(jsonParser.parseFloat(json, "quantity"));
		wMaterial.setUnitOfMesureId(jsonParser.parseString(json, "unit_of_measure_id"));
		wMaterial.setCreatorId(jsonParser.parseString(json, "creator_id"));
		wMaterial.setCreated(jsonParser.parseDate(json, "created"));
		wMaterial.setModified(jsonParser.parseDate(json, "modified"));

		return wMaterial;
	}

	@Override
	protected WorksheetMaterial buildDto(Cursor cursor) {
		WorksheetMaterial wMaterial = new WorksheetMaterial();

		wMaterial.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		wMaterial.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		wMaterial.setWorksheetId(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_id")));
		wMaterial.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("product_id")));
		wMaterial.setMaterialDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		wMaterial.setQuantity(cursor.getFloat(cursor.getColumnIndexOrThrow("quantity")));
		wMaterial.setUnitOfMesureId(cursor.getString(cursor.getColumnIndexOrThrow("unit_of_measure_id")));
		wMaterial.setCreatorId((cursor.getString(cursor.getColumnIndexOrThrow("creator_id"))));
		wMaterial.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		wMaterial.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));

		return wMaterial;
	}

}
