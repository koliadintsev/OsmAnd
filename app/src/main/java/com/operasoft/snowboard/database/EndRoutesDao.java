package com.operasoft.snowboard.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.operasoft.snowboard.dbsync.CommonUtils;

public class EndRoutesDao extends Dao<EndRoute> {

	public EndRoutesDao() {
		super("sb_end_routes");
	}

	@Override
	public void insert(EndRoute enroutes) {
		insertDto(enroutes);

	}

	@Override
	public void replace(EndRoute enroutes) {
		replaceDto(enroutes);

	}

	@Override
	public EndRoute buildDto(JSONObject json) throws JSONException {
		EndRoute dto = new EndRoute();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setDriverName(jsonParser.parseString(json, "driver_name"));
		dto.setLatitude(jsonParser.parseString(json, "latitude"));
		dto.setLongitude(jsonParser.parseString(json, "longitude"));
		dto.setDateTime(jsonParser.parseString(json, "date_time"));
		dto.setRouteId(jsonParser.parseString(json, "route_id"));
		dto.setTimeSpent(jsonParser.parseInt(json, "time_spent"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		dto.setRoute_selection_id(jsonParser.parseString(json, "route_selection_id"));
		dto.setUser_id(jsonParser.parseString(json, "user_id"));

		return dto;
	}

	@Override
	protected EndRoute buildDto(Cursor cursor) {
		EndRoute dto = new EndRoute();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setDriverName(cursor.getString(cursor.getColumnIndexOrThrow("driver_name")));
		dto.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow("latitude")));
		dto.setLongitude(cursor.getString(cursor.getColumnIndexOrThrow("longitude")));
		dto.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		dto.setRouteId(cursor.getString(cursor.getColumnIndexOrThrow("route_id")));
		dto.setTimeSpent(cursor.getInt(cursor.getColumnIndexOrThrow("time_spent")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		dto.setRoute_selection_id(cursor.getString(cursor.getColumnIndexOrThrow("route_selection_id")));
		dto.setUser_id(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));

		return dto;
	}

	@Override
	public List<EndRoute> listAllValid() {
		// TODO Auto-generated method stub
		return super.listAllValid();
	}

	@Deprecated
	public EndRoute getLast3daysRecentEndRoute(String routeId) {
		final String sql = "SELECT * FROM " + table + " where route_id = '" + routeId + "' order by date_time desc limit 1;";
		Cursor cursor = null;
		EndRoute endRoute = null;
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			while (cursor.moveToNext()) {
				endRoute = buildDto(cursor);
				if (endRoute != null) {
					java.util.Date lastDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endRoute.getDateTime());
					java.util.Date currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(CommonUtils.UtcDateNow());
					long hours = (currentDateTime.getTime() - lastDateTime.getTime()) / 1000 / 60 / 60;
					if (hours > 72) {
						endRoute = null;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("endroute", e.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();

		}
		if (endRoute != null && endRoute.getCompanyId() != null) {
			endRoute.company = (new CompanyDao()).getById(endRoute.getCompanyId());
		}
		return endRoute;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	select e1.id, e1.driver_name, e1.route_id,  e1.vehicle_id,  max(e1.modified)  
	from end_routes e1 
	group by e1.vehicle_id 
	  * 
	 * return the list of end routes for all vehicles
	 * @param routeId
	 * @return
	 */
	public List<EndRoute> getLastVehicleEndRoutes(String routeId) {
		final ArrayList<EndRoute> lastVehicleEndRoutes = new ArrayList<EndRoute>();
		final String sql = "select e1.* from sb_end_routes e1, (SELECT e.id, e.vehicle_id, MAX(e.modified) FROM sb_end_routes e GROUP BY e.vehicle_id) e2 where e1.id=e2.id and e1.route_id='"
				+ routeId + "'";
		Cursor cursor = null;
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			final CompanyCacheManager cacheMgr = new CompanyCacheManager();
			final VehiclesDao vehicleDao = new VehiclesDao();
			EndRoute vehicleEndRoute = null;
			while (cursor.moveToNext()) {
				vehicleEndRoute = buildDto(cursor);
				if (vehicleEndRoute != null) {
					if (vehicleEndRoute.getVehicleId() != null) {
						vehicleEndRoute.vehicle = vehicleDao.getById(vehicleEndRoute.getVehicleId());
					}
					vehicleEndRoute.company = cacheMgr.get(vehicleEndRoute.getCompanyId());
					lastVehicleEndRoutes.add(vehicleEndRoute);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(sql, "field not found", e);
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return lastVehicleEndRoutes;
	}

	private class CompanyCacheManager {

		private HashMap<String, Company> cache = new HashMap<String, Company>();

		private CompanyDao companyDao = new CompanyDao();

		private Company get(String id) {
			Company cachedCompany = cache.get(id);
			if (cachedCompany != null)
				return cachedCompany;
			cachedCompany = companyDao.getById(id);
			cache.put(id, cachedCompany);
			return cachedCompany;
		}
	}

	public boolean has3daysEndRoute(String routeId) {
		String sql = "SELECT * FROM " + table + " where route_id = '" + routeId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				EndRoute dto = buildDto(cursor);
				if (dto != null) {
					java.util.Date lastDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dto.getDateTime());
					java.util.Date currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(CommonUtils.UtcDateNow());
					double hours = (currentDateTime.getTime() - lastDateTime.getTime()) / 1000 / 60 / 60;
					if (hours < 72)
						return true;
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();
		return false;
	}

}
