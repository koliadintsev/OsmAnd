package com.operasoft.snowboard.dbsync.onetime;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.dbsync.NetworkUtilities;
import com.operasoft.snowboard.dbsync.Utils;
import com.operasoft.snowboard.util.Session;

public class DefaultOneTimeSync extends AbstractOneTimeSync {

	public DefaultOneTimeSync(String model, Dao dao) {
		super(model, dao, false);
	}

	public DefaultOneTimeSync(String model, Dao dao, boolean push) {
		super(model, dao, push);
	}

	@Override
	protected List<NameValuePair> buildRequestParams() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// Default queries require the following parameters:
		//	- imei:   To identify the tablet
		//  - pin:    To identify the user
		//  - model:  The data we want to query
		//  - action: The action to invoke (always "index" for periodic updates)
		params.add(imeiParam);
		if (pinParam != null) {
			params.add(pinParam);
		}
		params.add(modelParam);
		params.add(actionParam);
		// Limit the queries to 100 entries.
		if (limitParam != null) {
			params.add(limitParam);
		}

		params.add(companyParam);
		
		return params;
	}

	/**
	 * This method is intended to be overriden by subclasses to process 
	 * specific changes related to the data received. 
	 * 
	 * As an example, if a service activity has been removed or assigned to
	 * someone else, this is where the subclass needs to detect it and handle
	 * it properly (e.g. detach it from the POI list.
	 * 
	 * This method performs the required inserts/updates to the local database
	 * 
	 * @param jsArray The list of JSON objects retrieved from Snowman
	 */
	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		
		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			JSONArray recordKey = jsonObject.names();
			for (int j = 0; j < recordKey.length(); j++) {
				String jsonModel = recordKey.getString(j);
				JSONObject js = jsonObject.getJSONObject(jsonModel);
				dao.insertOrReplace(js);
			}
		}
	}
}
