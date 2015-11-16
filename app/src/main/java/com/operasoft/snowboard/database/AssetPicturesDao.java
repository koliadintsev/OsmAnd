package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class AssetPicturesDao extends AssetBaseDao<AssetPictures>{
	public AssetPicturesDao() {
		super("sb_asset_pictures");
	}

	@Override
	public AssetPictures buildDto(JSONObject json) throws JSONException {
		AssetPictures assetPicture = new AssetPictures();
		
		assetPicture.setId(jsonParser.parseString(json, "id"));
		
		assetPicture.setAsset_id(jsonParser.parseString(json, "asset_id"));
		assetPicture.setUploadId(jsonParser.parseString(json, "upload_id"));
		assetPicture.setFilename(jsonParser.parseString(json, "filename"));
		assetPicture.setCreatorId(jsonParser.parseString(json, "creator_id"));
		assetPicture.setUrl(jsonParser.parseString(json, "url"));
		assetPicture.setGps_cordinates(jsonParser.parseString(json, "gps_cordinates"));
		assetPicture.setImei_no(jsonParser.parseString(json, "imei_no"));

		assetPicture.setCreated(jsonParser.parseDate(json, "created"));
		assetPicture.setModified(jsonParser.parseDate(json, "modified"));
		
		return assetPicture;
	}

	@Override
	public AssetPictures buildDto(Cursor cursor) {
		AssetPictures assetPicture = new AssetPictures();

		assetPicture.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		
		assetPicture.setAsset_id(cursor.getString(cursor.getColumnIndexOrThrow("asset_id")));
		assetPicture.setUploadId(cursor.getString(cursor.getColumnIndexOrThrow("upload_id")));
		assetPicture.setFilename(cursor.getString(cursor.getColumnIndexOrThrow("filename")));
		assetPicture.setCreatorId(cursor.getString(cursor.getColumnIndexOrThrow("creator_id")));
		assetPicture.setUrl(cursor.getString(cursor.getColumnIndexOrThrow("url")));
		assetPicture.setGps_cordinates(cursor.getString(cursor.getColumnIndexOrThrow("gps_cordinates")));
		assetPicture.setImei_no(cursor.getString(cursor.getColumnIndexOrThrow("imei_no")));
		
		assetPicture.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		assetPicture.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		assetPicture.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return assetPicture;
	}
}
