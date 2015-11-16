package com.operasoft.snowboard.dbsync;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import com.operasoft.snowboard.Sw_LoginScreenActivity;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.VehiclesDao;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;

public class CommonUtils {
	private Context mContext;
	static private UsersDao usersDao = new UsersDao();
	static private VehiclesDao vehiclesDao = new VehiclesDao();

	public CommonUtils(Context con) {
		this.mContext = con;
	}

	public boolean getAthenticateUser(String userPin) {
		return usersDao.isPinValid(userPin);
	}

	public static void AlertGenerator(Sw_LoginScreenActivity con, Exception e, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setMessage(msg).setCancelable(false).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static String selectUserId(String userPin) {
		return usersDao.getUserIdForPin(userPin);
	}

	/**
	 * TO get current time(GMT)
	 * 
	 * @return current time at specific time zone
	 */
	public static String UtcDateNow() {
		SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatUTC.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
		return formatUTC.format(new Date().getTime());
	}

	public static Date Now() {
		final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EST5EDT"));
		return calendar.getTime();
	}

	public static boolean isRunningOnEmulator() {
		if (Build.DEVICE.equals("generic")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the given point is in the polygon(can be concave). x[] and y[] is assumed to be least size of verticesNum
	 * 
	 * @param x
	 *            array of x coordinates
	 * @param y
	 *            array of y coordinates
	 * @param verticesNum
	 * @param px
	 *            point x coordinate
	 * @param py
	 *            point y coordinate
	 * @return Wheter the point is in the polygon
	 */
	public static boolean isPointInPolygon(double x[], double y[], int verticesNum, float px, float py) {
		if (verticesNum < 3)
			return false;

		boolean oddNodes = false;
		float x2 = (float) x[verticesNum - 1];
		float y2 = (float) y[verticesNum - 1];
		float x1, y1;
		for (int i = 0; i < verticesNum; x2 = x1, y2 = y1, ++i) {
			x1 = (float) x[i];
			y1 = (float) y[i];
			if (((y1 < py) && (y2 >= py)) || (y1 >= py) && (y2 < py)) {
				if ((py - y1) / (y2 - y1) * (x2 - x1) < (px - x1))
					oddNodes = !oddNodes;
			}
		}
		return oddNodes;
	}

	public static List<TIT_RoutePoint> getPolyNodes(String geom) {
		List<TIT_RoutePoint> routList = new ArrayList<TIT_RoutePoint>();
		if ((geom != null) && (geom.equals("null") == false) && (geom.equals("") == false)) {
			String firstGeomIndex = geom.replace("POLYGON((", "");
			String GeomPolygon = firstGeomIndex.replace("))", "");
			String[] geomCoOrdinates = GeomPolygon.split(",");
			TIT_RoutePoint rout = new TIT_RoutePoint();
			for (int i = 0; i < geomCoOrdinates.length; i++) {
				String[] polygons = geomCoOrdinates[i].split(" ");
				double lat = Double.valueOf(polygons[0].trim()).doubleValue();
				double log = Double.valueOf(polygons[1].trim()).doubleValue();
				rout = new TIT_RoutePoint(lat, log);
				routList.add(rout);
			}
		}
		return routList;
	}

	public static String selectUserLastName(String userPin) {
		User user = usersDao.getByPin(userPin);
		if (user != null) {
			return user.getLastName();
		}
		return null;
	}

	public static String selectVehicleNumber(String vehicle) {
		Vehicle dto = vehiclesDao.getById(vehicle);
		if (dto != null) {
			return dto.getVehicleNumber();
		}
		return "";
	}
}