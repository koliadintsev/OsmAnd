package com.operasoft.snowboard.maplayers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.osmand.plus.views.OsmandMapLayer;
import net.osmand.plus.views.OsmandMapTileView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.operasoft.snowboard.engine.VehicleLocation;
import com.operasoft.snowboard.engine.VehicleLocationListener;
import com.operasoft.snowboard.engine.VehicleLocationManager;
import com.operasoft.snowboard.util.Session;

/**
 * This class implements the Osmand layer used to display the list of POIs to display on the map. Depending on their status, each POI may be
 * displayed differently and may enable a specific set of actions.
 * 
 * @author enbake
 * 
 */
public class VehicleLastReportLayer extends OsmandMapLayer implements VehicleLocationListener {

	/**
	 * This is the vehicle manager used by this layer to retrieve the list of vehicles and positions at any given time
	 */
	private VehicleLocationManager locationMgr;
	/**
	 * List of active Vehicles that need to be considered by the layer
	 */
	private List<VehicleLocation> locations = new ArrayList<VehicleLocation>();

	/**
	 * This map defines how a given Vehicle needs to be drawn based on its status
	 */
	private static Map<VehicleLocation.Status, VehicleIcon> iconMap;

	private OsmandMapTileView mView;
	private Rect boundsRect;
	private RectF tileRect;

	public VehicleLastReportLayer(Context context) {
		locationMgr = VehicleLocationManager.getInstance();
		locationMgr.setListener(this);
	}

	private void initUI() {
		markerinitUI();
		if (iconMap == null) {
			initIconMap();
		}
	}

	private void markerinitUI() {
		boundsRect = new Rect(0, 0, mView.getWidth(), mView.getHeight());
		tileRect = new RectF();
	}

	@Override
	public void initLayer(OsmandMapTileView view) {
		this.mView = view;
		initUI();
	}

	@Override
	public void onDraw(Canvas canvas, RectF latlonRect, RectF tilesRect, DrawSettings settings) {
		if (!Session.viewVehicles) {
			return;
		}
		mView.calculateTileRectangle(boundsRect, mView.getCenterPointX(), mView.getCenterPointY(), mView.getXTile(), mView.getYTile(), tileRect);

		/**
		 * Only draw visible Vehicles
		 */
		for (int i = 0; i < locations.size(); i++) {
			VehicleLocation location = locations.get(i);
			if (location != null) {
				if (mView.isPointOnTheRotatedMap(location.getLatitude(), location.getLongitude())) {
					VehicleIcon icon = iconMap.get(location.getStatus());
					if (icon != null) {
						icon.draw(canvas, mView, location);
					}
				}
			}
		}
	}

	@Override
	public void destroyLayer() {
	}

	@Override
	public boolean drawInScreenPixels() {
		return true;
	}

	private void initIconMap() {
		iconMap = new HashMap<VehicleLocation.Status, VehicleIcon>();
		VehicleLocation.Status status = VehicleLocation.Status.IN_MOVEMENT;
		iconMap.put(status, new VehicleIcon(status));

		status = VehicleLocation.Status.STOPPED_LESS_THAN_30;
		iconMap.put(status, new VehicleIcon(status));

		status = VehicleLocation.Status.STOPPED_LESS_THAN_60;
		iconMap.put(status, new VehicleIcon(status));

		status = VehicleLocation.Status.STOPPED_LESS_THAN_DAY;
		iconMap.put(status, new VehicleIcon(status));

		status = VehicleLocation.Status.STOPPED_MORE_THAN_DAY;
		iconMap.put(status, new VehicleIcon(status));
	}

	/**
	 * 
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

	@Override
	public void vehicleAdded(VehicleLocation vehicle) {
		locations.add(vehicle);
	}

	@Override
	public void vehicleModified(VehicleLocation vehicle) {
		if (locations.contains(vehicle))
			locations.remove(vehicle.getId());
			locations.add(vehicle);
	}

	@Override
	public void vehicleRemoved(VehicleLocation vehicle) {
		locations.remove(vehicle);
	}

	@Override
	public void vehicleListReloaded(Collection<VehicleLocation> activeVehicles) {
		locations.clear();
		for (VehicleLocation vehicleLocation : activeVehicles) {
			locations.add(vehicleLocation);
		}
	}
}