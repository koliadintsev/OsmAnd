package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class WorkOrderBaseDao <T extends Dto> extends Dao<T> {

	public WorkOrderBaseDao(String tablename) {
		super(tablename);
	}

	@Override
	public void insert(T dto) {
		if (dto.getId() == null)
			dto.setId(UUID.randomUUID().toString());
		insertDto(dto);
		
	}
	
	public void insert(List<T> dtoList) {
		if(dtoList != null){
			for(T dto : dtoList)
				insert(dto);
		}
	}

	@Override
	public void replace(T dto) {
		replaceDto(dto);
	}
	
	public void replace(List<T> dtoList) {
		if(dtoList != null){
			for(T dto : dtoList)
				replaceDto(dto);
		}
	}
	
	public void insertOrReplace(List<T> dtoList) {
		if(dtoList != null){
			for(T dto : dtoList)
				insertOrReplace(dto);
		}
		
	}
	
	public void removeWorkOrderAttachements(String wid) {
		String sql = "DELETE FROM " + table + " WHERE work_order_id = '" + wid + "'";
		DataBaseHelper.getDataBase().execSQL(sql);
	}
	
	public void replaceWorkorderId(String id, String newId) {
		String sql = "UPDATE " + table + " SET work_order_id = '" + newId + "' WHERE work_order_id = '" + id + "';";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	public void deleteListAttachedWithWorkorder(String wid) {
		final String sql = "delete  FROM " + table + " where work_order_id = '" + wid + "'";
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

	public ArrayList<T> getListAttachedWithWorkorder(String workorderId) {
		final ArrayList<T> list = new ArrayList<T>();
		final String sql = "SELECT * FROM " + table + " where work_order_id = '" + workorderId + "'";
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

	@Override
	public T buildDto(JSONObject json) throws JSONException {
		return null;
	}

	@Override
	protected T buildDto(Cursor cursor) {
		return null;
	}
}
