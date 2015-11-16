package com.operasoft.snowboard.events;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.database.LoginSession;
import com.operasoft.snowboard.database.LoginSessionDao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UserWorkStatusLogs;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.push.LoginSessionPushSync;
import com.operasoft.snowboard.dbsync.push.PunchPushSync;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;
import com.operasoft.snowboard.util.Session;

/**
 * @author Narendra
 */
public class PunchOutEvent {

	private final UserWorkStatusLogs dto = new UserWorkStatusLogs();
	private Context context = null;

	public PunchOutEvent(Context context, String userId) {
		this.context = context;

		initDto(userId);
	}

	private void initDto(String userId) {

		Utils cU = new Utils(context);

		dto.setCompanyId(Session.getCompanyId());
		dto.setDateTime(CommonUtils.UtcDateNow());
		dto.setUserId(userId);
		dto.setServiceLocationId(getPolygonId());
		dto.setOperation(UserWorkStatusLogs.PUNCH_OUT);
		dto.setWorkStatus(User.STATUS_INACTIVE);
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
	 * do punch out and send details to server
	 * 
	 * @param closeSession
	 *            true, to close session also.
	 */
	public void doPunchOut(final boolean closeSession) {

		AsyncTask<Void, Void, Void> punchOutTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				PunchPushSync sync = PunchPushSync.getInstance();
				sync.pushData(context, dto);
				if (closeSession)
					closeSession();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				String message = "Successfully punched out";
				final User user = (new UsersDao()).getById(dto.getUserId());
				if (user != null) {
					message = user.getFullName() + " - " + message;
				}
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		};
		punchOutTask.execute();
	}

	/**
	 * Close session and send details to server
	 */
	private void closeSession() {
		LoginSessionDao sessionDao = new LoginSessionDao();
		LoginSession session = sessionDao.getLatestSession();
		session.setImei(dto.getImei());
		session.setEnd_datetime(dto.getDateTime());
		session.setUserId(dto.getUserId());
		session.setLatitude(dto.getLatitude());
		session.setLongitude(dto.getLongitude());
		session.setVehicleId(dto.getVehicleId());
		session.setSession_status(LoginSession.END_STATUS);

		LoginSessionPushSync sync = LoginSessionPushSync.getInstance();
		sync.pushData(context, session);
		// Clear up the global session object
		Session.FirstLogin = true;
		// Session.driver = null;
		// Session.vehicle = null;
	}

	/**
	 * if user is in polygon will return polygonId (SlId), else blank
	 * 
	 * @return SlId
	 */
	private String getPolygonId() {

		PointOfInterestManager poiManager = PointOfInterestManager.getInstance();
		Collection<PointOfInterest> activePois = poiManager.listActivePois();

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

			Session.clocation == null ? 0.0f : (float) Session.clocation.getLatitude()))
				return poi.getSlId();
		}

		return "";
	}
}
