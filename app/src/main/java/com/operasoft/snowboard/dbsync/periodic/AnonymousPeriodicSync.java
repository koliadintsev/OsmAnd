package com.operasoft.snowboard.dbsync.periodic;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

/**
 * This class implements the synchronization process used for tables that must be synchronized upon start of the application, before the
 * user is logged in.
 * 
 * @author Christian
 * 
 */
public class AnonymousPeriodicSync extends DefaultPeriodicSync {

	public AnonymousPeriodicSync(String model, Dao dao) {
		super(model, dao, false);
		// We need to override a few things up since we invoke a different API
		this.url = NetworkUtilities.AUTH_URI_SYNC;
		this.snowmanModel = "Sb" + model;
	}

	/**
	 * For this kind of initialization, we need less parameters than the default one.
	 */
	@Override
	protected ArrayList<NameValuePair> buildRequestParams() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		// Default queries require the following parameters:
		// - imei: To identify the tablet
		// - model: The data we want to query
		// - action: The action to invoke (always "index" for periodic updates)
		params.add(imeiParam);
		params.add(modelParam);
		// params.add(actionParam);
		// Limit the queries to 100 entries.
		params.add(limitParam);

		return params;
	}

}
