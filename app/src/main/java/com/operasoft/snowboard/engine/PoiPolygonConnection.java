package com.operasoft.snowboard.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.util.Log;

import com.operasoft.geom.Polygon;
import com.operasoft.geom.PolygonDetector;
import com.operasoft.snowboard.database.Polygonable;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.engine.PointOfInterestManager.StatusListener;

/**
 * @author dounaka
 *
 */
class PoiPolygonConnection {

	private static final String TAG = "polygon";
	private PointOfInterestManager mManager;

	private PolygonDetector mPolygonDetector;

	private PointOfInterestEventListener mEventListener;

	public PoiPolygonConnection(PointOfInterestManager manager) {
		mManager = manager;
	}

	private <T extends Polygonable> List<Polygon> convert(Collection<T> polygonables) {
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();
		for (Polygonable polygonable : polygonables) {
			Polygon p = convertString(polygonable.getId(), polygonable.getPolygon());
			if (p != null) {
				p.name = polygonable.getName();
				polygons.add(p);
			} else
				Log.e(TAG, "Service location with no polygon " + polygonable.getId());
		}
		return polygons;
	}

	private Polygon convertString(String id, String polygonString) {
		if (polygonString == null)
			return null;
		Polygon p = Polygon.buildFromString(id, polygonString);
		return p;
	}

	//TODO 00 refactoring, not very clean design
	void connect(final PolygonDetector polygonDetector, final StatusListener externalListener) {
		mPolygonDetector = polygonDetector;
		mEventListener = new PointOfInterestEventListener() {
			@Override
			public void onPoiListReloaded(Collection<PointOfInterest> activePois) {
				if (!mManager.isModeNone())
					mPolygonDetector.setPolygons(convert(activePois));
			}

			@Override
			public void poiAdded(PointOfInterest poi) {
				mPolygonDetector.add(Polygon.buildFromString(poi.getId(), poi.getPolygon()));
			}

			@Override
			public void poiRemoved(PointOfInterest poi) {
				mPolygonDetector.remove(Polygon.buildFromString(poi.getId(), poi.getPolygon()));
			}

			@Override
			public void poiModified(PointOfInterest poi) {
			}
		};
		mManager.addPoiEventListener(mEventListener);
		mManager.statuslistener = new StatusListener() {
			@Override
			public void onNewServiceLocationUpdate(ServiceLocation sl) {
				mPolygonDetector.add(Polygon.buildFromString(sl.getId(), sl.getPolygon()));
				externalListener.onNewServiceLocationUpdate(sl);
			}

			@Override
			public void onAssistedMode() {
				Log.d(TAG, "change to assisted mode");
				externalListener.onAssistedMode();
				// no need to reload list done in mEventListener#poiListReloaded 
				// mPolygonDetector.setPolygons(convert(mManager.listActivePois()));
			}

			@Override
			public void onNoneMode() {
				Log.d(TAG, "change to NONE mode");
				ServiceLocationDao servicelDao = new ServiceLocationDao();
				mPolygonDetector.setPolygons(convert(servicelDao.listAll()));
				externalListener.onNoneMode();
			}
		};

	}

	void disconnect() {
		mManager.removePoiEventListener(mEventListener);
		mManager.statuslistener = null;

	}
}
