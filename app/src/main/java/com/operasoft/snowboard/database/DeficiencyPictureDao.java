package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class DeficiencyPictureDao extends Dao<DeficiencyPicture> {

	public DeficiencyPictureDao() {
		super("sb_route_deficiency_pictures");
	}

	@Override
	public void insert(DeficiencyPicture dto) {
		insertDto(dto);
		
	}

	public void insert(List<DeficiencyPicture> dtoList) {
		if(dtoList!=null){
			for(DeficiencyPicture picture : dtoList)
				insertDto(picture);
		}
	}
	
	@Override
	public void replace(DeficiencyPicture dto) {
		replaceDto(dto);
		
	}
	
	public void replace(List<DeficiencyPicture> dtoList) {
		if(dtoList!=null){
			for(DeficiencyPicture picture : dtoList)
				replaceDto(picture);
		}
	}

	@Override
	public DeficiencyPicture buildDto(JSONObject json) throws JSONException {
		DeficiencyPicture deficiencyPicture = new DeficiencyPicture();
		
		deficiencyPicture.setId(jsonParser.parseString(json, "id"));
		
		deficiencyPicture.setRouteDeficiencyId(jsonParser.parseString(json, "route_deficiency_id"));
		deficiencyPicture.setUploadId(jsonParser.parseString(json, "upload_id"));
		deficiencyPicture.setFilename(jsonParser.parseString(json, "filename"));
		deficiencyPicture.setCreatorId(jsonParser.parseString(json, "creator_id"));
		deficiencyPicture.setUrl(jsonParser.parseString(json, "url"));
		deficiencyPicture.setGps_cordinates(jsonParser.parseString(json, "gps_cordinates"));
		deficiencyPicture.setImeiNo(jsonParser.parseString(json, "imei_no"));

		deficiencyPicture.setCreated(jsonParser.parseDate(json, "created"));
		deficiencyPicture.setModified(jsonParser.parseDate(json, "modified"));
		
		return deficiencyPicture;
	}

	@Override
	protected DeficiencyPicture buildDto(Cursor cursor) {
		DeficiencyPicture deficiencyPicture = new DeficiencyPicture();
		
		deficiencyPicture.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		
		deficiencyPicture.setRouteDeficiencyId(cursor.getString(cursor.getColumnIndexOrThrow("route_deficiency_id")));
		deficiencyPicture.setUploadId(cursor.getString(cursor.getColumnIndexOrThrow("upload_id")));
		deficiencyPicture.setFilename(cursor.getString(cursor.getColumnIndexOrThrow("filename")));
		deficiencyPicture.setCreatorId(cursor.getString(cursor.getColumnIndexOrThrow("creator_id")));
		deficiencyPicture.setUrl(cursor.getString(cursor.getColumnIndexOrThrow("url")));
		deficiencyPicture.setGps_cordinates(cursor.getString(cursor.getColumnIndexOrThrow("gps_cordinates")));
		deficiencyPicture.setImeiNo(cursor.getString(cursor.getColumnIndexOrThrow("imei_no")));
		
		deficiencyPicture.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		deficiencyPicture.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		deficiencyPicture.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return deficiencyPicture;
	}
	
	public List<DeficiencyPicture> getListAttachedWithDeficiency(String deficiencyId) {
		final List<DeficiencyPicture> list = new ArrayList<DeficiencyPicture>();
		final String sql = "SELECT * FROM " + table + " WHERE route_deficiency_id = '" + deficiencyId + "'";
		Log.d("REQUETE SQL getListAttachedWithDeficiency ", sql);
		Cursor cursor = null;
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			while (cursor.moveToNext()) {
				DeficiencyPicture dto = buildDto(cursor);
				if (dto != null) {
					Log.d("ADD DeficiencyPicture getListAttachedWithDeficiency ", dto.getFilename());
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
	
	public void removeDeficiencyPictures(String deficiencyId) {
		String sql = "DELETE FROM " + table + " WHERE route_deficiency_id = '" + deficiencyId + "'";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

}
