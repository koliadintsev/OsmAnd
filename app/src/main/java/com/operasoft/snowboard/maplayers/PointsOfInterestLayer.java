package com.operasoft.snowboard.maplayers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.osmand.plus.views.OsmandMapLayer;
import net.osmand.plus.views.OsmandMapTileView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.operasoft.snowboard.database.Company;
import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.dbsync.Utils;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestActionHandler;
import com.operasoft.snowboard.engine.PointOfInterestActionListener;
import com.operasoft.snowboard.engine.PointOfInterestComparator;
import com.operasoft.snowboard.engine.PointOfInterestConstants;
import com.operasoft.snowboard.engine.PointOfInterestEventListener;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.util.Session;

/**
 * This class implements the Osmand layer used to display the list of POIs to display on the map.
 * Depending on their status, each POI may be displayed differently and may enable a specific set of
 * actions.
 * 
 * @author enbake
 */
public class PointsOfInterestLayer extends OsmandMapLayer implements PointOfInterestEventListener, PointOfInterestConstants {

	private final String RED_COLOR = "#cc0000";
	private final String GREEN_COLOR = "#99cc00";
	private final String TRACER_COLOR = "#00FF00";

	private Canvas canvas;

	private Paint paintPath, paintDistance, paintOnline;
	private Path path;

	ArrayList<ServiceLocation> serviceLocations;
	/**
	 * This is the listener to invoke when the driver performs an action on a POI.
	 */
	private PointOfInterestActionListener actionListener;
	/**
	 * This is the POI manager used by this layer to retrieve the list of active POIs at any given
	 * time
	 */
	private PointOfInterestManager poiMgr;
	/**
	 * List of active POIs that need to be considered by the layer
	 */
	private List<PointOfInterest> activePois = new ArrayList<PointOfInterest>();
	/**
	 * This is the POI currently selected by the driver
	 */
	private PointOfInterestComparator poiComparator = new PointOfInterestComparator();

	private PointOfInterestMenu menu;
	/**
	 * The route currently displayed
	 */
	private Route selectedRoute = null;
	/**
	 * The list of points associated with the route currently selected
	 */
	private static PointF routePoints[];

	private Paint routeStart;
	private Paint routeEnd;
	private Paint routeBreadCrumb;

	private Bitmap arrowImage;
	Matrix matrix = new Matrix();

	private OsmandMapTileView mView;

	private Rect boundsRect;
	private RectF tileRect;

	/**
	 * This map defines how a given POI needs to be drawn based on its status
	 */
	private static Map<PointOfInterest.PoiStatus, PointOfInterestIcon> iconMap;

	/**
	 * This flag will check visibility of menu on screen
	 */
	boolean isMenu = false;

	public PointsOfInterestLayer(Context context) {
		poiMgr = PointOfInterestManager.getInstance();
		poiMgr.addPoiEventListener(this);
		actionListener = new PointOfInterestActionHandler(context);
	}

	private void updateRoutePoints(Route route) {
		if (route != this.selectedRoute) {
			if (route == null) {
				routePoints = new PointF[0];
			} else {
				List<PointF> points = getGeoPolygon(route.getLinePath());
				routePoints = new PointF[points.size()];
				routePoints = points.toArray(routePoints);
			}
			selectedRoute = route;
			Session.serLocCrossed = new HashSet<String>();
		}
	}

	private void initUI() {
		markerinitUI();
		if (iconMap == null) {
			initIconMap();
		}

		menu = new PointOfInterestMenu(actionListener, mView);
	}

	private void markerinitUI() {
		boundsRect = new Rect(0, 0, mView.getWidth(), mView.getHeight());
		tileRect = new RectF();
		path = new Path();

		paintPath = new Paint();
		paintPath.setColor(Color.RED);
		paintPath.setStyle(Style.STROKE);
		paintPath.setStrokeWidth(6);
		paintPath.setAlpha(150);
		paintPath.setAntiAlias(true);
		paintPath.setStrokeCap(Cap.ROUND);
		paintPath.setStrokeJoin(Join.ROUND);

		paintDistance = new Paint();
		paintDistance.setColor(Color.BLACK);
		paintDistance.setTextSize(30);

		paintOnline = new Paint();
		paintOnline.setColor(Color.BLACK);
		paintOnline.setTextSize(15);

		routeStart = new Paint();
		routeStart.setColor(Color.parseColor(GREEN_COLOR));

		routeEnd = new Paint();
		routeEnd.setColor(Color.parseColor(RED_COLOR));

		routeBreadCrumb = new Paint();
		routeBreadCrumb.setColor(Color.parseColor(TRACER_COLOR));
		routeBreadCrumb.setStyle(Style.STROKE);
		routeBreadCrumb.setStrokeWidth(6);
		routeBreadCrumb.setAlpha(150);
		routeBreadCrumb.setAntiAlias(true);
		routeBreadCrumb.setStrokeCap(Cap.ROUND);
		routeBreadCrumb.setStrokeJoin(Join.ROUND);

		arrowImage = BitmapFactory.decodeResource(mView.getResources(), com.operasoft.snowboard.R.drawable.arrow_black);
	}

	@Override
	public void initLayer(OsmandMapTileView view) {
		this.mView = view;
		initUI();
	}

	@Override
	public void onDraw(Canvas canvas, RectF latlonRect, RectF tilesRect, DrawSettings settings) {
		this.canvas = canvas;

		mView.calculateTileRectangle(boundsRect, mView.getCenterPointX(), mView.getCenterPointY(), mView.getXTile(), mView.getYTile(),
				tileRect);

		if (Session.networkState.equals("disconnected")) {
			canvas.drawCircle(canvas.getWidth() - 40, 70, 12, routeEnd);
			canvas.drawText("Offline", canvas.getWidth() - 110, 75, paintOnline);
		} else if (Session.networkState.equals("connected")) {
			canvas.drawCircle(canvas.getWidth() - 40, 70, 12, routeStart);
			canvas.drawText("Online", canvas.getWidth() - 110, 75, paintOnline);
		}

		// The route is now drawn by the RouteLayer
		if (selectedRoute != null) {
			drawSelectedRoute(latlonRect);
		}

		if (poiMgr.isInBreadCrumbMode()) {
			path.reset();
			double distance = 0;

			ArrayList<TIT_RoutePoint> breadCrumbPoints = poiMgr.getBreadCrumbPoints();
			if (!breadCrumbPoints.isEmpty()) {
				String disUnit = Session.getDistanceUnit();
				int px = mView.getRotatedMapXForPoint(breadCrumbPoints.get(0).getLatitude(), breadCrumbPoints.get(0).getLongitude());
				int py = mView.getRotatedMapYForPoint(breadCrumbPoints.get(0).getLatitude(), breadCrumbPoints.get(0).getLongitude());

				double lat = breadCrumbPoints.get(0).getLatitude();
				double lon = breadCrumbPoints.get(0).getLongitude();
				path.moveTo(px, py);
				for (TIT_RoutePoint point : breadCrumbPoints) {
					px = mView.getRotatedMapXForPoint(point.getLatitude(), point.getLongitude());
					py = mView.getRotatedMapYForPoint(point.getLatitude(), point.getLongitude());

					distance += Utils.distance(lat, lon, point.getLatitude(), point.getLongitude(),
							disUnit.equals(Company.KILOMETERS) ? 'K' : 'M');
					path.lineTo(px, py);

					lat = point.getLatitude();
					lon = point.getLongitude();
				}

				float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mView.getResources().getDisplayMetrics());
				canvas.drawPath(path, routeBreadCrumb);
				canvas.drawText(new DecimalFormat("0.00").format(distance) + " " + disUnit, (7 * pixels), canvas.getHeight() - 15,
						paintDistance);
			}
		}

		/**
		 * Only draw visible POIs
		 */
		// int visible = 0;
		for (int i = 0; i < activePois.size(); i++) {
			PointOfInterest poi = activePois.get(i);
			if (poi != null) {
				// Log.d("poiicon", "" + poi);
				if (mView.isPointOnTheRotatedMap(poi.getLatitude(), poi.getLongitude())) {
					PointOfInterestIcon icon = iconMap.get(poi.getStatus());
					if (icon != null) {
						icon.draw(canvas, mView, poi);
					}
					// visible++;
				}
			}
		}
		// Log.d("POI", visible + "/" + activePois.size() + " are visible");
	}

	void drawSelectedRoute(RectF latlonRect) {
		path.reset();
		// TODO Draw only the list of points that are located inside the visible
		// rectangle to optimize the display...
		if (routePoints.length > 0) {

			int px = mView.getRotatedMapXForPoint(routePoints[0].y, routePoints[0].x);
			int py = mView.getRotatedMapYForPoint(routePoints[0].y, routePoints[0].x);

			canvas.drawCircle(px, py, 20, routeStart);
			int x = 0;
			int y = 0;

			if (Session.inlookAheadMode) {
				// Navigation route points count
				int np = Session.navRoutePointsCount;

				// Route points count
				int rp = routePoints.length;

				int startPoint = 0;
				int endPoint = 0;

				// Navigation overlapping route
				if ((np - rp) < Route.LOOK_AHEAD_TURN_COUNT) {

					// Navigation overlapping route partially
					if ((np - rp) >= 0) {
						startPoint = 0;
						endPoint = Route.LOOK_AHEAD_TURN_COUNT - (np - rp);
					} else {
						// Navigation overlapping route fully
						startPoint = rp - np;

						// Check if navigation touched end point of route or not
						endPoint = (startPoint + Route.LOOK_AHEAD_TURN_COUNT) < rp ? (startPoint + Route.LOOK_AHEAD_TURN_COUNT) : rp;
					}
					x = mView.getRotatedMapXForPoint(routePoints[startPoint].y, routePoints[startPoint].x);
					y = mView.getRotatedMapYForPoint(routePoints[startPoint].y, routePoints[startPoint].x);

					path.moveTo(x, y);

					for (int i = startPoint; i < endPoint; i++) {
						x = mView.getRotatedMapXForPoint(routePoints[i].y, routePoints[i].x);
						y = mView.getRotatedMapYForPoint(routePoints[i].y, routePoints[i].x);
						// draw the line.
						path.lineTo(x, y);

						if (i == 1) {
							float angle = (int) (Math.atan2(y - py, x - px) * 180 / Math.PI);

							matrix.setRotate(angle);
							Bitmap arrow = Bitmap.createBitmap(arrowImage, 0, 0, arrowImage.getWidth(), arrowImage.getHeight(), matrix,
									true);
							int x2 = (px + x) / 2;
							int y2 = (py + y) / 2;
							// Draw image between line.
							canvas.drawBitmap(arrow, x2 - arrow.getWidth() / 2, y2 - arrow.getHeight() / 2, null);
						}
					}
					canvas.drawPath(path, paintPath);
				}

			} else {
				path.moveTo(px, py);
				for (int i = 1; i < routePoints.length; i++) {
					x = mView.getRotatedMapXForPoint(routePoints[i].y, routePoints[i].x);
					y = mView.getRotatedMapYForPoint(routePoints[i].y, routePoints[i].x);
					// draw the line.
					path.lineTo(x, y);

					if (i == 1) {
						float angle = (int) (Math.atan2(y - py, x - px) * 180 / Math.PI);

						matrix.setRotate(angle);
						Bitmap arrow = Bitmap.createBitmap(arrowImage, 0, 0, arrowImage.getWidth(), arrowImage.getHeight(), matrix, true);
						int x2 = (px + x) / 2;
						int y2 = (py + y) / 2;
						// Draw image between line.
						canvas.drawBitmap(arrow, x2 - arrow.getWidth() / 2, y2 - arrow.getHeight() / 2, null);
					}
				}
				canvas.drawPath(path, paintPath);
			}

			px = mView.getRotatedMapXForPoint(routePoints[routePoints.length - 1].y, routePoints[routePoints.length - 1].x);
			py = mView.getRotatedMapYForPoint(routePoints[routePoints.length - 1].y, routePoints[routePoints.length - 1].x);

			canvas.drawCircle(px, py, 20, routeEnd);
		}
	}

	@Override
	public void destroyLayer() {
	}

	@Override
	public boolean drawInScreenPixels() {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			for (PointOfInterest poi : activePois) {
				if (poi != null) {
					if (mView.isPointOnTheRotatedMap(poi.getLatitude(), poi.getLongitude())) {
						PointOfInterestIcon icon = iconMap.get(poi.getStatus());
						if (icon != null) {
							int px = (int) event.getX();
							int py = (int) event.getY();

							int cx = mView.getRotatedMapXForPoint(poi.getLatitude(), poi.getLongitude());
							int cy = mView.getRotatedMapYForPoint(poi.getLatitude(), poi.getLongitude());

							if (pointInCircle(px, py, 50, cx, cy)) {
								menu.createDialog(poi);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public void showMenu(PointOfInterest poi) {
		menu.createDialog(poi);
	}

	@Override
	public void poiAdded(PointOfInterest poi) {
		boolean added = false;
		for (int i = 0; i < activePois.size(); i++) {
			PointOfInterest curPoi = activePois.get(i);
			if ((curPoi != null) && (poiComparator.compare(poi, curPoi) < 0)) {
				activePois.add(i, poi);
				added = true;
				break;
			}
		}
		if (!added) {
			activePois.add(poi);
		}

		// TODO Figure out a more optimal way to update the layer than this...
		// mView.refreshMap();
	}

	@Override
	public void poiModified(PointOfInterest poi) {
		// TODO Figure out a more optimal way to update the layer than this...
		// mView.refreshMap();
	}

	@Override
	public void poiRemoved(PointOfInterest poi) {
		activePois.remove(poi);
		// TODO Figure out a more optimal way to update the layer than this...
		// mView.refreshMap();
	}

	@Override
	public void onPoiListReloaded(Collection<PointOfInterest> activePois) {
		updateRoutePoints(poiMgr.getCurrentRoute());
		updateActivePois(activePois);

		// mView.refreshMap();
	}

	private void updateActivePois(Collection<PointOfInterest> activePois) {
		// Build the new list of active POIs
		List<PointOfInterest> list = new ArrayList<PointOfInterest>();

		for (PointOfInterest poi : activePois) {
			list.add(poi);
		}

		// Sort POIs based on their centroid
		// Collections.sort(list, poiComparator);

		// Override the list of active POIs
		this.activePois = list;
	}

	private void initIconMap() {
		iconMap = new HashMap<PointOfInterest.PoiStatus, PointOfInterestIcon>();
		PointOfInterest.PoiStatus status = PointOfInterest.PoiStatus.SERVICE_ACTIVITY_RECEIVED;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.SERVICE_ACTIVITY_ACCEPTED;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.SERVICE_ACTIVITY_IN_DIRECTION;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.SERVICE_LOCATION_ACTIVE;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.SERVICE_LOCATION_GO_BACK;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.SERVICE_LOCATION_COMPLETED;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.SERVICE_LOCATION_COMPLETED_NOW;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.SERVICE_LOCATION_COMPLETED_NOW;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.MISSION_ENABLED;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.MISSION_ACTIVE;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.MARKER_INSTALLER;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.END_ROUTE;
		iconMap.put(status, new PointOfInterestIcon(status));

		status = PointOfInterest.PoiStatus.SERVICE_LOCATION_CONSTRUCTION;
		iconMap.put(status, new PointOfInterestIcon(status));

	}

	/**
	 * @param geom
	 * @return Route Point list.
	 */
	public List<PointF> getGeoPolygon(String geom) {
		List<PointF> routList = new ArrayList<PointF>();
		if ((geom != null) && (!geom.equals("")) && (!geom.equals("null"))) {
			String firstGeomIndex = geom.replace("LINESTRING(", "");
			String GeomPolygon = firstGeomIndex.replace(")", "");
			String[] geomCoOrdinates = GeomPolygon.split(",");
			PointF point = new PointF();
			for (int i = 0; i < geomCoOrdinates.length; i++) {
				String[] polygons = geomCoOrdinates[i].split(" ");
				point = new PointF();
				point.y = Float.valueOf(polygons[0].trim());
				point.x = Float.valueOf(polygons[1].trim());
				routList.add(point);
			}
		}
		return routList;
	}

	public boolean pointInCircle(int px, int py, int rad, int cx, int cy) {
		if (((cx - px) * (cx - px) + (cy - py) * (cy - py)) <= (rad * rad))
			return true;

		return false;
	}

}