package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class AssetTypesDao extends Dao<AssetTypes> {
	public AssetTypesDao() {
		super("sb_asset_types");
	}
	
	@Override
	public void insert(AssetTypes dto) {
		insertDto(dto);
	}

	@Override
	public void replace(AssetTypes dto) {
		replaceDto(dto);
	}
	
	@Override
	public AssetTypes buildDto(JSONObject json) throws JSONException {
		AssetTypes assetType = new AssetTypes();
		
		assetType.setId(jsonParser.parseString(json, "id"));
		
		assetType.setCompany_id(jsonParser.parseString(json, "company_id"));
		assetType.setName(jsonParser.parseString(json, "name"));
		assetType.setDescription(jsonParser.parseString(json, "description"));
		assetType.setAsset_icon_id(jsonParser.parseString(json, "asset_icon_id"));
		assetType.setDivision_id(jsonParser.parseString(json, "division_id"));
		assetType.setStatus_code_id(jsonParser.parseString(json, "status_code_id"));
		assetType.setAsset_inspection_template_id(jsonParser.parseString(json, "asset_inspection_template_id"));
		assetType.setBounding_box_size(jsonParser.parseInt(json, "bounding_box_size"));
		assetType.setRadius_unit(jsonParser.parseString(json, "radius_unit"));
		assetType.setCreate_on_tablet(jsonParser.parseInt(json, "create_on_tablet"));
		assetType.setForce_color(jsonParser.parseString(json, "force_color"));

		assetType.setCreated(jsonParser.parseDate(json, "created"));
		assetType.setModified(jsonParser.parseDate(json, "modified"));
		
		return assetType;
	}

	@Override
	public AssetTypes buildDto(Cursor cursor) {
		AssetTypes assetType = new AssetTypes();

		assetType.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		
		assetType.setCompany_id(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		assetType.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		assetType.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
		assetType.setAsset_icon_id(cursor.getString(cursor.getColumnIndexOrThrow("asset_icon_id")));
		assetType.setDivision_id(cursor.getString(cursor.getColumnIndexOrThrow("division_id")));
		assetType.setStatus_code_id(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		assetType.setAsset_inspection_template_id(cursor.getString(cursor.getColumnIndexOrThrow("asset_inspection_template_id")));
		assetType.setBounding_box_size(cursor.getInt(cursor.getColumnIndexOrThrow("bounding_box_size")));
		assetType.setRadius_unit(cursor.getString(cursor.getColumnIndexOrThrow("radius_unit")));
		assetType.setCreate_on_tablet(cursor.getInt(cursor.getColumnIndexOrThrow("create_on_tablet")));
		assetType.setForce_color(cursor.getString(cursor.getColumnIndexOrThrow("force_color")));
		
		assetType.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		assetType.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		assetType.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return assetType;
	}
}
