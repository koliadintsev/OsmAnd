package com.operasoft.snowboard.events;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.database.LoginSession;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UserWorkStatusLogs;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.DbSyncManager;
import com.operasoft.snowboard.dbsync.onetime.ImeiCompanyOneTimePush;
import com.operasoft.snowboard.dbsync.push.LoginSessionPushSync;
import com.operasoft.snowboard.dbsync.push.PunchPushSync;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;
import com.operasoft.snowboard.util.Session;

public class PunchInEvent {

	private final UserWorkStatusLogs dto = new UserWorkStatusLogs();
	private Context context = null;

	public PunchInEvent(Context context, String userId) {
		this.context = context;
		initDto(userId);
	}

	private void initDto(String userId) {
		Utils cU = new Utils(context);

		dto.setCompanyId(Session.getCompanyId());
		dto.setDateTime(CommonUtils.UtcDateNow());
		dto.setUserId(userId);
		dto.setServiceLocationId(getPolygonId());
		dto.setOperation(UserWorkStatusLogs.PUNCH_IN);
		dto.setWorkStatus(User.STATUS_IN_VEHICLE);
		dto.setImei(cU.getIMEI());

		if (Session.clocation != null) {
			dto.setLatitude(Session.clocation.getLatitude());
			dto.setLongitude(Session.clocation.getLongitude());
		} else {
			dto.setLatitude(0.0);
			dto.setLongitude(0.0);
		}

		if (Session.getVehicle() != null)
			dto.setVehicleId(Session.getVehicle().getId());
		else
			dto.setVehicleId("");

	}

	/**
	 * send punch in details to the server
	 */
	public void doPunch(String operation) {
		PunchPushSync sync = PunchPushSync.getInstance();

		dto.setOperation(operation);
		sync.pushData(context, dto);

		String message = "Successfully punched in";
		final User user = (new UsersDao()).getById(dto.getUserId());
		if (user != null) {
			message = user.getFullName() + " - " + message;
		}
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Starts new session and send session details to server
	 */
	public void sendSessionStartEvent() {
		LoginSession session = new LoginSession();
		session.setImei(dto.getImei());
		session.setStart_datetime(dto.getDateTime());
		session.setUserId(dto.getUserId());
		session.setLatitude(dto.getLatitude());
		session.setLongitude(dto.getLongitude());
		session.setVehicleId(dto.getVehicleId());
		session.setSession_status(LoginSession.START_STATUS);

		LoginSessionPushSync sync = LoginSessionPushSync.getInstance();
		sync.pushData(context, session);

		// Update the global session object
		Session.FirstLogin = false;

		// Update the IMEI information on server...
		// Not quite ready yet...
		ImeiCompanyOneTimePush imeiUpdate = new ImeiCompanyOneTimePush();
		DbSyncManager.getInstance().addOneTimeSync(imeiUpdate);
	}

	/**
	 * if user is in polygon will return polygonId (SlId), else blank
	 * 
	 * @return SlId
	 */
	private String getPolygonId() {

		// PointOfInterestManager poiManager = PointOfInterestManager.getInstance();
		ArrayList<PointOfInterest> activePois = new ArrayList<PointOfInterest>();

		ServiceLocationDao slDao = new ServiceLocationDao();
		List<ServiceLocation> list = slDao.listAll();
		for (ServiceLocation sl : list) {
			if (sl.getPolygon() != null)
				if (!sl.getPolygon().equals("")) {
					PointOfInterest poi = new PointOfInterest(sl.getId());
					poi.attachServiceLocation(sl);
					activePois.add(poi);
				}
		}

		String SlId = "";
		for (PointOfInterest poi : activePois) {

			List<TIT_RoutePoint> nodes = CommonUtils.getPolyNodes(poi.getPolygon());
			double[] nlat = new double[nodes.size()];
			double[] nlon = new double[nodes.size()];

			for (int i = 0; i < nodes.size(); i++) {
				nlat[i] = nodes.get(i).getLatitude();
				nlon[i] = nodes.get(i).getLongitude();
			}
			if (CommonUtils.isPointInPolygon(nlon, nlat, nodes.size(),

			Session.clocation == null ? 0.0f : (float) Session.clocation.getLongitude(),

			Session.clocation == null ? 0.0f : (float) Session.clocation.getLatitude())) {

				SlId = poi.getSlId();
			}
		}

		return SlId;
	}
}
