package com.operasoft.snowboard.engine;

import java.util.ArrayList;
import java.util.List;

import com.operasoft.geom.Point;
import com.operasoft.geom.Polygon;
import com.operasoft.geom.PolygonFinder;
import com.operasoft.geom.SimplePolygonFinder;
import com.operasoft.snowboard.database.Geofence;
import com.operasoft.snowboard.database.GeofenceDao;
import com.operasoft.snowboard.util.Session;

public class GeofenceMonitor implements Runnable, GeofenceEventListener {

	private PolygonFinder finder = new SimplePolygonFinder();
	private boolean running = false;
	private boolean dirty = false;

	/**
	 * Starts the worker thread if it is not already running
	 */
	synchronized public void start() {
		if (running == false) {
			running = true;
			(new Thread(this)).start();
		}
	}

	/**
	 * Stops the worker thread if it is not already stopped.
	 */
	synchronized public void stop() {
		if (running) {
			running = false;
			notifyAll();
		}
	}

	@Override
	public void geofenceAdded(Geofence geofence) {
		Polygon polygon = Polygon.buildFromString(geofence.getId(), geofence.getGeom());
		if (polygon != null) {
			synchronized (this) {
				finder.add(polygon);
				dirty = true;
			}
		}
	}

	@Override
	public void geofenceUpdated(Geofence geofence) {
		synchronized (this) {
			finder.remove(geofence.getId());
			dirty = true;
		}

		Polygon polygon = Polygon.buildFromString(geofence.getId(), geofence.getGeom());
		if (polygon != null) {
			synchronized (this) {
				finder.add(polygon);
				dirty = true;
			}
		}
	}

	@Override
	public void geofenceRemoved(Geofence geofence) {
		synchronized (this) {
			finder.remove(geofence.getId());
			dirty = true;
		}
	}

	@Override
	public void run() {
		initFinder();
		Point lastPoint = null;

		// Since this thread is starting, make sure we do receive geofence events
		GeofenceManager.getInstance().addGeofenceEventListener(this);

		while (running) {

			long start = System.currentTimeMillis();

			if (Session.clocation != null) {
				Point point = new Point(Session.clocation.getLatitude(), Session.clocation.getLongitude());

				// Only scans the polygon list if we have changed location since last time or 
				// if the list of polygons has been updated (dirty flag)
				if (dirty || (lastPoint == null) || (!point.equals(lastPoint))) {
					// TODO This algorithm can be further optimized by looking at the
					//      polygons we were in the previous pass and see if we are still in them
					//      This would avoid the need to scan the entire list again
					List<Polygon> polygons;
					synchronized (this) {
						polygons = finder.findAll(point);
						dirty = false;
					}
					List<String> ids = new ArrayList<String>();
					for (Polygon polygon : polygons) {
						ids.add(polygon.getId());
					}
					GeofenceManager.getInstance().updateCurrentGeofences(ids);
					lastPoint = point;
				}
			}

			long end = System.currentTimeMillis();
			long delay = 1000 - (end - start);
			if (delay < 250) {
				delay = 250;
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException ie) {
			}
		}

		// Since this thread is stopping, we should no longer receive geofence events
		GeofenceManager.getInstance().removeGeofenceEventListener(this);
	}

	private void initFinder() {
		List<Polygon> list = new ArrayList<Polygon>();

		GeofenceDao geofenceDao = new GeofenceDao();

		for (Geofence geofence : geofenceDao.listAllValid()) {
			Polygon polygon = Polygon.buildFromString(geofence.getId(), geofence.getGeom());
			if (polygon != null) {
				list.add(polygon);
			}
		}

		synchronized (this) {
			finder.init(list);
		}
	}

}