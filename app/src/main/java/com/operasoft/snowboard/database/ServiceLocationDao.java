package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class ServiceLocationDao extends Dao<ServiceLocation> {

	public ServiceLocationDao() {
		super("sb_service_locations");
	}

	/**
	 * Inserts a new SL in our database
	 * 
	 * @param sl
	 */
	@Override
	public void insert(ServiceLocation sl) {
		insertDto(sl);
	}

	/**
	 * Updates an existing SL in our database
	 * 
	 * @param sl
	 */
	@Override
	public void replace(ServiceLocation sl) {
		replaceDto(sl);
	}

	@Override
	protected ServiceLocation buildDto(Cursor cursor) {
		ServiceLocation serviceLocation = new ServiceLocation();

		serviceLocation.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		serviceLocation.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		serviceLocation.setContactId(cursor.getString(cursor.getColumnIndexOrThrow("contact_id")));
		serviceLocation.setStreetNumber(cursor.getString(cursor.getColumnIndexOrThrow("street_number")));
		serviceLocation.setStreetName(cursor.getString(cursor.getColumnIndexOrThrow("street_name")));
		serviceLocation.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
		serviceLocation.setCityName(cursor.getString(cursor.getColumnIndexOrThrow("city_name")));
		serviceLocation.setZip(cursor.getString(cursor.getColumnIndexOrThrow("zip")));
		serviceLocation.setComments(cursor.getString(cursor.getColumnIndexOrThrow("comments")));
		serviceLocation.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
		serviceLocation.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
		serviceLocation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		serviceLocation.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		serviceLocation.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		serviceLocation.setPolygon(cursor.getString(cursor.getColumnIndexOrThrow("polygon")));
		serviceLocation.setPolyCentroid(cursor.getString(cursor.getColumnIndexOrThrow("polycentroid")));
		serviceLocation.setTimeLastSA(cursor.getString(cursor.getColumnIndexOrThrow("time_of_last_completed_sa")));
		serviceLocation.setLastVisitDate(cursor.getString(cursor.getColumnIndexOrThrow("last_visit_date")));
		serviceLocation.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));

		return serviceLocation;
	}

	@Override
	public ServiceLocation buildDto(JSONObject json) throws JSONException {
		ServiceLocation sl = new ServiceLocation();

		sl.setId(jsonParser.parseString(json, "id"));
		sl.setCompanyId(jsonParser.parseString(json, "company_id"));
		sl.setContactId(jsonParser.parseString(json, "contact_id"));
		sl.setStreetNumber(jsonParser.parseString(json, "street_number"));
		sl.setStreetName(jsonParser.parseString(json, "street_name"));
		sl.setStatus(jsonParser.parseString(json, "status_code_id"));
		sl.setAddress(jsonParser.parseString(json, "address"));
		sl.setCityName(jsonParser.parseString(json, "city_name"));
		sl.setZip(jsonParser.parseString(json, "zip"));
		sl.setComments(jsonParser.parseString(json, "comments"));
		sl.setStatus(jsonParser.parseString(json, "status_code_id"));
		sl.setCreated(jsonParser.parseDate(json, "created"));
		sl.setModified(jsonParser.parseDate(json, "modified"));
		sl.setPolygon(jsonParser.parseString(json, "polygon"));
		sl.setPolyCentroid(jsonParser.parseString(json, "polycentroid"));
		sl.setTimeLastSA(jsonParser.parseDate(json, "time_of_last_completed_sa"));
		sl.setLastVisitDate(jsonParser.parseDate(json, "last_visit_date"));
		sl.setName(jsonParser.parseString(json, "name"));

		return sl;
	}

	/**
	 * Retrieves a ServiceLocation based on a contract ID
	 */
	public ServiceLocation findServiceLocationByContractId(String contractID) {
		ArrayList<ServiceLocation> serviceLocations = new ArrayList<ServiceLocation>();
		String sql = "select sb_contracts.id as conid, sb_service_locations.* from sb_contracts left join " + table
				+ " on sb_contracts.service_location_id = sb_service_locations.id where sb_contracts.id = " + "'" + contractID + "'";
		Cursor cursor = null;
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ServiceLocation serviceLocation = null;
		if (cursor.moveToFirst()) {
			try {
				serviceLocation = buildDto(cursor);
				if (serviceLocation != null) {
					serviceLocations.add(serviceLocation);
				} else {
					Log.e(sql, "Failed to buildDto");
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();
		return serviceLocation;
	}

	/**
	 * Retrieves a ServiceLocation based on a route ID
	 */
	public List<ServiceLocation> findByRoute(String route_id) {
		ArrayList<ServiceLocation> serviceLocations = new ArrayList<ServiceLocation>();
		String sql = "select sb_route_sequences.id as idr,sb_route_sequences.sequence_order, sb_service_locations.* from sb_route_sequences left join "
				+ "sb_service_locations on sb_route_sequences.service_location_id = sb_service_locations.id " + "where sb_route_sequences.route_id = " + "'" + route_id + "'"
				+ " ORDER BY sb_route_sequences.sequence_order ASC";
		Cursor cursor = null;
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ServiceLocation serviceLocation = new ServiceLocation();
		try {
			while (cursor.moveToNext()) {
				serviceLocation = buildDto(cursor);
				serviceLocations.add(serviceLocation);
			}
		} catch (Exception e) {
			Log.e(sql, "field not found", e);
		}
		cursor.close();
		return serviceLocations;
	}

	private Date resetDateTime(Date dt, int pastdays) {
		calendar.setTime(dt);
		calendar.add(java.util.Calendar.DAY_OF_MONTH, (-1 * pastdays));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	Calendar calendar = Calendar.getInstance();

	public List<ServiceLocation> findByRoute(String route_id, int completedInPastDays) {
		ArrayList<ServiceLocation> serviceLocations = new ArrayList<ServiceLocation>();
		String sql = "select sb_route_sequences.id as idr,sb_route_sequences.sequence_order, sb_service_locations.* from sb_route_sequences left join "
				+ "sb_service_locations on sb_route_sequences.service_location_id = sb_service_locations.id " + "where sb_route_sequences.route_id = " + "'" + route_id + "'"
				+ " ORDER BY sb_route_sequences.sequence_order ASC";
		Cursor cursor = null;
		ServiceLocation serviceLocation = null;
		final Date fromDate = resetDateTime(new Date(), completedInPastDays);
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			while (cursor.moveToNext()) {
				serviceLocation = buildDto(cursor);
				if (serviceLocation.isCompletedAfter(fromDate) || serviceLocation.isVisitedAfter(fromDate)) {
					serviceLocations.add(serviceLocation);
					Log.d("sldao", serviceLocation.getId() + "completed on " + serviceLocation.getTimeLastSA());
				} else {
					serviceLocation.setTimeLastSA(null);
					serviceLocation.setLastVisitDate(null);
				}
				if (serviceLocation.isNotCompleted()) {
					serviceLocations.add(serviceLocation);
					Log.d("sldao", serviceLocation.getId() + "not completed :#" + serviceLocation.getTimeLastSA() + "#");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e != null && e.getMessage() != null)
				Log.e(getClass().getSimpleName(), e.getMessage(), e);
		} finally {
			if (cursor != null)
				cursor.close();

		}
		return serviceLocations;
	}

	public String[] getGeoPolygon(String geom) {
		String[] polygons = null;
		if (geom.equals("null") == false) {
			String firstGeomIndex = geom.replace("POINT(", "");
			String GeomPolygon = firstGeomIndex.replace(")", "");
			String[] geomCoOrdinates = GeomPolygon.split(",");
			for (int i = 0; i < geomCoOrdinates.length; i++) {
				polygons = geomCoOrdinates[i].split(" ");
			}
		}
		return polygons;
	}

	public String getContactId(String slId) {
		String conId = "";
		String sql = "SELECT contact_id FROM sb_service_locations WHERE id = '" + slId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			conId = cursor.getString(0);
		}
		cursor.close();

		return conId;
	}

	/*
	 * 
	 *  and division.type='Construction'
	 * 
	 */
	public List<ServiceLocation> findAllServiceLocationByDivisionType(String divisionType) {
		List<ServiceLocation> serviceLocations = new ArrayList<ServiceLocation>();
		if (divisionType == null || divisionType.trim().length() == 0)
			return serviceLocations;
		ServiceLocation serviceLocation = null;
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM " + table + " WHERE id in (";
			sql += " select distinct c.service_location_id from sb_divisions d, sb_contracts c where  d.id=c.division_id";
			sql += " and d.type='" + divisionType + "'  and c.status_code_id in (SELECT id FROM sb_status_codes where model='Contract' and name='Activate')";
			sql += ")";
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			while (cursor.moveToNext()) {
				serviceLocation = buildDto(cursor);
				serviceLocations.add(serviceLocation);
			}
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return serviceLocations;
	}

}