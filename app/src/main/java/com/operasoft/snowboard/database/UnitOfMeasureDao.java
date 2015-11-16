package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

/**
 * @author dounaka
 *
 */
public class UnitOfMeasureDao extends Dao<UnitOfMeasure> {
	public UnitOfMeasureDao() {
		super("sb_unit_of_measures");
	}

	@Override
	protected UnitOfMeasure buildDto(Cursor cursor) {
		UnitOfMeasure uom = new UnitOfMeasure();
		uom.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		uom.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		uom.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		uom.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		uom.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		uom.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));
		return uom;
	}

	@Override
	public UnitOfMeasure buildDto(JSONObject json) throws JSONException {
		UnitOfMeasure uom = new UnitOfMeasure();
		uom.setId(jsonParser.parseString(json, "id"));
		uom.setCompanyId(jsonParser.parseString(json, "company_id"));
		uom.setName(jsonParser.parseString(json, "name"));
		uom.setCreated(jsonParser.parseDate(json, "created"));
		uom.setModified(jsonParser.parseDate(json, "modified"));
		return uom;
	}

	@Override
	public void insert(UnitOfMeasure dto) {
		insertDto(dto);

	}

	@Override
	public void replace(UnitOfMeasure dto) {
		replaceDto(dto);
	}

}
