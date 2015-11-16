package com.operasoft.snowboard.map.actions;

import android.os.Handler;

abstract public class AbstractComponentUpdateAction {

	/**
	 * This is the Handler used to post Geofence update requests
	 */
	private Handler uiHandler = new Handler();

	/**
	 * This is the method used to update the Geofence component from the UI thread.
	 */
	final private Runnable updateComponentWorker = new Runnable() {
		@Override
		public void run() {
			updateComponent();
		}
	};
	
	public void postComponentUpdate() {
		uiHandler.post(updateComponentWorker);
	}
	
	abstract protected void updateComponent();
}
