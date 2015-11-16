package com.operasoft.snowboard.database;

import java.util.ArrayList;

public class RouteDetails {
	private String id;
	private String customerId;
	private String name;
	private String status_code;
	private String created;
	private ArrayList<RouteSequence> routeSequences;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus_code() {
		return status_code;
	}

	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public ArrayList<RouteSequence> getRouteSequences() {
		return routeSequences;
	}

	public void setRouteSequences(ArrayList<RouteSequence> routeSequences) {
		this.routeSequences = routeSequences;
	}
}
