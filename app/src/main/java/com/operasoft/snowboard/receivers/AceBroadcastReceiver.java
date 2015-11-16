package com.operasoft.snowboard.receivers;

import com.operasoft.snowboard.engine.GeofenceManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * This class is responsible for handling intents sent by the ACE Salt 
 * Android application and configure the GeofenceManager accordingly
 * @author Christian
 *
 */
public class AceBroadcastReceiver extends BroadcastReceiver {

	static final private String TAG = "SaltBroadcastReceiver";

	static final private String MATERIAL_KEY = "material";
	static final private String RATE_KEY = "rate";
	static final private String WIDTH_KEY = "width";
	
	public AceBroadcastReceiver() {
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {
		String action = intent.getAction();

		GeofenceManager geofenceManager = GeofenceManager.getInstance();
		Bundle params = intent.getExtras();
		
		if (params != null) {
			Log.i(TAG, "Received action: " + action + ", material= " + params.getString(MATERIAL_KEY) + ", rate= " + params.getInt(RATE_KEY) + ", width= " + params.getFloat(WIDTH_KEY));
		} else {
			Log.i(TAG, "Received action: " + action);
		}
		
		if (action.equals("com.operasoft.ace.action.ACE_ENABLED")) {
			// The ACE device has been connected. Let's wait until we know which
			// mode it is in...
		} else if (action.equals("com.operasoft.ace.action.ACE_DISABLED")) {
			// The ACE device is no longer available...
			geofenceManager.disableAceMode();
		} else if (action.equals("com.operasoft.ace.action.BLAST_MODE")) {
			if (!params.containsKey(RATE_KEY) || !params.containsKey(WIDTH_KEY)) {
				Log.e(TAG, "Missing parameters for action " + action + ", intent: " + intent);
			} else {
				String material = params.getString(MATERIAL_KEY);
				int rate = params.getInt(RATE_KEY);
				float width = params.getFloat(WIDTH_KEY);
				
				geofenceManager.enterAceBlastMode(material, rate, width);
			}
		} else if (action.equals("com.operasoft.ace.action.NORMAL_MODE")) {
			if (!params.containsKey(RATE_KEY) || !params.containsKey(WIDTH_KEY)) {
				Log.e(TAG, "Missing parameters for action " + action + ", intent: " + intent);
			} else {
				String material = params.getString(MATERIAL_KEY);
				int rate = params.getInt(RATE_KEY);
				float width = params.getFloat(WIDTH_KEY);
				
				geofenceManager.enterAceNormalMode(material, rate, width);
			}
		} else if (action.equals("com.operasoft.ace.action.PAUSE_MODE")) {
			geofenceManager.enterAcePauseMode();
		} else if (action.equals("com.operasoft.ace.action.ERROR_MODE")) {
			geofenceManager.enterAceErrorMode();
		} else {
			Log.w(TAG, "Unknown action received: " + action + ", intent: " + intent.toString());
		}
	}
}
