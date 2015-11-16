package com.operasoft.snowboard.engine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.osmand.plus.activities.MapActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.operasoft.snowboard.database.Callout;
import com.operasoft.snowboard.database.CompanyDao;
import com.operasoft.snowboard.database.MarkerInstallation;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceActivityDao;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.push.CalloutPushSync;
import com.operasoft.snowboard.dbsync.push.MarkerInstallationPushSync;
import com.operasoft.snowboard.dbsync.push.ServiceActivityPushSync;
import com.operasoft.snowboard.dbsync.push.ServiceLocationPushSync;
import com.operasoft.snowboard.util.DamageCustomDialogHandler;
import com.operasoft.snowboard.util.ForemanDailySheetDialogHandler;
import com.operasoft.snowboard.util.SaCompletedCustomDialog;
import com.operasoft.snowboard.util.Session;

/**
 * This class implements the default set of actions to perform when the driver selects an action
 * from the Osmand layer menu
 * 
 * @author Christian
 */
public class PointOfInterestActionHandler implements PointOfInterestActionListener {

	private Context context;
	private ServiceActivityPushSync saPush = ServiceActivityPushSync.getInstance();
	private MarkerInstallationPushSync miPush = MarkerInstallationPushSync.getInstance();
	private PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();
	private CalloutPushSync calloutPush = CalloutPushSync.getInstance();
	private ServiceActivityDao saDao = new ServiceActivityDao();

	public PointOfInterestActionHandler(Context context) {
		this.context = context;
	}

	@Override
	public void serviceActivityAccepted(PointOfInterest poi, ServiceActivity sa) {
		if (sa != null) {
			updateServiceActivity(sa, ServiceActivity.SA_ACCEPTED);
		}
	}

	@Override
	public void serviceActivityRefused(PointOfInterest poi, ServiceActivity sa) {
		if (sa != null) {
			updateServiceActivity(sa, ServiceActivity.SA_REJECTED);
		}
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void serviceActivityInDirection(PointOfInterest poi, ServiceActivity sa) {
		if (sa == null) {
			return;
		}
		// We can only have at most 1 SA in "En route" status at any given time for a vehicle.
		// If we already have one in that state, switch it back to "Accepted" status
		if (Session.getVehicle() != null) {
			List<ServiceActivity> enRouteList = saDao.listEnRoute(Session.getVehicle().getId());
			for (ServiceActivity dto : enRouteList) {
				updateServiceActivity(dto, ServiceActivity.SA_ACCEPTED);
			}
		} else {
			Log.e("PoiActionHandler", "Failed to identify the current vehicle while evaluating SA " + sa.getId());
		}

		if (Session.clocation != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			sa.setEnrouteLatitude(Session.clocation.getLatitude() + "");
			sa.setEnrouteLongitude(Session.clocation.getLongitude() + "");
			sa.setEnrouteTime(dateFormat.format(new Date()));
			updateServiceActivity(sa, ServiceActivity.SA_IN_DIRECTION);
		}
	}

	@Override
	public void serviceActivityCompleted(PointOfInterest poi, ServiceActivity sa) {
		if (sa != null) {
			updateServiceActivity(sa, ServiceActivity.SA_COMPLETED);
		}
	}

	@Override
	public void serviceActivityCreated(PointOfInterest poi, ServiceActivity sa) {
		SaCompletedCustomDialog dialog = new SaCompletedCustomDialog(context, poi, this, true);
		dialog.createDialog();
	}

	@Override
	public void calloutCreated(PointOfInterest poi, Callout callout) {
		calloutPush.pushData(context, callout);
	}

	@Override
	public void incidentCreated(PointOfInterest poi) {
		DamageCustomDialogHandler damageHandler = new DamageCustomDialogHandler(context, poi);
		damageHandler.createDialog();
	}

	@Override
	public void goBackTriggered(PointOfInterest poi) {
		poiMgr.markServiceLocationAsGoBack(poi);
	}

	@Override
	public void goToTriggered(PointOfInterest poi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void goBackCancelTriggered(PointOfInterest poi) {
		poiMgr.markServiceLocationAsCompleted(poi);
	}

	@Override
	public void serviceLocationCompleted(PointOfInterest poi) {
		poiMgr.markServiceLocationAsCompleted(poi);
		ServiceLocationPushSync slPush = new ServiceLocationPushSync();
		ServiceLocationDao slDao = new ServiceLocationDao();
		
		ServiceLocation sl = slDao.getById(poi.getSlId());
		if (sl != null) {
			slPush.pushData(context, sl);
		} else {
			Log.e("PoiActionHandler", "Failed to find SL for POI " + poi.getId() + " - SL ID " + poi.getSlId());
		}
	}

	@Override
	public void markerInstalled(PointOfInterest poi) {
		MarkerInstallation mi = poi.getCurrentMarkerInstallation();
		if (mi == null) {
			Log.e("PoiActionHandler", "Failed to find MI for POI " + poi.getId());
			return;
		}
		mi.setStatus(MarkerInstallation.INSTALLED_CASE);
		if (Session.getDriver() != null) {
			mi.setUserId(Session.getDriver().getId());
		}
		mi.setDateTime(CommonUtils.UtcDateNow());

		// Push the update to the server (and our local database)
		miPush.pushData(context, mi);

		// Update the POI manager
		poiMgr.detachMarkerInstallation(mi);

	}

	public void updateServiceActivity(ServiceActivity sa, String statusCodeId) {
		// Take ownership of the SA (if we are not rejecting it)
		if (!statusCodeId.equals(ServiceActivity.SA_REJECTED)) {
			if (Session.getDriver() != null) {
				sa.setUserId(Session.getDriver().getId());
			} else {
				Log.e("PoiActionHandler", "Session.driver is NULL");
			}
			if (Session.getVehicle() != null) {
				sa.setVehicleId(Session.getVehicle().getId());
			} else {
				Log.e("PoiActionHandler", "Session.vehicle is NULL");
			}
		}
		sa.setStatus(statusCodeId, true);

		// Push the update to the server (and our local database)
		saPush.pushData(context, sa);

		try {
			// Update the POI manager
			if ((statusCodeId.equals(ServiceActivity.SA_COMPLETED)) || (statusCodeId.equals(ServiceActivity.SA_REJECTED))) {
				poiMgr.removeServiceActivity(sa);
			} else {
				poiMgr.updateServiceActivity(sa);
			}
		} catch (RuntimeException re) {
			Log.e("PoiActionHandler", "Failed to update SA " + sa.getId() + " to status " + statusCodeId, re);
		}
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void serviceLocationToserviceActivityEnroute(PointOfInterest poi) {
		ServiceActivity serviceActivity;
		if (poi.getCurrentServiceActivity() == null) {
			serviceActivity = new ServiceActivity();
			CompanyDao dao = new CompanyDao();
			serviceActivity.setCompanyId(dao.getCompanyId());
			try {
				serviceActivity.setContractId(poi.getContract().getId());
			} catch (NullPointerException e) {
				Toast.makeText(context, "No Contract found for this Location", Toast.LENGTH_LONG).show();
				return;
			}
			serviceActivity.setDateTime(CommonUtils.UtcDateNow());

			if (Session.getDriver() != null) {
				serviceActivity.setUserId(Session.getDriver().getId());
			} else {
				Log.w("New SA", "Session.driver is NULL");
			}
			if (Session.getVehicle() != null) {
				serviceActivity.setVehicleId(Session.getVehicle().getId());
			} else {
				Log.w("New SA", "Session.vehicle is NULL");
			}

			serviceActivity.setServiceLocationId(poi.getSlId());

			// We can only have at most 1 SA in "En route" status at any given time for a vehicle.
			// If we already have one in that state, switch it back to "Accepted" status
			if (Session.getVehicle() != null) {
				List<ServiceActivity> enRouteList = saDao.listEnRoute(Session.getVehicle().getId());
				for (ServiceActivity dto : enRouteList)
					updateServiceActivity(dto, ServiceActivity.SA_ACCEPTED);
			} else {
				Log.e("PoiActionHandler", "Failed to identify the current vehicle while evaluati");
			}

		} else {
			serviceActivity = poi.getCurrentServiceActivity();
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		serviceActivity.setStatus(ServiceActivity.SA_IN_DIRECTION, true);

		// TODO QUESTION is this fix acceptable
		if (Session.clocation != null) {
			serviceActivity.setEnrouteLatitude(Session.clocation.getLatitude() + "");
			serviceActivity.setEnrouteLongitude(Session.clocation.getLongitude() + "");
			serviceActivity.setEnrouteTime(dateFormat.format(new Date()));
			serviceActivity.setStatus(ServiceActivity.SA_IN_DIRECTION);

			poi.attachServiceLocation(serviceActivity.getServiceLocation());
			ServiceActivityPushSync saPushSync = ServiceActivityPushSync.getInstance();
			saPushSync.pushData(context, serviceActivity);
		} else
			Toast.makeText(context, "Current location unknown", Toast.LENGTH_SHORT).show();

		context.startActivity(new Intent(context, MapActivity.class));
	}

	@Override
	public void ForemanDaily(PointOfInterest poi) {
		ForemanDailySheetDialogHandler FDSheetHandler;
		FDSheetHandler = new ForemanDailySheetDialogHandler(context, poi);
		FDSheetHandler.createDialog();
	}

	public void serviceLocationCompletedNow(PointOfInterest mPoi) {
		poiMgr.markServiceLocationAsCompletedNow(mPoi);

	}
}