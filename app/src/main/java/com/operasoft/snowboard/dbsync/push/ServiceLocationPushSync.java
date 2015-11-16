package com.operasoft.snowboard.dbsync.push;

import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.LoginSession;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;
import com.operasoft.snowboard.util.Session;

public class ServiceLocationPushSync extends AbstractPushSync<ServiceLocation> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private ServiceLocationDao dao = new  ServiceLocationDao();

	static private ServiceLocationPushSync instance_sm = new ServiceLocationPushSync();

	static public ServiceLocationPushSync getInstance() {
		if (instance_sm == null) {
			instance_sm = new ServiceLocationPushSync();
		}
		return instance_sm;
	}

	public ServiceLocationPushSync() {
		super("ServiceLocation");
	}

	@Override
	protected Dao<ServiceLocation> getDao() {
		if (dao == null) {
			dao = new ServiceLocationDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params,
			ServiceLocation dto) {
		params.remove(actionParam);
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "edit"));
		params.add(new BasicNameValuePair(model + "[id]", dto.getId()));
		params.add(new BasicNameValuePair(model + "[last_visit_date]", new Date()+""));
		params.add(new BasicNameValuePair(model + "[company_id]", Session.getCompanyId()));
	}

	@Override
	protected boolean processServerResponse(String value, ServiceLocation dto) throws JSONException {
		return true;
	}

	@Override
	protected void saveDirtyDto(ServiceLocation dto) {
		dao.markAsDirty(dto);
	}

	@Override
	protected void saveClearDto(ServiceLocation dto) {
		dao.insertOrReplace(dto);
	}

	@Override
	protected void clearDirtyDto(ServiceLocation dto) {
		dao.clearDirtyDto(dto);
	}

}
