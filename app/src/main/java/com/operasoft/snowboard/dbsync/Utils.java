package com.operasoft.snowboard.dbsync;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.osmand.plus.OsmandSettings;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.operasoft.snowboard.database.DataBaseHelper;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterest.PoiStatus;
import com.operasoft.snowboard.engine.PointOfInterest.SlStatus;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;
import com.operasoft.snowboard.util.Session;

public class Utils {

	public static String ConvertToDate(String dateString) {
		String convertedDate;
		String[] datetime = dateString.split(" ");
		String[] monthName = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
		String[] date = datetime[0].split("-");
		String year = date[0];
		String month = monthName[Integer.parseInt(date[1]) - 1];
		String day = date[2];
		convertedDate = month + " " + day + ", " + year + " " + datetime[1];
		return convertedDate;
	}

	public static String ConvertToDateTime(String dateString) {
		String convertedDate;
		String dayNum;
		String[] datetime = dateString.split(",");
		String dayMonth = datetime[0];
		String[] daysarray = dayMonth.split(" ");
		String month = getMonthNumber(daysarray[0]);
		String day = daysarray[1];
		if (Integer.parseInt(day) <= 9) {
			dayNum = "0" + day;
		} else
			dayNum = day;
		String yearnTime = datetime[1];
		String[] yearTarray = yearnTime.split(" ");
		String year = yearTarray[1];
		String time = yearTarray[2];
		convertedDate = year + "-" + month + "-" + dayNum + " " + time;
		return convertedDate;
	}

	public static String getMonthNumber(String Month) {
		if (Month.equals("January"))
			return "01";
		else if (Month.equals("February"))
			return "02";
		else if (Month.equals("March"))
			return "03";
		else if (Month.equals("April"))
			return "04";
		else if (Month.equals("May"))
			return "05";
		else if (Month.equals("June"))
			return "06";
		else if (Month.equals("July"))
			return "07";
		else if (Month.equals("August"))
			return "08";
		else if (Month.equals("September"))
			return "09";
		else if (Month.equals("October"))
			return "10";
		else if (Month.equals("November"))
			return "11";
		else if (Month.equals("December"))
			return "12";
		return "0";
	}

	/**
	 * @Description : Check the Internet connection.
	 * @param context
	 *            con.
	 * @return boolean value{Online :true , Offline : false}.
	 */
	public static boolean isOnline(Context con) {
		/*
		 * boolean connected = false; try { ConnectivityManager conMgr = (ConnectivityManager)
		 * con.getSystemService(Context.CONNECTIVITY_SERVICE); connected = conMgr.getActiveNetworkInfo().isConnected(); } catch (Exception
		 * e) { // /e.printStackTrace(); } if (connected) { return true; } return false;
		 */
		
		ConnectivityManager conMgr = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = conMgr.getActiveNetworkInfo();
		
		return (ni != null && ni.isAvailable() && ni.isConnected());
	}

	// @SuppressWarnings("static-access")
	// public static void getBasedata(Context con, String IMEI) throws Exception {
	// if (isOnline(con)) {
	// BasedataModel bModel = new BasedataModel(IMEI, con);
	// bModel.fetchModelData();
	// } else {
	// throw new Exception("No Internet Connection " + "\n" + "Please try again");
	// }
	// }

	/**
	 * @deprecated We should retrieve the IMEI from the application context rather than from a DB setting. See CommonUtils.getIMEI()
	 * @return
	 */
	public static String selectIMEINum() {
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery("select * from master_setting", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToNext();
				String imei = cursor.getString(cursor.getColumnIndexOrThrow("imei"));
				cursor.close();
				return imei;
			}
		}
		return "";
	}

	/**
	 * To show the complete route on screen.
	 * 
	 * @return zoomLevel
	 */
	private static int setRouteZoom() {

		// TODO 001 no zoomm Confirm :
		if (true)
			return Session.userZoom;

		List<TIT_RoutePoint> points = getGeoPolygon(Session.route.getLinePath());

		List<Double> lat = new ArrayList<Double>();
		List<Double> lon = new ArrayList<Double>();

		for (TIT_RoutePoint point : points) {
			lat.add(point.getLatitude());
			lon.add(point.getLongitude());
		}

		Collections.sort(lat);
		Collections.sort(lon);

		int maxSize = lat.size() - 1;
		int cZoom = zoomToDistance((distance(lat.get(0), lon.get(0), lat.get(maxSize), lon.get(maxSize), 'K')) * 1000);

		OsmandSettings settings = Session.MapAct.getMyApplication().getSettings();
		settings.setMapLocationToShow((lat.get(0) + lat.get(maxSize)) / 2, (lon.get(0) + lon.get(maxSize)) / 2, cZoom, null);
		return cZoom;
	}

	/**
	 * To show the all Markers on screen.
	 * 
	 * @return zoomLevel
	 */
	public static int setMarkerZoom() {
		Collection<PointOfInterest> poiList = PointOfInterestManager.getInstance().listActivePois();

		List<Double> lat = new ArrayList<Double>();
		List<Double> lon = new ArrayList<Double>();

		for (PointOfInterest poi : poiList) {
			if (poi.getStatus() == PoiStatus.MARKER_INSTALLER) {
				lat.add(poi.getLatitude());
				lon.add(poi.getLongitude());
			}
		}

		if (lat.isEmpty() || lon.isEmpty()) {
			Log.w("Utils", "lat/lon is empty, returning zoom 0");
			return 0;
		}

		Collections.sort(lat);
		Collections.sort(lon);

		int maxSize = lat.size() - 1;
		int cZoom = zoomToDistance((distance(lat.get(0), lon.get(0), lat.get(maxSize), lon.get(maxSize), 'K')) * 1000);

		OsmandSettings settings = Session.MapAct.getMyApplication().getSettings();
		settings.setMapLocationToShow((lat.get(0) + lat.get(maxSize)) / 2, (lon.get(0) + lon.get(maxSize)) / 2, cZoom, null);
		return cZoom;
	}

	public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist > 0 ? dist : 0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	/**
	 * returns the respective zoom level to show a specific distance on map
	 * 
	 * @param distance
	 * @return zoom
	 */
	private static int zoomToDistance(double distance) {
		int cZoom = 1;
		if (distance < 15000000)
			cZoom++;
		if (distance < 12000000)
			cZoom++;
		if (distance < 5000000)
			cZoom++;
		if (distance < 2000000)
			cZoom++;
		if (distance < 1000000)
			cZoom++;
		if (distance < 500000) // 7
			cZoom++;
		if (distance < 300000)
			cZoom++;
		if (distance < 200000)
			cZoom++;
		if (distance < 100000) // 10
			cZoom++;
		if (distance < 50000)
			cZoom++;
		if (distance < 20000)
			cZoom++;
		if (distance < 5000)
			cZoom++;
		if (distance < 4000) // 14
			cZoom++;
		if (distance < 2000)
			cZoom++;
		if (distance < 1000)
			cZoom++;
		if (distance < 500)
			cZoom++;
		if (distance < 200)
			cZoom++;
		if (distance < 100)
			cZoom++;
		if (distance < 50) // 20
			cZoom++;
		return cZoom;
	}

	public static List<TIT_RoutePoint> getGeoPolygon(String geom) {
		List<TIT_RoutePoint> routList = new ArrayList<TIT_RoutePoint>();
		if ((geom != null) && (!geom.equals(""))) {
			String firstGeomIndex = geom.replace("LINESTRING(", "");
			String GeomPolygon = firstGeomIndex.replace(")", "");
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

	public static int setSLZoom() {
		Collection<PointOfInterest> poiList = PointOfInterestManager.getInstance().listActivePois();

		List<Double> lat = new ArrayList<Double>();
		List<Double> lon = new ArrayList<Double>();

		for (PointOfInterest poi : poiList) {
			if (poi.getSlStatus() == SlStatus.ACTIVE) {
				lat.add(poi.getLatitude());
				lon.add(poi.getLongitude());
			}
		}

		if (lat.isEmpty() || lon.isEmpty()) {
			Log.w("Utils", "lat/lon is empty, returning zoom 0");
			return 0;
		}

		Collections.sort(lat);
		Collections.sort(lon);

		int maxSize = lat.size() - 1;
		int cZoom = zoomToDistance((distance(lat.get(0), lon.get(0), lat.get(maxSize), lon.get(maxSize), 'K')) * 1000);
		//int userZoom = 18;

		OsmandSettings settings = Session.MapAct.getMyApplication().getSettings();
		if (Session.clocation != null && settings != null) {
			settings.setMapLocationToShow(Session.clocation.getLatitude(), Session.clocation.getLongitude(), 15, null);
			Session.userZoom = cZoom;
		}
		return cZoom;

	}
}