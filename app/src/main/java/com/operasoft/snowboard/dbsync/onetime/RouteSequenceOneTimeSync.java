package com.operasoft.snowboard.dbsync.onetime;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.operasoft.snowboard.database.RouteSequenceDao;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

public class RouteSequenceOneTimeSync extends DefaultOneTimeSync {

	private String routeId;
	
	public RouteSequenceOneTimeSync(String routeId) {
		super("RouteSequence", new RouteSequenceDao());
		this.routeId = routeId;
	}

	@Override
	protected List<NameValuePair> buildRequestParams() {
		List<NameValuePair> params = super.buildRequestParams();
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[conditions][" + snowmanModel + ".route_id]", routeId));
		
		return params;
	}
	
	
}
