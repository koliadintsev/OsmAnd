package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class TabletConfigsDao extends Dao<TabletConfigs> {

	public TabletConfigsDao() {
		super("sb_tablet_configs");
	}

	@Override
	public void insert(TabletConfigs dto) {
		insertDto(dto);
	}

	@Override
	public void replace(TabletConfigs dto) {
		replaceDto(dto);
	}

	@Override
	protected TabletConfigs buildDto(Cursor cursor) {
		TabletConfigs dto = new TabletConfigs();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setTurnByTurn(cursor.getString(cursor.getColumnIndexOrThrow("turn_by_turn")));
		dto.setTurnCount(cursor.getInt(cursor.getColumnIndexOrThrow("turn_count")));
		dto.setTurnDistance(cursor.getInt(cursor.getColumnIndexOrThrow("turn_distance")));
		dto.setTurnDisplay(cursor.getString(cursor.getColumnIndexOrThrow("turn_display")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public TabletConfigs buildDto(JSONObject json) throws JSONException {
		TabletConfigs dto = new TabletConfigs();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setTurnByTurn(jsonParser.parseString(json, "turn_by_turn"));
		dto.setTurnCount(jsonParser.parseInt(json, "turn_count"));
		dto.setTurnDistance(jsonParser.parseInt(json, "turn_distance"));
		dto.setTurnDisplay(jsonParser.parseString(json, "turn_display"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	public TabletConfigs getByCompanyId(String companyId) {
		TabletConfigs tabletConfig = null;
		final String sql = "SELECT * FROM " + table + " where company_id = '" + companyId + "';";
		final Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			try {
				tabletConfig = buildDto(cursor);
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		}
		if (tabletConfig == null) {
			tabletConfig = TabletConfigs.getDefault(companyId);
		}
		return tabletConfig;
	}

}
