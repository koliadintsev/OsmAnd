package com.operasoft.snowboard.dbsync.push;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.VehiclesDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

public class VehiclePushSync extends AbstractPushSync<Vehicle> {

	private JsonDtoParser			dtoParser	= new JsonDtoParser();
	private VehiclesDao				dao			= null;

	// Singleton pattern
	static private VehiclePushSync	instance_s	= new VehiclePushSync();

	static public VehiclePushSync getInstance() {
		if (instance_s == null) {
			instance_s = new VehiclePushSync();
		}

		return instance_s;
	}

	public VehiclePushSync() {
		super("Vehicle");
	}

	@Override
	protected Dao<Vehicle> getDao() {
		if (dao == null) {
			dao = new VehiclesDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, Vehicle dto) {
		params.remove(actionParam);
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "edit"));
		params.add(new BasicNameValuePair(model + "[id]", dto.getId()));
		params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompany_id()));
		params.add(new BasicNameValuePair(model + "[trailer_id]", dto.getTrailerId()));
	}

	@Override
	protected boolean processServerResponse(String value, Vehicle dto) throws JSONException {
		return true;
	}

}
