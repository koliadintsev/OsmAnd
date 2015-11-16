package com.operasoft.snowboard.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.operasoft.snowboard.database.Geofence;
import com.operasoft.snowboard.database.GeofenceDao;
import com.operasoft.snowboard.util.Worker;

/**
 * This class is responsible for keeping track of all Geofences currently
 * active on Snowboard.
 * 
 * When a Geofence is added/removed/modified, the manager will notify other
 * components through the GeofenceEventListener interface
 * 
 * When a Geofence is entered/exited, the manager will notify other components
 * through the GeofenceActionListener interface
 */
public class GeofenceManager {

	/**
	 * Used by the EventNotifyWorker class
	 */
	private enum Event {
		ADDED,
		UPDATED,
		REMOVED
	}
	
	/**
	 * Used by the ActionNotifyWorker class
	 */
	private enum Action {
		ENTERED,
		EXITED,
		UPDATED
	}
	
	public enum Mode {
		DISABLED, 		// Geofences are not being tracked
		ACE_NORMAL,	    // We are spreading salt in normal mode
		ACE_BLAST,		// We are spreading salt in blast mode
		ACE_PAUSE,		// We are spreading salt, but we are paused.
		ACE_ERROR		// The ACE device is in error mode
	}

	private static final String TAG = "GeofenceManager";
	
	/**
	 * The singleton instance
	 */
	static private GeofenceManager instance_s;

	private Mode mode = Mode.DISABLED;
	private String aceMaterial = null;
	private int aceRate;
	private float aceWidthSpread;
	
	/**
	 * The list of Geofences we are currently in
	 */
	private Map<String, Geofence> currentGeofences = new LinkedHashMap<String, Geofence>();

	/**
	 * The listeners to invoke when a significant change is made to a Geofence.
	 */
	private List<GeofenceEventListener> eventListeners = Collections.synchronizedList(new ArrayList<GeofenceEventListener>());
	private EventNotifyWorker eventWorker = new EventNotifyWorker();

	/**
	 * The listeners to invoke when we enter/exit a geofence
	 */
	private List<GeofenceActionListener> actionListeners = Collections.synchronizedList(new ArrayList<GeofenceActionListener>());
	private ActionNotifyWorker actionWorker = new ActionNotifyWorker();
	
	private GeofenceDao geofenceDao = new GeofenceDao();

	/**
	 * Thread that constantly monitors for the list of Geofences we are currently in
	 * This thread is started when the first action listener is registered.
	 */
	private GeofenceMonitor monitor = new GeofenceMonitor();

	
	/**
	 * Singleton pattern. This makes sure we have only one instance of this class instanciated in the entire application.
	 */
	static public GeofenceManager getInstance() {
		if (instance_s != null) {
			return instance_s;
		}
		
		// Make sure one and only one instance can be created
		synchronized (GeofenceManager.class) {
			if (instance_s == null) {
				instance_s = new GeofenceManager();
			}
		}
		return instance_s;
	}

	/**
	 * Private constructor. Users of this class must call GeofenceManager.getInstance()
	 */
	private GeofenceManager() {
	}

	public Mode getMode() {
		return mode;
	}

	public String getAceMaterial() {
		return aceMaterial;
	}

	public int getAceRate() {
		return aceRate;
	}

	public float getAceWidthSpread() {
		return aceWidthSpread;
	}

	public void enterAceNormalMode(String material, int rate, float widthSpread) {
		aceMaterial = material;
		aceRate = rate;
		aceWidthSpread = widthSpread;
		setMode(Mode.ACE_NORMAL);
	}
	
	public void enterAceBlastMode(String material, int rate, float widthSpread) {
		aceMaterial = material;
		aceRate = rate;
		aceWidthSpread = widthSpread;
		setMode(Mode.ACE_BLAST);
	}
	
	public void enterAcePauseMode() {
		aceRate = 0;
		aceWidthSpread = 0;
		setMode(Mode.ACE_PAUSE);
	}

	public void enterAceErrorMode() {
		aceRate = 0;
		aceWidthSpread = 0;
		setMode(Mode.ACE_ERROR);
	}

	public void disableAceMode() {
		aceRate = 0;
		aceWidthSpread = 0;
		setMode(Mode.DISABLED);
	}
	
	private void setMode(Mode newMode) {
		Log.i(TAG, "Old: " + mode + ", New: " + newMode);
		mode = newMode;
		notifyActionListeners(null, Action.UPDATED);
	}
	
	/**
	 * Adds a new listener to receive notifications when a Geofence status changes
	 */
	public void addGeofenceEventListener(GeofenceEventListener listener) {
		if (!eventListeners.contains(listener)) {
			eventListeners.add(listener);
		}

		if ( !eventWorker.isRunning() ) {
			eventWorker.start();
		}
	}

	public void removeGeofenceEventListener(GeofenceEventListener listener) {
		eventListeners.remove(listener);
		
		if (eventListeners.isEmpty()) {
			eventWorker.stop();
		}
	}

	/**
	 * Adds a new listener to receive notifications when we enter/exits a Geofence
	 */
	public void addGeofenceActionListener(GeofenceActionListener listener) {
		if (!actionListeners.contains(listener)) {
			actionListeners.add(listener);
		}
		
		if (!actionWorker.isRunning()) {
			actionWorker.start();
			monitor.start();
		}

	}

	public void removeGeofenceActionListener(GeofenceActionListener listener) {
		actionListeners.remove(listener);
		
		if (actionListeners.isEmpty()) {
			actionWorker.stop();
			monitor.stop();
		}
	}
	
	/**
	 * Adds or updates a Geofence object to the list being monitored
	 * @param geofence
	 */
	public void addOrUpdateGeofence(Geofence geofence) {
		if (!geofenceDao.exists(geofence.getId())) {
			geofenceDao.insert(geofence);
			notifyEventListeners(geofence, Event.ADDED);
		} else {
			geofenceDao.replace(geofence);
			notifyEventListeners(geofence, Event.UPDATED);
		}
	}

	/**
	 * Removes a Geofence object from the list being monitored
	 * @param geofence
	 */
	public void removeGeofence(Geofence geofence) {
		if (geofenceDao.exists(geofence.getId())) {
			geofenceDao.remove(geofence.getId());
			notifyEventListeners(geofence, Event.REMOVED);
			
			if (currentGeofences.containsKey(geofence.getId())) {
				removeCurrentGeofence(geofence);
			}
		}
	}
	
	/**
	 * Adds a geofence to the list we are currently in
	 * @param geofence
	 */
	synchronized public void addCurrentGeofence(Geofence geofence) {
		if (!currentGeofences.containsKey(geofence.getId())) {
			currentGeofences.put(geofence.getId(), geofence);
			notifyActionListeners(geofence, Action.ENTERED);
		}
	}
	
	/**
	 * Removes a geofence to the list we are currently in
	 * @param geofence
	 */
	synchronized protected void removeCurrentGeofence(Geofence geofence) {
		if (currentGeofences.containsKey(geofence.getId())) {
			currentGeofences.remove(geofence.getId());
			notifyActionListeners(geofence, Action.EXITED);
		}
	}

	/**
	 * Updates the list of geofences we are currently in.
	 * @param ids
	 */
	synchronized public void updateCurrentGeofences(List<String> ids) {
		
		List<Geofence> removeList = new ArrayList<Geofence>();
		
		// Find out the ones we are no longer in
		for (Geofence geofence : currentGeofences.values()) {
			if (ids.contains(geofence.getId())) {
				// We are still in this one, no need to add it again later
				ids.remove(geofence.getId());
			} else {
				removeList.add(geofence);
			}
		}
		
		for (Geofence geofence : removeList) {
			removeCurrentGeofence(geofence);
		}
		
		// Add any new geofences we were not in before.
		for (String id : ids) {
			Geofence geofence = geofenceDao.getById(id);
			if (geofence != null) {
				addCurrentGeofence(geofence);
			}
		}
		
	}
	
	private void notifyEventListeners(Geofence geofence, Event event) {
		if (!eventListeners.isEmpty()) {
			eventWorker.add(geofence, event);
		}
	}
	
	private void notifyActionListeners(Geofence geofence, Action action) {
		if (!actionListeners.isEmpty()) {
			actionWorker.add(geofence, action);
		}
	}
	
	
	/**
	 * Worker thread used to notify event listeners that a Geofence has changed
	 * its status
	 * 
	 * @author Christian
	 */
	private class EventNotifyWorker extends Worker<Geofence, Event> {

		@Override
		protected void process(Geofence geofence, Event event) {
			synchronized (eventListeners) {
				for (GeofenceEventListener listener : eventListeners) {
					switch (event) {
						case ADDED:
							listener.geofenceAdded(geofence);
							break;
						case UPDATED:
							listener.geofenceUpdated(geofence);
							break;
						case REMOVED:
							listener.geofenceRemoved(geofence);
							break;
					}
				}
			}
		}
	}
	
	
	/**
	 * Worker thread used to notify action listeners that a Geofence has been 
	 * entered/exited
	 * 
	 * @author Christian
	 */
	private class ActionNotifyWorker extends Worker<Geofence, Action> {

		@Override
		protected void process(Geofence geofence, Action action) {
			synchronized (actionListeners) {
				for (GeofenceActionListener listener : actionListeners) {
					switch (action) {
						case ENTERED:
							listener.geofenceEntered(geofence);
							break;
						case EXITED:
							listener.geofenceExited(geofence);
							break;
						case UPDATED:
							listener.geofenceStatusUpdated(geofence);
					}
				}
			}
		}
	}
	
}
