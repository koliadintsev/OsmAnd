package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class ServiceActivityDao extends Dao<ServiceActivity> {

	private ContractsDao contractDao = new ContractsDao();

	public ServiceActivityDao() {
		super("sb_service_activities");
	}

	public List<ServiceActivity> listActive(String userId, String vehicleId) {
		List<ServiceActivity> list = new ArrayList<ServiceActivity>();
		String sql = "SELECT * FROM " + table + " WHERE ("/* user_id = '" + userId + "' OR */+ "vehicle_id = '" + vehicleId + "')" + " AND (status_code_id = '" + ServiceActivity.SA_ASSIGNED
				+ "' OR status_code_id = '" + ServiceActivity.SA_ACCEPTED + "' OR status_code_id = '" + ServiceActivity.SA_IN_DIRECTION + "') order by date_time LIMIT " + LIST_LIMIT;
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			ServiceActivity sa = buildDto(cursor);
			if (sa != null) {
				list.add(sa);
			}
		}
		cursor.close();
		return list;
	}

	public List<ServiceActivity> listOthers(String vehicleId) {
		List<ServiceActivity> list = new ArrayList<ServiceActivity>();
		String sql = "SELECT * FROM " + table + " WHERE (" + "vehicle_id <> '" + vehicleId + "')" + " AND (status_code_id = '" + ServiceActivity.SA_ASSIGNED + "' OR status_code_id = '"
				+ ServiceActivity.SA_ACCEPTED + "' OR status_code_id = '" + ServiceActivity.SA_IN_DIRECTION + "')";

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			ServiceActivity sa = buildDto(cursor);
			if (sa != null) {
				list.add(sa);
			}
		}
		cursor.close();
		return list;
	}

	public List<ServiceActivity> listEnRoute(String vehicleId) {
		List<ServiceActivity> list = new ArrayList<ServiceActivity>();
		String sql = "SELECT * FROM " + table + " WHERE vehicle_id = '" + vehicleId + "'" + " AND status_code_id = '" + ServiceActivity.SA_IN_DIRECTION + "'";

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			ServiceActivity sa = buildDto(cursor);
			if (sa != null) {
				list.add(sa);
			}
		}
		cursor.close();
		return list;
	}

	/**
	 * Inserts a new SA in our database
	 * 
	 * @param sa
	 */
	@Override
	public void insert(ServiceActivity sa) {
		insertDto(sa);
	}

	/**
	 * Updates an existing SA in our database
	 * 
	 * @param sa
	 */
	@Override
	public void replace(ServiceActivity sa) {
		replaceDto(sa);
	}

	public String getRouteGroupId(String serviceLocationId) {
		String routeGroupId = "";
		String sql = "SELECT r.route_group_id FROM sb_routes r, sb_route_sequences rs where rs.route_id = r.id and rs.service_location_id = '" + serviceLocationId + "'";

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst())
			routeGroupId = cursor.getString(0);

		cursor.close();
		return routeGroupId;
	}

	/**
	 * This method builds the DTO object based on a database cursor.
	 */
	@Override
	protected ServiceActivity buildDto(Cursor cursor) {
		String contractId = cursor.getString(cursor.getColumnIndexOrThrow("contract_id"));

		ServiceActivity serviceActivity = new ServiceActivity();

		serviceActivity.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		serviceActivity.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		serviceActivity.setContractId(contractId);
		serviceActivity.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		serviceActivity.setClientNotes(cursor.getString(cursor.getColumnIndexOrThrow("client_notes")));
		serviceActivity.setJobNotes(cursor.getString(cursor.getColumnIndexOrThrow("job_notes")));
		serviceActivity.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		serviceActivity.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		serviceActivity.setSequenceNumber(cursor.getInt(cursor.getColumnIndexOrThrow("sequence_number")));
		serviceActivity.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		serviceActivity.setSeasonId(cursor.getString(cursor.getColumnIndexOrThrow("season_id")));
		serviceActivity.setArrivedTime(cursor.getString(cursor.getColumnIndexOrThrow("arrived_time")));
		serviceActivity.setEnrouteTime(cursor.getString(cursor.getColumnIndexOrThrow("enroute_time")));
		serviceActivity.setEnrouteLatitude(cursor.getString(cursor.getColumnIndexOrThrow("enroute_latitude")));
		serviceActivity.setEnrouteLongitude(cursor.getString(cursor.getColumnIndexOrThrow("enroute_longitude")));
		serviceActivity.setTimeOnSite(cursor.getString(cursor.getColumnIndexOrThrow("time_on_site")));
		serviceActivity.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		serviceActivity.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		serviceActivity.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		serviceActivity.setServiceLocationId(contractDao.getServiceLocationIdForContract(contractId));
		return serviceActivity;
	}

	@Override
	public ServiceActivity buildDto(JSONObject jsonObject) throws JSONException {
		ServiceActivity sa = new ServiceActivity();

		sa.setId(jsonParser.parseString(jsonObject, "id"));
		sa.setCompanyId(jsonParser.parseString(jsonObject, "company_id"));
		sa.setContractId(jsonParser.parseString(jsonObject, "contract_id"));
		sa.setDateTime(jsonParser.parseDate(jsonObject, "date_time"));
		sa.setClientNotes(jsonParser.parseString(jsonObject, "client_notes"));
		sa.setJobNotes(jsonParser.parseString(jsonObject, "job_notes"));
		sa.setVehicleId(jsonParser.parseString(jsonObject, "vehicle_id"));
		sa.setUserId(jsonParser.parseString(jsonObject, "user_id"));
		sa.setSequenceNumber(jsonObject.optInt("sequence_number"));
		sa.setStatus(jsonParser.parseString(jsonObject, "status_code_id"));
		sa.setSeasonId(jsonParser.parseString(jsonObject, "season_id"));
		sa.setArrivedTime(jsonParser.parseString(jsonObject, "arrived_time"));
		sa.setEnrouteTime(jsonParser.parseString(jsonObject, "enroute_time"));
		sa.setEnrouteLatitude(jsonParser.parseString(jsonObject, "enroute_latitude"));
		sa.setEnrouteLongitude(jsonParser.parseString(jsonObject, "enroute_longitude"));
		sa.setTimeOnSite(jsonParser.parseString(jsonObject, "time_on_site"));
		sa.setCreated(jsonParser.parseDate(jsonObject, "created"));
		sa.setModified(jsonParser.parseDate(jsonObject, "modified"));
		sa.setServiceLocationId(contractDao.getServiceLocationIdForContract(sa.getContractId()));

		return sa;
	}

	public ServiceActivity getLastEnroute(String vehicleId) {
		String sql = "SELECT * FROM " + table + " WHERE vehicle_id = '" + vehicleId + "'" + " AND status_code_id = '" + ServiceActivity.SA_IN_DIRECTION + "'" + " AND enroute_latitude <> 'null'";

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			ServiceActivity sa = buildDto(cursor);
			if (sa != null) {
				return sa;
			}
		}
		cursor.close();
		return null;
	}

}