package com.operasoft.snowboard.test;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

import com.operasoft.geom.Point;

/**
 * @author dounaka
 *
 */
public class GpxSimulator {

	private static final ArrayList<Point> points = new ArrayList<Point>();

	private static final long intervalmillis = 500;

	long lastMove = System.currentTimeMillis();
	Point currentPoint = null;

	public static void initRoad(List<Location> locations) {
		synchronized (points) {
			previousLoc = null;
			points.clear();
			for (Location loc : locations)
				addPoint(loc);
		}
	}

	static Location previousLoc = null;

	private static void addPoint(Location loc) {
		if (previousLoc != null) {
			double difflat = loc.getLatitude() - previousLoc.getLatitude();
			double difflong = loc.getLongitude() - previousLoc.getLongitude();

			final int inter = 6;
			for (int i = 0; i < inter; i++) {
				points.add(new Point(previousLoc.getLatitude() + ((difflat * i) / inter), previousLoc.getLongitude() + ((difflong * i) / inter))

				);
			}
		}

		points.add(new Point(loc.getLatitude(), loc.getLongitude()));

		previousLoc = loc;
	}

	public void setLocation(Location location) {
		if (location == null)
			location = new Location("gpxsimulator");
		if (currentPoint == null || (System.currentTimeMillis() - lastMove) > intervalmillis)
			moveNext();
		if (currentPoint != null) {
			location.setLatitude(currentPoint.getLatitude());
			location.setLongitude(currentPoint.getLongitude());

		}
	}

	void moveNext() {
		synchronized (points) {
			if (points.size() > 0) {
				currentPoint = points.get(0);
				points.remove(currentPoint);
				lastMove = System.currentTimeMillis();
			}
		}
	}

	@Deprecated
	private void loadPoints() {
		points.add(new Point(45.53932, -73.60031));
		points.add(new Point(45.53953, -73.60001));
		points.add(new Point(45.53985, -73.59972));
		points.add(new Point(45.53997, -73.59960));
		points.add(new Point(45.54004, -73.59967));
		points.add(new Point(45.54007, -73.59973));
		points.add(new Point(45.54014, -73.59989));
		points.add(new Point(45.54018, -73.59998));
		points.add(new Point(45.54088, -73.60153));
		points.add(new Point(45.54110, -73.60207));
		points.add(new Point(45.54126, -73.60239));
		points.add(new Point(45.54143, -73.60277));
		points.add(new Point(45.54152, -73.60297));
		points.add(new Point(45.54153, -73.60308));
		points.add(new Point(45.54137, -73.60323));
		points.add(new Point(45.54115, -73.60342));
		points.add(new Point(45.54102, -73.60354));
		points.add(new Point(45.54086, -73.60369));
		points.add(new Point(45.54083, -73.60371));
		points.add(new Point(45.54048, -73.60403));
		points.add(new Point(45.54021, -73.60427));
		points.add(new Point(45.53959, -73.60483));
		points.add(new Point(45.53784, -73.60153));
		points.add(new Point(45.53786, -73.60162));

	}

}
