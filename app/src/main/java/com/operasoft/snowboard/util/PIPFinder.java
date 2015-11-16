package com.operasoft.snowboard.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.views.OsmandMapTileView;
import android.view.View;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.DropEmployeesDao;
import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterest.PoiStatus;
import com.operasoft.snowboard.engine.PointOfInterestActionHandler;
import com.operasoft.snowboard.engine.PointOfInterestActionListener;
import com.operasoft.snowboard.engine.PointOfInterestEventListener;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.maplayers.PointOfInterestMenu;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;

/**
 * This scans a list of POIs to find the ones we are currently in
 * 
 * NOTE: This class should be migrated to use the PolygonFinder approach in
 * order to improve its efficiency and have a common algorithm for finding the
 * polygons we are currently in.
 */
public class PIPFinder implements PointOfInterestEventListener {

	private MapActivity mMapActivity;
	private TextView driverCommentText;
	private boolean isPip = false;
	private double nlat[];
	private double nlon[];
	private List<TIT_RoutePoint> nodes;
	private PointOfInterestActionListener actionListener;
	private View mHandler;
	private boolean mStopHandler;
	private int pipCount, counter = 0;
	private String tempId = "";
	protected PointOfInterest tempPoi;
	private List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
	private List<PointOfInterest> poiListInactive = new ArrayList<PointOfInterest>();
	private PointOfInterest mCurrentPOI;
	PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();

	// -------------------------------------------------------------------------------
	// IMPORTANT: The following fields can be accessed by 2 different threads.
	//            Hence, their manipulation must always be synchronized
	// -------------------------------------------------------------------------------
	private List<PointOfInterest> newList = new ArrayList<PointOfInterest>();
	private List<PointOfInterest> obsoleteList = new ArrayList<PointOfInterest>();
	private boolean reloadList = false;
	// -------------------------------------------------------------------------------
	private boolean mStopHandlerInactive;
	private PointOfInterest tempPoiInactive = null;
	private int counterInactive;
	private Runnable runnableInactive;
	private View mHandlerInactive;

	public PIPFinder(MapActivity map, View view) {
		this.mMapActivity = map;
		mHandler = view;
		mHandlerInactive = view;
		actionListener = new PointOfInterestActionHandler(mMapActivity);

		driverCommentText = (TextView) mMapActivity.findViewById(R.id.drivercomment);
	}

	/**
	 * Will start a thread to find if Driver is within a Polygon or not
	 * View.post() runs on UI Thread
	 */
	public void findPIP() {
		mStopHandler = false;
		tempPoi = null;
		Runnable runnable;

		// Counter will avoid multiple instances of thread
		if (++counter < 2) {
			runnable = new Runnable() {
				@Override
				public void run() {
					if (!mStopHandler) {
						tempPoi = getPoiUpdateUI();

						if (tempPoi != null) {
							if (tempId.equals(tempPoi.getId()))
								pipCount++;
							else
								pipCount = 0;

							if ((pipCount == 10) && (!tempPoi.getStatus().equals(PoiStatus.SERVICE_LOCATION_COMPLETED)) && (!tempPoi.getStatus().equals(PoiStatus.SERVICE_LOCATION_COMPLETED))) {
								actionListener.serviceLocationCompleted(tempPoi);
							}
							tempId = tempPoi.getId();
						} else {
							pipCount = 0;
						}
					}
					//TODO 00 question why 1000 ms ??
					mHandler.postDelayed(this, 1000);
				}
			};

			mHandler.post(runnable);
		}
	}

	private PointOfInterest getPoiUpdateUI() {
		updatePoiList();

		if (tempPoi != null) {
			if (inPip(tempPoi) != null)
				return tempPoi;
		}

		for (final PointOfInterest pointOfInterest : poiList) {
			if (inPip(pointOfInterest) != null) {
				return pointOfInterest;
			}
		}
		return null;
	}

	private void updatePoiList() {
		synchronized (this) {
			if (reloadList) {
				poiList.clear();
			} else {
				for (PointOfInterest poi : obsoleteList) {
					poiList.remove(poi);
				}
			}
			for (PointOfInterest poi : newList) {
				poiList.add(poi);
			}
		}
	}

	private PointOfInterest inPip(final PointOfInterest pointOfInterest) {
		nodes = CommonUtils.getPolyNodes(pointOfInterest.getPolygon());
		nlat = new double[nodes.size()];
		nlon = new double[nodes.size()];

		for (int i = 0; i < nodes.size(); i++) {
			nlat[i] = nodes.get(i).getLatitude();
			nlon[i] = nodes.get(i).getLongitude();
		}

		isPip = CommonUtils.isPointInPolygon(nlon, nlat, nodes.size(), Session.clocation == null ? 0.0f : (float) Session.clocation.getLongitude(), Session.clocation == null ? 0.0f
				: (float) Session.clocation.getLatitude());

		mMapActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (isPip) {
					driverCommentText.setText(pointOfInterest.getDriverComments());
					driverCommentText.setVisibility(View.VISIBLE);
				} else {
					driverCommentText.setVisibility(View.GONE);
				}
			}
		});

		if (isPip) {
			isPip = false;
			//TODO 0000 Reprendre la logique onEnter/exit
			mCurrentPOI = pointOfInterest;
			poiMgr.onPoiEnter(pointOfInterest);
			if (!Session.serLocCrossed.contains(pointOfInterest.getId())) {
				Session.serLocCrossed.add(pointOfInterest.getId());
				PIPMediaPlayer pipMedia = new PIPMediaPlayer(mMapActivity);
				pipMedia.player();
				try {
					DropEmployeesDao dropEmployeesDao = new DropEmployeesDao();
					if (pointOfInterest.getCurrentServiceActivity().getStatus().equals(ServiceActivity.SA_IN_DIRECTION)) {
						if (dropEmployeesDao.isDropped(pointOfInterest.getCurrentServiceActivity().getServiceLocationId())) {
							TaskListCustomDialog dialog = new TaskListCustomDialog(mMapActivity, pointOfInterest);
							dialog.createDialog();
						} else {
							OsmandMapTileView view = new OsmandMapTileView(mMapActivity);
							PointOfInterestMenu menu = new PointOfInterestMenu(actionListener, view);
							pointOfInterest.attachServiceLocation(poiMgr.routeSlList.get(0));
							if (Session.route.getPopUp().equals(Route.SHOW_POPUP)) {
								menu.createDialog(pointOfInterest);
			}
						}
					}
				} catch (Exception e) {
				}
			}

			return pointOfInterest;
		} else {
			if (Session.serLocCrossed.contains(mCurrentPOI))
				Session.serLocCrossed.remove(mCurrentPOI);
			if (Session.route == null) {
				if (mCurrentPOI != null)
					if (mCurrentPOI.getCurrentServiceActivity() == null)
						poiMgr.onPoiExit(mCurrentPOI);
		}
		}
		return null;
	}

	@Override
	public void poiAdded(PointOfInterest poi) {
		synchronized (this) {
			newList.add(poi);
		}
	}

	@Override
	public void poiModified(PointOfInterest poi) {
		// Nothing to do
	}

	@Override
	public void poiRemoved(PointOfInterest poi) {
		synchronized (this) {
			obsoleteList.remove(poi);
		}
	}

	@Override
	public void onPoiListReloaded(Collection<PointOfInterest> activePois) {
		synchronized (this) {
			// We have received a new list of POIs... let's update it properly.
			reloadList = true;
			newList.clear();
			obsoleteList.clear();
			for (PointOfInterest poi : activePois) {
				newList.add(poi);
			}
		}
	}

	/**
	 * Will start a thread to check if Driver is within a Polygon or not even if
	 * POI is not active
	 */
	public void findPIPInactive() {
		poiListInactive.clear();
		mStopHandlerInactive = false;
		tempPoiInactive = null;

		// Counter will avoid multiple instances of thread
		if (++counterInactive < 2) {
			runnableInactive = new Runnable() {
				private String tempIdInactive = "";

				@Override
				public void run() {
					if (!mStopHandlerInactive) {
						tempPoiInactive = getPoiUpdateUIInactive();

						if (tempPoiInactive != null) {
							if (tempIdInactive.equals(tempPoiInactive.getId())) {
								// TODO: show current SL as it is
							} else {
								// TODO: sl changed move to new ne
							}

							tempIdInactive = tempPoiInactive.getId();
						} else {
							// TODO: remove poi from UI
						}
					}
					//TODO 00 why 1000ms??
					mHandlerInactive.postDelayed(this, 1000);
				}
			};

			mHandlerInactive.post(runnableInactive);
		}
	}

	private PointOfInterest getPoiUpdateUIInactive() {
		if (Session.route == null)
			updatePoiListInactive();
		for (final PointOfInterest pointOfInterest : poiListInactive) {
			if (inPip(pointOfInterest) != null) {
				return pointOfInterest;
			}
		}
		return null;
	}

	/**
	 * Scans all service locations and adds POI to inactive POI list
	 */
	private void updatePoiListInactive() {
		synchronized (this) {
			ServiceLocationDao slDao = new ServiceLocationDao();
			List<ServiceLocation> list = new ArrayList<ServiceLocation>();
			try {
				if (Session.getUserPin() != null)
					list = slDao.listAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
			poiListInactive = new ArrayList<PointOfInterest>();
			for (ServiceLocation sl : list) {
				if (sl != null && sl.getPolygon() != null && sl.getPolygon().trim().length() > 0) {
					PointOfInterest poi = poiMgr.getPOI(sl.getId());
					if (poi == null) {
						poi = new PointOfInterest(sl.getId());
						poi.attachServiceLocation(sl);
					}
					poiListInactive.add(poi);
				}
			}
		}
	}
}