package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.UUID;

import android.database.Cursor;
import android.util.Log;

/**
 * @author dounaka
 *
 */
public abstract class WorksheetBaseDao<T extends Dto> extends Dao<T> {

	public WorksheetBaseDao(String tablename) {
		super(tablename);
	}

	@Override
	public void insert(T dto) {
		if (dto.getId() == null)
			dto.setId(UUID.randomUUID().toString());
		insertDto(dto);
	}

	@Override
	public void replace(T dto) {
		replaceDto(dto);
	}

	public void replaceWorksheetId(String id, String newId) {
		String sql = "UPDATE " + table + " SET worksheet_id = '" + newId + "' WHERE worksheet_id = '" + id + "';";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	public void deleteListAttachedWithWorksheet(String wid) {
		final String sql = "delete  FROM " + table + " where worksheet_id = '" + wid + "'";
		Cursor cursor = null;
		try {
			DataBaseHelper.getDataBase().execSQL(sql);
		} catch (Exception e) {
			Log.e(sql, "sql exec error", e);
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public ArrayList<T> getListAttachedWithWorksheet(String worsheetId) {
		final ArrayList<T> list = new ArrayList<T>();
		final String sql = "SELECT * FROM " + table + " where worksheet_id = '" + worsheetId + "'";
		Cursor cursor = null;
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			while (cursor.moveToNext()) {
				T dto = buildDto(cursor);
				if (dto != null) {
					list.add(dto);
				}
			}
		} catch (Exception e) {
			Log.e(sql, "field not found", e);
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return list;
	}

}
