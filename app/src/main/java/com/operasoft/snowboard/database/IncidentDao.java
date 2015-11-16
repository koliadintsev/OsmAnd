package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class IncidentDao extends Dao<Incident> {

	public IncidentDao() {
		super("sb_incidents");
	}

	@Override
	public void insert(Incident dto) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void replace(Incident dto) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	protected Incident buildDto(Cursor cursor) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public Incident buildDto(JSONObject json) throws JSONException {
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
