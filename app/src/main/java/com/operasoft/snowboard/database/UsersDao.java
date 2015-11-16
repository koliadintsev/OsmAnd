package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class UsersDao extends Dao<User> {

	public UsersDao() {
		super("sb_users");
	}

	/**
	 * Checks if a given PIN exists in the database
	 */
	public boolean isPinValid(String userPin) {
		String sql = "select pin from " + table + " where pin= '" + userPin + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		}
		return false;
	}

	public User getByPin(String pin) {
		String sql = "SELECT * FROM " + table + " WHERE pin= '" + pin + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			try {
				cursor.moveToFirst();
				User dto = buildDto(cursor);
				cursor.close();
				return dto;
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		return null;
	}

	public String getUserIdForPin(String userPin) {
		String sql = "select id from " + table + " where pin= '" + userPin + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToNext();
				String userId = cursor.getString(cursor.getColumnIndexOrThrow("id"));
				cursor.close();
				return userId;
			}
		}

		return "";
	}

	@Override
	public void insert(User dto) {
		insertDto(dto);
	}

	@Override
	public void replace(User dto) {
		replaceDto(dto);
	}

	@Override
	protected User buildDto(Cursor cursor) {
		User dto = new User();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompany_id(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
		dto.setGroup_id(cursor.getString(cursor.getColumnIndexOrThrow("group_id")));
		dto.setLastName(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
		dto.setPin(cursor.getString(cursor.getColumnIndexOrThrow("pin")));
		dto.setSupplier(cursor.getString(cursor.getColumnIndexOrThrow("supplier")));
		dto.setLocale(cursor.getString(cursor.getColumnIndexOrThrow("locale")));
		dto.setSuperDriver(cursor.getInt(cursor.getColumnIndexOrThrow("is_superdriver")));
		dto.setForeman(cursor.getInt(cursor.getColumnIndexOrThrow("is_foreman")));
		dto.setWorkStatus(cursor.getString(cursor.getColumnIndexOrThrow("work_status")));
		dto.setWorkStatusDate(cursor.getString(cursor.getColumnIndexOrThrow("work_status_date")));
		dto.setCurrentVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("current_vehicle_id")));
		dto.setCurrentServiceLocationId(cursor.getString(cursor.getColumnIndexOrThrow("current_service_location_id")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public User buildDto(JSONObject json) throws JSONException {
		User dto = new User();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompany_id(jsonParser.parseString(json, "company_id"));
		dto.setFirstName(jsonParser.parseString(json, "first_name"));
		dto.setGroup_id(jsonParser.parseString(json, "group_id"));
		dto.setLastName(jsonParser.parseString(json, "last_name"));
		dto.setPin(jsonParser.parseString(json, "pin"));
		dto.setSupplier(jsonParser.parseString(json, "supplier"));
		dto.setLocale(jsonParser.parseString(json, "locale"));
		dto.setSuperDriver(jsonParser.parseInt(json, "is_superdriver"));
		dto.setForeman(jsonParser.parseInt(json, "is_foreman"));
		dto.setWorkStatus(jsonParser.parseString(json, "work_status"));
		dto.setWorkStatusDate(jsonParser.parseString(json, "work_status_date"));
		dto.setCurrentVehicleId(jsonParser.parseString(json, "current_vehicle_id"));
		dto.setCurrentServiceLocationId(jsonParser.parseString(json, "current_service_location_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	/**
	 * This method will return all the user's that are in Vehicle
	 * 
	 * @param vehicleId
	 * @return User's list
	 */
	public ArrayList<User> getUsersInVehicle(String vehicleId) {
		String sql = "SELECT * FROM " + table + " WHERE work_status = '" + User.STATUS_IN_VEHICLE + "' and current_vehicle_id = '" + vehicleId + "' ORDER BY LOWER(first_name) ASC";
		ArrayList<User> list = new ArrayList<User>();

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				User dto = buildDto(cursor);
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

	/**
	 * This method will return all the user's that are in SL with status inVehicle
	 * 
	 * @param vehicleId
	 * @param slId
	 * @return
	 */
	public ArrayList<User> getUsersInVehicleForSl(String vehicleId, String slId) {
		String sql = "SELECT * FROM " + table + " WHERE work_status = '" + User.STATUS_IN_VEHICLE + "' and current_vehicle_id = '" + vehicleId + "' and current_service_location_id = '" + slId
				+ "' ORDER BY LOWER(first_name) ASC";
		ArrayList<User> list = new ArrayList<User>();

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				User dto = buildDto(cursor);
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

	/**
	 * This method will return all other user's then getUsersInVehicleForSl()
	 * 
	 * @param vehicleId
	 * @param slId
	 * @return
	 */
	public ArrayList<User> getOtherUsersInVehicleForSl(String vehicleId, String slId) {
		String sql = "SELECT * FROM " + table + " WHERE work_status != '" + User.STATUS_IN_VEHICLE + "' or current_vehicle_id != '" + vehicleId + "' or current_service_location_id != '" + slId
				+ "' ORDER BY LOWER(first_name) ASC";
		ArrayList<User> list = new ArrayList<User>();

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				User dto = buildDto(cursor);
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

	/**
	 * List of all employees dropped at a SL
	 * 
	 * @param serviceLocationId
	 * @return
	 */
	public ArrayList<User> getDropEmployeesOnSL(String serviceLocationId) {
		ArrayList<User> list = new ArrayList<User>();
		String sql = "SELECT * FROM " + table + " WHERE " + "work_status = '" + User.STATUS_ON_SITE + "' AND current_service_location_id = '" + serviceLocationId + "' ORDER BY LOWER(first_name) ASC";

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			User user = buildDto(cursor);
			if (user != null) {
				list.add(user);
			}
		}
		cursor.close();
		return list;
	}

	/**
	 * List of all employees not included in getDropEmployeesOnSL()
	 * 
	 * @param vehicleId
	 * @param serviceLocationId
	 * @return
	 */
	public ArrayList<User> getOtherDropEmployeesOnSL(String vehicleId, String serviceLocationId) {
		String sql = "SELECT * FROM " + table + " WHERE work_status != '" + User.STATUS_ON_SITE + "' or current_service_location_id != '" + serviceLocationId + "' ORDER BY LOWER(first_name) ASC";
		ArrayList<User> list = new ArrayList<User>();

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				User dto = buildDto(cursor);
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

	/**
	 * List of all employees with on top users who are : 
	 * - login, punchin, drop off on the SL after the startdate
	 * 
	 * List ={ active employee on SL } +  { inactive employee }
	 * 
	 * @param vehicleId
	 * @param serviceLocationId
	 * @return
	 */
	public List<User> getTopActiveEmployees(String contractId, String startDate) {

		UserWorkStatusLogsDao workStatusDao = new UserWorkStatusLogsDao();
		ArrayList<String> userids = new ArrayList<String>();
		for (UserWorkStatusLogs workStatus : workStatusDao.getDropOff(contractId, startDate)) {
			userids.add(workStatus.getUserId());
		}
		List<User> allusers = listAll();
		Collections.sort(allusers, new TopEmployeeComparator(userids));
		return allusers;
	}

	class TopEmployeeComparator implements Comparator<User> {
		public TopEmployeeComparator(ArrayList<String> users) {
			super();
			dropOffUsers.addAll(users);
		}

		ArrayList<String> dropOffUsers = new ArrayList<String>();

		@Override
		public int compare(User lhs, User rhs) {
			if (lhs.getWorkStatus() == null && rhs.getWorkStatus() == null)
				return compareName(lhs, rhs);
			boolean ltop = isTop(lhs);
			boolean rtop = isTop(rhs);
			if (ltop && !rtop)
				return -1;
			else if (rtop && !ltop)
				return +1;
			else
				return compareName(lhs, rhs);
		}

		private boolean isTop(User u) {
			boolean top = false;
			if (dropOffUsers.contains(u.getId())) {
				top = true;
				u.tag = "droppedOff";
			}
			if (u.isInVehicle()) {
				top = true;
				u.tag = "in vehicle";
			}
			return top;
		}

		private int compareName(User lhs, User rhs) {
			final String lfull = lhs.getFirstName() + lhs.getLastName();
			final String rfull = rhs.getFirstName() + rhs.getLastName();
			return lfull.compareTo(rfull);
		}

	}
}
