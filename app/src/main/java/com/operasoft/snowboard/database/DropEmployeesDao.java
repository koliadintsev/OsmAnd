package com.operasoft.snowboard.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class DropEmployeesDao extends Dao<DropEmployees> {

	public DropEmployeesDao() {
		super("sb_drop_employee");
	}

	@Override
	public void insert(DropEmployees dto) {
		insertDto(dto);
	}

	@Override
	public void replace(DropEmployees dto) {
		replaceDto(dto);
	}

	@Override
	public DropEmployees buildDto(JSONObject json) throws JSONException {
		DropEmployees dto = new DropEmployees();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setEmployeeId(jsonParser.parseString(json, "employee_id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setServiceLocationId(jsonParser.parseString(json, "service_location_id"));
		dto.setPickTime(jsonParser.parseString(json, "pick_time"));
		dto.setDropTime(jsonParser.parseString(json, "drop_time"));
		dto.setOperation(jsonParser.parseString(json, "operation"));
		dto.setContractId(jsonParser.parseDate(json, "contract_id"));
		dto.setWorksheetMaintenanceId(jsonParser.parseString(json, "worksheet_maintenance_id"));
		dto.setLatitude(jsonParser.parseDate(json, "latitude"));
		dto.setLongitude(jsonParser.parseDate(json, "longitude"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		return dto;
	}

	@Override
	protected DropEmployees buildDto(Cursor cursor) {
		DropEmployees dropEmployees = new DropEmployees();

		dropEmployees.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dropEmployees.setEmployeeId(cursor.getString(cursor.getColumnIndexOrThrow("employee_id")));
		dropEmployees.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dropEmployees.setServiceLocationId(cursor.getString(cursor.getColumnIndexOrThrow("service_location_id")));
		dropEmployees.setPickTime(cursor.getString(cursor.getColumnIndexOrThrow("pick_time")));
		dropEmployees.setDropTime(cursor.getString(cursor.getColumnIndexOrThrow("drop_time")));
		dropEmployees.setOperation(cursor.getString(cursor.getColumnIndexOrThrow("operation")));
		dropEmployees.setContractId(cursor.getString(cursor.getColumnIndexOrThrow("contract_id")));
		dropEmployees.setWorksheetMaintenanceId(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_maintenance_id")));
		dropEmployees.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow("latitude")));
		dropEmployees.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow("longitude")));
		dropEmployees.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dropEmployees.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		return dropEmployees;
	}

	/**
	 * List of all employees dropped today on this SL
	 * 
	 * @param serviceLOcationId
	 * @param date
	 * @return list of DropEmplyees
	 */
	public ArrayList<DropEmployees> getDropEmployeesOnSL(String serviceLOcationId, String date) {
		ArrayList<DropEmployees> list = new ArrayList<DropEmployees>();
		String sql = "SELECT * FROM " + table + " WHERE " + "service_location_id = '" + serviceLOcationId + "' AND operation = 'drop' AND drop_time BETWEEN '" + date
				+ " 00:00:00.00' AND '" + date + " 23:59:59.999' AND employee_id NOT IN(SELECT employee_id  FROM " + table + " WHERE " + "service_location_id = '"
				+ serviceLOcationId + "' AND operation = 'pick' AND pick_time BETWEEN '" + date + " 00:00:00.00' AND '" + date + " 23:59:59.999')";

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			DropEmployees drop = buildDto(cursor);
			if (drop != null) {
				list.add(drop);
			}
		}
		cursor.close();
		return list;
	}

	/**
	 * List of employees that are already dropped
	 * 
	 * @param cDate
	 * @return List of DropEmployees
	 */
	public ArrayList<DropEmployees> getdropEmployee(String cDate) {
		ArrayList<DropEmployees> list = new ArrayList<DropEmployees>();
		String sql = "SELECT * FROM " + table + " WHERE " + "drop_time BETWEEN '" + cDate + " 00:00:00.00' AND '" + cDate + " 23:59:59.999'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			DropEmployees drop = buildDto(cursor);
			if (drop != null) {
				list.add(drop);
			}
		}
		cursor.close();
		return list;
	}

	public String getEmployeedropTime(String cDate, String userid) {
		String sql = "SELECT * FROM " + table + " WHERE " + "drop_time BETWEEN '" + cDate + " 00:00:00.00' AND '" + cDate + " 23:59:59.999' AND employee_id = '" + userid + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			DropEmployees drop = buildDto(cursor);
			if (drop != null) { return drop.getDropTime(); }
		}
		cursor.close();
		return "not dropped";
	}

	public String getEmployeedropLocation(String cDate, String userId) {
		String sql = "SELECT * FROM " + table + " WHERE " + "drop_time BETWEEN '" + cDate + " 00:00:00.00' AND '" + cDate + " 23:59:59.999' AND employee_id = '" + userId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			DropEmployees drop = buildDto(cursor);
			if (drop != null) { return drop.getServiceLocationId(); }
		}
		cursor.close();
		return "not dropped";
	}

	public String getEmployeepickTime(String cDate, String userId, String dateTime) {
		String sql = "SELECT * FROM " + table + " WHERE " + "pick_time BETWEEN '" + cDate + " 00:00:00.00' AND '" + cDate + " 23:59:59.999' AND employee_id = '" + userId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			DropEmployees drop = buildDto(cursor);
			if (drop != null) { return drop.getPickTime(); }
		}
		cursor.close();
		return "not picked";
	}

	public ArrayList<DropEmployees> getdropEmployeeDistinct(String cDate) {
		ArrayList<DropEmployees> list = new ArrayList<DropEmployees>();
		String sql = "SELECT *   FROM " + table + " WHERE " + "drop_time BETWEEN '" + cDate + " 00:00:00.00' AND '" + cDate + " 23:59:59.999' GROUP BY service_location_id";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			DropEmployees drop = buildDto(cursor);
			if (drop != null) {
				list.add(drop);
			}
		}
		cursor.close();
		return list;
	}

	/**
	 * 
	 * @param serviceLocationId
	 *            -- Service location Id
	 * @return true if employee dropped but not picked from service location
	 */
	public boolean isDropped(String serviceLocationId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String date = sdf.format(new Date());

		String sql = "SELECT * FROM " + table + " WHERE " + "service_location_id = '" + serviceLocationId + "' AND operation = 'drop' AND drop_time BETWEEN '" + date
				+ " 00:00:00.00' AND '" + date + " 23:59:59.999' AND employee_id NOT IN(SELECT employee_id  FROM " + table + " WHERE " + "service_location_id = '"
				+ serviceLocationId + "' AND operation = 'pick' AND pick_time BETWEEN '" + date + " 00:00:00.00' AND '" + date + " 23:59:59.999')";

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.getCount() > 0) return true;
		else return false;
	}

}
