package com.operasoft.snowboard.dbsync.onetime;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import com.operasoft.snowboard.database.ImeiCompany;
import com.operasoft.snowboard.dbsync.NetworkUtilities;
import com.operasoft.snowboard.dbsync.Utils;
import com.operasoft.snowboard.util.Config;
import com.operasoft.snowboard.util.Session;

public class ImeiCompanyOneTimePush extends DefaultOneTimeSync {

	public ImeiCompanyOneTimePush() {
		super("ImeiCompany", null, true);
		actionParam = new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "update");
	}

	@Override
	protected List<NameValuePair> buildRequestParams() {
		List<NameValuePair> params = super.buildRequestParams();
		ImeiCompany info = Session.getImeiCompany();
		
		if (info == null) {
			throw new RuntimeException("Failed to retrieve ImeiCompany from Session");
		}
		
		StringBuilder notes = new StringBuilder();
		notes.append("URL: " + Config.getBaseUrl() + "\n");
		notes.append("DB: " + Config.getDbName() + "\n");
		notes.append("Collector: " + Config.getCollectorAddress() + ":" + Config.getCollectorPort() + "\n");
		notes.append("AutoUpdate: " + (Config.isAutoUpdateEnabled() ? "Enabled" : "Disabled") + "\n");
		
		params.add(new BasicNameValuePair(model + "[id]", info.getId()));
		//params.add(new BasicNameValuePair(model + "[company_id]", info.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[config]", Config.CURRENT_CONFIG.toString()));
		params.add(new BasicNameValuePair(model + "[version]", String.valueOf(Config.getVersion())));
		params.add(new BasicNameValuePair(model + "[notes]", notes.toString()));

		return params;
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		// Nothing to do
	}

}
