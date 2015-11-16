package com.operasoft.snowboard.dbsync.periodic;

import org.json.JSONArray;
import org.json.JSONObject;

import com.operasoft.snowboard.database.Company;
import com.operasoft.snowboard.database.CompanyDao;
import com.operasoft.snowboard.util.Session;

public class CompanyPeriodicSync extends DefaultPeriodicSync {

	public CompanyPeriodicSync() {
		super("Company", new CompanyDao());
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			JSONArray recordKey = jsonObject.names();
			for (int j = 0; j < recordKey.length(); j++) {
				String jsonModel = recordKey.getString(j);
				JSONObject js = jsonObject.getJSONObject(jsonModel);
				dao.insertOrReplace(js);
				Company company = (Company) dao.buildDto(js);
				if (Session.getCompany().equals(company)) {
					Session.setCompany(company);
				}
			}
		}
	}
	
	
}
