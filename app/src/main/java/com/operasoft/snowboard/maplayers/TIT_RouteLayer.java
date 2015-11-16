/**
 * 
 */
package com.operasoft.snowboard.maplayers;

import java.util.ArrayList;
import java.util.List;

import net.osmand.plus.routing.RoutingHelper;
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
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.util.Config;
import com.operasoft.snowboard.util.Session;

/**
 * @author enbake
 * 
 */
public class TIT_RouteLayer extends OsmandMapLayer {

	private OsmandMapTileView view;

	private final RoutingHelper helper;
	private Context mContext;
	private Rect boundsRect;
	private RectF tileRect;
	private List<TIT_RoutePoint> points;
	private Paint paint;
	private Paint paint1, point, pointCrossed, paintText, paintGreenBtn, paintRedBtn, markerBg;
	private Path path;
	private String routename;
	Paint pa = new Paint();
	Paint paintTextSeq = new Paint();

	private Rect rInstall, rCancel;

	private static List<ServiceLocation> serviceLoc;
	private static ArrayList<Integer> xT, yT;
	private static int drawVal = -1;
	ArrayList<ServiceLocation> serviceLocations;

	public TIT_RouteLayer(Context con, RoutingHelper helper) {
		this.helper = helper;
		mContext = con;
	}

	private void initUI() {
		boundsRect = new Rect(0, 0, view.getWidth(), view.getHeight());
		tileRect = new RectF();
		points = new ArrayList<TIT_RoutePoint>();
		path = new Path();

		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(6);
		paint.setAlpha(150);
		paint.setAntiAlias(true);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeJoin(Join.ROUND);

		paint1 = new Paint();
		paint1.setColor(Color.rgb(255, 128, 0));
		paint1.setStyle(Style.FILL);
		paint1.setStrokeWidth(10);
		paint1.setAlpha(150);
		paint1.setAntiAlias(true);
		paint1.setStrokeCap(Cap.ROUND);
		paint1.setStrokeJoin(Join.ROUND);

		point = new Paint();
		point.setColor(Color.BLACK);
		point.setStyle(Paint.Style.STROKE);
		point.setAlpha(150);

		pointCrossed = new Paint();
		pointCrossed.setColor(Color.BLACK);

		paintText = new Paint();
		paintText.setStyle(Paint.Style.FILL);
		paintText.setAntiAlias(true);
		paintText.setTextSize(20);

		markerBg = new Paint();
		markerBg.setColor(Color.LTGRAY);
		markerBg.setAlpha(175);
		markerBg.setAntiAlias(true);

		paintGreenBtn = new Paint();
		paintGreenBtn.setColor(Color.rgb(5, 255, 5));
		paintGreenBtn.setStyle(Style.FILL);

		paintRedBtn = new Paint();
		paintRedBtn.setColor(Color.rgb(255, 5, 5));
		paintRedBtn.setStyle(Style.FILL);
		routename="";

	}

	@Override
	public void initLayer(OsmandMapTileView view) {
		this.view = view;
		initUI();
	}

	@Override
	public void onDraw(Canvas canvas, RectF latlonRect, RectF tilesRect, DrawSettings settings) {

		int w = view.getWidth();
		int h = view.getHeight();
		boundsRect = new Rect(0, 0, w, h);
		view.calculateTileRectangle(boundsRect, view.getCenterPointX(), view.getCenterPointY(), view.getXTile(), view.getYTile(), tileRect);

		if (Config.MARKER_INSTALLATION) {

			/*xT = new ArrayList<Integer>();
			yT = new ArrayList<Integer>();

			serviceLocations = ContractsDao.getServiceLocations();

			if (serviceLocations.size() > 0) {
				for (int k = 0; k < serviceLocations.size(); k++) {
					int xl = view.getMapXForPoint(serviceLocations.get(k).getLongitude());
					int yl = view.getMapYForPoint(serviceLocations.get(k).getLatitude());

					canvas.drawCircle(xl, yl, 11, point);
					canvas.drawCircle(xl, yl, 10, paint1);

					xT.add(xl);
					yT.add(yl);

					if ((drawVal == k) && (view.getZoom() >= 18)) {
						canvas.drawRect(xl - 150, yl - 120, xl + 150, yl - 11, markerBg);

						rInstall = new Rect(xl - 140, yl - 110, xl - 10, yl - 56);
						rCancel = new Rect(xl, yl - 110, xl + 140, yl - 56);

						canvas.drawRect(rInstall, paintGreenBtn);
						canvas.drawRect(rCancel, paintRedBtn);

						canvas.drawText("Install", xl - 105, yl - 75, paintText);
						canvas.drawText("Cancel", xl + 40, yl - 75, paintText);
						if (serviceLocations != null) {
							String address = getAddress(serviceLocations.get(k).getAddress()) != null ? getAddress(serviceLocations.get(k)
									.getAddress()) : serviceLocations.get(k).getStreetNumber() + " " + serviceLocations.get(k).getStreetName();
							canvas.drawText(address, xl - (address != null ? address.length() * 5 : 10), yl - 30, paintText);
						}
					}

				}
			}*/

		} else {
			
			/*if(Session.route != null)
			{
			
				if(Session.route.getName()!=routename)
				{
				
				path.reset();
	
				if (Session.route != null) {
					TextView routeName = (TextView) Session.MapAct.findViewById(R.id.routename);
					routename=Session.route.getName();
					routeName.setText(routename);
					routeName.setVisibility(View.VISIBLE);
					points = getGeoPolygon(Session.route.getLinePath());
				}
	
				if (points.size() > 0 && Session.route != null) {
					ServiceLocationDao serLocDao = new ServiceLocationDao();
					if (Session.route != null)
						serviceLoc = serLocDao.findAllServiceLocation(Session.route.getId());
					if (serviceLoc != null) {
						if (serviceLoc.size() > 0) {
							for (int k = 0; k < serviceLoc.size(); k++) {
								int xl = view.getMapXForPoint(serviceLoc.get(k).getLongitude());
								int yl = view.getMapYForPoint(serviceLoc.get(k).getLatitude());
								Session.serLoc = serviceLoc;
	
								canvas.drawCircle(xl, yl, 11, point);
								canvas.drawCircle(xl, yl, 10, paint1);
	
								if (Session.serLocCrossed.contains(k))
									canvas.drawCircle(xl, yl, 4, pointCrossed);
	
								// Moved this functionality to ContextMenuLayer.java
								// if (view.getZoom() >= 18) {
								// String address =
								// getAddress(serviceLoc.get(k).getAddress()) !=
								// null ?
								// getAddress(serviceLoc.get(k).getAddress())
								// : serviceLoc.get(k).getStreetNumber() + " " +
								// serviceLoc.get(k).getStreetName();
								// canvas.drawText(address, xl - (address != null ?
								// address.length() * 5 : 10), yl - 15, paintText);
								// }
							}
						}
					}
	
					int px = view.getMapXForPoint(points.get(0).getLongitude());
					int py = view.getMapYForPoint(points.get(0).getLatitude());
					// PointLocationLayer pointLocation = new PointLocationLayer();
					if (Session.location != null) {
						view.getSettings().setMapLocationToShow(points.get(0).getLatitude(), points.get(0).getLongitude(), 15, null);
					}
	
					path.moveTo(px, py);
					for (int i = 1; i < points.size(); i++) {
						TIT_RoutePoint o = points.get(i);
						int x = view.getMapXForPoint(o.getLongitude());
						int y = view.getMapYForPoint(o.getLatitude());
						float angle = (int) (Math.atan2(y - py, x - px) * 180 / Math.PI);
						Bitmap back = BitmapFactory.decodeResource(view.getResources(), R.drawable.arrow_black);
						Matrix matrix = new Matrix();
						matrix.setRotate(angle);
						back = Bitmap.createBitmap(back, 0, 0, back.getWidth(), back.getHeight(), matrix, true);
						path.lineTo(x, y);
						int x2 = (px + x) / 2;
						int y2 = (py + y) / 2;
						canvas.drawBitmap(back, x2 - back.getWidth() / 2, y2 - back.getHeight() / 2, pa);
						px = x;
						py = y;
					}
				}
			}
			canvas.drawPath(path, paint);
			}*/
		}
	}

	@Override
	public void destroyLayer() {
	}

	@Override
	public boolean drawInScreenPixels() {
		return false;
	}

	public RoutingHelper getHelper() {
		return helper;
	}

	public static List<TIT_RoutePoint> getGeoPolygon(String geom) {
		List<TIT_RoutePoint> routList = new ArrayList<TIT_RoutePoint>();
		if (geom.equals("null") == false) {
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

	/*public static String getAddress(String addresses) {
		String address = null;
		if (addresses.equals("null") != false) {
			if (addresses.indexOf(",") > 0) {
				String[] addName = addresses.split(",");
				address = addName[0];
			}
			return address;
		}
		return address;
	}*/

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// In case of install/cancel marker installation
		/*if (Config.MARKER_INSTALLATION && (view.getZoom() >= 18)) {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if ((drawVal >= 0) && (Session.location != null)) {
					// touch point exists in install box or not
					if (rInstall.contains((int) event.getX(), (int) event.getY())) {
						// Updating Marker installation table.
						// Here ServiceLocationId = MarkerInstallationId
						ContractsDao.updateMarker(serviceLocations.get(drawVal).getId(), mContext);
					}
				}

				// getting Sr.No. of service location if touched
				drawVal = touchOnServiceLocation((int) event.getX(), (int) event.getY());
			}
			view.refreshMap();
		}*/
		if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
		}

		return false;
	}

	public int touchOnServiceLocation(int px, int py) {
		double result = -1;
		if (Config.MARKER_INSTALLATION && !xT.isEmpty()) {
			for (int i = 0; i < xT.size(); i++) {
				if (((xT.get(i) - px) * (xT.get(i) - px) + (yT.get(i) - py) * (yT.get(i) - py)) <= 121) {
					result = i;
					break;
				}
			}
		}
		return (int) result;
	}
}