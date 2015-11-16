package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class AssetInspectionItemsDao extends Dao {

	protected AssetInspectionItemsDao() {
		super("sb_asset_inspection_items");
	}

	@Override
	public void insert(Object dto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replace(Object dto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object buildDto(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object buildDto(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

}
