package com.operasoft.snowboard.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class WorksheetMaintenanceProductDao extends Dao<WorksheetMaintenanceProducts> {

	public WorksheetMaintenanceProductDao() {
		super("sb_worksheet_maintenance_products");
	}

	@Override
	public void insert(WorksheetMaintenanceProducts dto) {
		insertDto(dto);
	}

	@Override
	public void replace(WorksheetMaintenanceProducts dto) {
		replaceDto(dto);
	}

	@Override
	protected WorksheetMaintenanceProducts buildDto(Cursor cursor) {
		WorksheetMaintenanceProducts WorksheetMaintenanceProducts = new WorksheetMaintenanceProducts();
		WorksheetMaintenanceProducts.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		WorksheetMaintenanceProducts.setCode(cursor.getString(cursor.getColumnIndexOrThrow("code")));
		WorksheetMaintenanceProducts.setCompany_id(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		WorksheetMaintenanceProducts.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		WorksheetMaintenanceProducts.setQuantity(cursor.getString(cursor.getColumnIndexOrThrow("quantity")));
		WorksheetMaintenanceProducts.setWorksheet_maintenance_id(cursor.getString(cursor
				.getColumnIndexOrThrow("worksheet_maintenance_id")));

		WorksheetMaintenanceProducts
				.setUnitOfMeasure(cursor.getString(cursor.getColumnIndexOrThrow("unit_of_measure")));
		WorksheetMaintenanceProducts.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("product_id")));
		WorksheetMaintenanceProducts.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		WorksheetMaintenanceProducts.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));

		return WorksheetMaintenanceProducts;
	}

	@Override
	public WorksheetMaintenanceProducts buildDto(JSONObject json) throws JSONException {
		WorksheetMaintenanceProducts dto = new WorksheetMaintenanceProducts();

		dto.setId(jsonParser.parseString(json, "id"));

		dto.setCode(jsonParser.parseString(json, "code"));
		dto.setCompany_id(jsonParser.parseString(json, "company_id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setQuantity(jsonParser.parseString(json, "quantity"));
		dto.setUnitOfMeasure(jsonParser.parseString(json, "unit_of_measure"));
		dto.setProductId(jsonParser.parseString(json, "product_id"));
		dto.setWorksheet_maintenance_id(jsonParser.parseString(json, "worksheet_maintenance_id"));

		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	public ArrayList<WorksheetMaintenanceProducts> getListAttachedWithWorksheet(String worsheetId) {
		ArrayList<WorksheetMaintenanceProducts> list = new ArrayList<WorksheetMaintenanceProducts>();

		String sql = "SELECT * FROM " + table + " where worksheet_maintenance_id = '" + worsheetId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		while (cursor.moveToNext()) {
			try {
				WorksheetMaintenanceProducts dto = buildDto(cursor);
				if (dto != null) {
					list.add(dto);
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();

		return list;
	}

	// public void replaceWorksheetId(String id, String newId) {
	// String sql = "UPDATE " + table + " SET worksheet_id = '" + newId +
	// "' WHERE worksheet_id = '" + id + "';";
	// DataBaseHelper.getDataBase().rawQuery(sql, null);
	// }

}