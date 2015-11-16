package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.operasoft.snowboard.util.JSONParser;

/**
 * This interface defines the common methods 
 * @author Christian
 *
 * @param <T>
 */
public abstract class Dao<T> {
	protected int LIST_LIMIT = 50;
	protected String table;
	protected JSONParser jsonParser = new JSONParser();

	protected Dao(String table) {
		this.table = table;
	}

	public String getTable() {
		return table;
	}

	/**
	 * Checks if a given ID is already defined in the DB.
	 */
	public boolean exists(String id) {
		String sql = "SELECT id FROM " + table + " where id = '" + id + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Inserts or replaces a DTO in the DB
	 */
	public void insertOrReplace(T obj) {
		Dto dto = (Dto) obj;
		if (exists(dto.getId())) {
			if (dto.getNewId() != null) {
				// We need to replace the object in the database along with its ID
				replaceId(dto);
			}
			// Then, we need to override its content
			replace(obj);
		} else {
			if (dto.getId() == null) {
				// We need to set a temporary ID until we get the real one from the master database.
				dto.setId(UUID.randomUUID().toString());
			}
			insert(obj);
		}
	}

	/**
	 * Inserts of replaces a JSON representation of a DTO in the DB
	 * @param json
	 * @throws JSONException 
	 */
	public void insertOrReplace(JSONObject json) throws JSONException {
		T obj = buildDto(json);
		if (obj != null) {
			insertOrReplace(obj);
		} else {
			Log.e(table, "Failed to create DTO from JSON " + json);
		}
	}

	public void replaceId(Dto dto) {
		if (dto.getNewId() == null) {
			Log.e(table, "New ID is null, cannot replace ID " + dto.getId());
			return;
		}
		String sql = "UPDATE " + table + " SET id = '" + dto.getNewId() + "' WHERE id = '" + dto.getId() + "';";
		DataBaseHelper.getDataBase().execSQL(sql);
		dto.setId(dto.getNewId());
		dto.setNewId(null);
	}

	public T getFirst() {
		String sql = "SELECT * FROM " + table + " LIMIT 1;";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			try {
				T dto = buildDto(cursor);
				cursor.close();
				return dto;
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		return null;
	}

	/**
	 * This method defines a way to retrieve a DTO from its ID 
	 */
	public T getById(String id) {
		String sql = "SELECT * FROM " + table + " where id = '" + id + "';";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			try {
				T dto = buildDto(cursor);
				cursor.close();
				return dto;
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		return null;
	}

	protected String orderByFields = null;

	/**
	 * Returns all DTOs currently in the DB
	 */
	public List<T> listAll() {
		List<T> list = new ArrayList<T>();

		String sql = "SELECT * FROM " + table + (orderByFields == null ? ";" : " order by " + orderByFields + ";");
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				T dto = buildDto(cursor);
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
	 * Returns all DTOs currently in the DB as a map where the key is the DTO ID.
	 */
	public Map<String, T> listAllAsMap() {
		Map<String, T> map = new HashMap<String, T>();

		List<T> list = listAll();
		for (T obj : list) {
			map.put(((Dto) obj).getId(), obj);
		}

		return map;
	}

	/**
	 * Returns all DTOs currently in the DB that have a deleted value set to 0.
	 */
	public List<T> listAllValid() {
		List<T> list = new ArrayList<T>();

		String sql = "SELECT * FROM " + table + " WHERE deleted <= 0;";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				T dto = buildDto(cursor);
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
	 * Returns all DTOs currently in the DB that have a deleted value set to 0
	 * as a map where the key is the DTO ID.
	 */
	public Map<String, T> listAllValidAsMap() {
		Map<String, T> map = new HashMap<String, T>();

		List<T> list = listAllValid();
		for (T obj : list) {
			map.put(((Dto) obj).getId(), obj);
		}

		return map;
	}

	/**
	 * Removes an existing DTO from the DB
	 */
	public void remove(String id) {
		String sql = "DELETE FROM " + table + " WHERE id = '" + id + "'";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	/**
	 * Retrieves the last modified data we have in our DB. This is to let the PeriodicSync knows what to query for
	 */
	public String getLastModified() {
		String sql = "SELECT MAX(modified) AS modified FROM " + table + " WHERE modified <> 'null'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			return cursor.getString(0);
		}

		return "2012-11-01 00:00:00";
	}

	/**
	 * Returns the list of DTO objects that need to be pushed to Snowman.
	 * DAOs need to look at the dirty flag in the database those ones.
	 */
	public List<T> listDirtyDtos() {
		List<T> list = new ArrayList<T>();

		String sql = "SELECT * FROM " + table + " WHERE sync_flag > " + Dto.CLEAR + " AND sync_flag < " + Dto.FAILED;

		if (table.equals("sb_user_work_status_logs"))
			sql = "SELECT * FROM " + table + " WHERE sync_flag > " + Dto.CLEAR + " AND sync_flag < " + Dto.FAILED + " ORDER BY date_time";

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				T dto = buildDto(cursor);
				if (dto != null) {
					list.add(dto);
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}

		return list;
	}

	/**
	 * Clears the dirty flag of a given DTO
	 */
	public void clearDirtyDto(String id) {
		String sql = "UPDATE " + table + " SET sync_flag = " + Dto.CLEAR + " WHERE id = " + "'" + id + "'";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	public void clearDirtyDto(T obj) {
		Dto dto = (Dto) obj;
		clearDirtyDto(dto.getId());
	}

	/**
	 * Sets the dirty flag of a given DTO in the database
	 */
	public void markAsDirty(String id) {
		String sql = "UPDATE " + table + " SET sync_flag = " + Dto.DIRTY + " WHERE id = " + "'" + id + "'";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	public void markAsDirty(T obj) {
		Dto dto = (Dto) obj;
		dto.setSyncFlag(Dto.DIRTY);
		if (dto.isNew()) {
			if (dto.getId() == null) {
				// We need to set a temporary ID until we get the real one from the master database.
				dto.setId(UUID.randomUUID().toString());
			}
			insert(obj);
		} else {
			replace(obj);
		}
	}

	/**
	 * Updates the sync_flag of a dirty DTO to properly handle the number of retransmit allowed.
	 */
	public void updateDirtyDto(T obj) {
		Dto dto = (Dto) obj;
		int syncFlag = dto.getSyncFlag();
		syncFlag++;
		if (syncFlag > Dto.FAILED) {
			syncFlag = Dto.FAILED;
		}
		dto.setSyncFlag(syncFlag);
		String sql = "UPDATE " + table + " SET sync_flag = " + syncFlag + " WHERE id = " + "'" + dto.getId() + "'";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	/**
	 * This method is used to escape single quotes in SQLlite statements
	 * @param input
	 * @return
	 */
	protected String escape(String input) {
		if (input == null) {
			return null;
		}
		return input.replace("'", "''");
	}

	protected void insertDto(Dto dto) {
		List<String> columns = new ArrayList<String>();
		List<Object> values = new ArrayList<Object>();

		try {
			dto.prepareDbFields(columns, values);
			StringBuilder builder = new StringBuilder("INSERT INTO " + table + " (");
			for (int i = 0; i < columns.size(); i++) {
				if (i != 0) {
					builder.append(',');
				}
				builder.append(columns.get(i));
			}
			builder.append(") VALUES (");
			for (int i = 0; i < values.size(); i++) {
				if (i != 0) {
					builder.append(',');
				}
				builder.append(values.get(i));
			}
			builder.append(")");

			String sql = builder.toString();
			Log.v("SQL INSERT " + table, sql);
			DataBaseHelper.getDataBase().execSQL(sql);
		} catch (Exception e) {
			Log.e(table, "Failed to insert DTO", e);
			e.printStackTrace();
		}
	}

	protected void replaceDto(Dto dto) {
		List<String> columns = new ArrayList<String>();
		List<Object> values = new ArrayList<Object>();

		try {
			dto.prepareDbFields(columns, values);
			StringBuilder builder = new StringBuilder("UPDATE " + table + " SET ");
			for (int i = 0; i < columns.size(); i++) {
				if (i != 0) {
					builder.append(',');
				}
				builder.append(columns.get(i));
				builder.append("=");
				builder.append(values.get(i));
			}
			builder.append(" WHERE id = '" + dto.getId() + "'");

			String sql = builder.toString();
			Log.v("SQL UPDATE " + table, sql);
			DataBaseHelper.getDataBase().execSQL(sql);
		} catch (Exception e) {
			Log.e(table, "Failed to update DTO", e);
			e.printStackTrace();
		}
	}

	protected T getDto(String sql) {
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			try {
				T dto = buildDto(cursor);
				cursor.close();
				return dto;
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		if(!cursor.isClosed()){
			cursor.close();
		}
		return null;
	}
	
	protected List<T> listDtos(String sql) {
		List<T> list = null;
		Cursor cursor= null;
		try {
			list = new ArrayList<T>();
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			while (cursor.moveToNext()) {
					T dto = buildDto(cursor);
					if (dto != null) {
						list.add(dto);
					}
				} 
			if(!cursor.isClosed()){
				cursor.close();
			}
		}
		catch (Exception e) {			
			Log.e(sql, "field not found", e);
		}
		
		return list;
	}
	
	/**
	 * Inserts a new DTO in the DB
	 */
	abstract public void insert(T dto);

	/**
	 * Replaces an existing DTO in the DB
	 */
	abstract public void replace(T dto);

	/**
	 * This method builds the DTO object based on a JSON object (received from the server)
	 * @throws JSONException 
	 */
	abstract public T buildDto(JSONObject json) throws JSONException;

	/**
	 * This method builds the DTO object based on a database cursor.
	 */
	abstract protected T buildDto(Cursor cursor);

}
