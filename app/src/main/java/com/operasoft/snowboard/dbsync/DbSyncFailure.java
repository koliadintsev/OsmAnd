package com.operasoft.snowboard.dbsync;

import java.util.Date;

public class DbSyncFailure {

	private String model;
	private int count;
	private Date lastFailure;
	private String reason;

	public DbSyncFailure(String model) {
		this.model = model;
		this.count = 1;
		this.lastFailure = new Date();
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void increment() {
		this.count++;
		lastFailure = new Date();
	}
	
	public Date getLastFailure() {
		return lastFailure;
	}

	public void setLastFailure(Date lastFailure) {
		this.lastFailure = lastFailure;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
}
