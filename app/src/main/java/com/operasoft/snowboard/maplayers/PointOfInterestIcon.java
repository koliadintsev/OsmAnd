package com.operasoft.snowboard.maplayers;

import net.osmand.plus.views.OsmandMapTileView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestManager;

public class PointOfInterestIcon {

	static final private float ADDRESS_TEXT_SIZE = 30.0f;
	// Define the set of colors used for drawing POIs
	private final int RED_COLOR = Color.parseColor("#cc0000");
	private final int BLUE_COLOR = Color.parseColor("#0099cc");
	private final int GREEN_COLOR = Color.parseColor("#99cc00");
	private final int ORANGE_COLOR = Color.parseColor("#ffbb33");
	private final int PURPLE_COLOR = Color.parseColor("#000000");

	private PointOfInterest.PoiStatus status;
	private Paint paint = new Paint();
	private Paint paintText = new Paint();
	private Paint paintTextWhite = new Paint();
	private PointOfInterestManager poiManager = PointOfInterestManager.getInstance();
	public int iconId = R.drawable.red_dot;
	private Bitmap bitmap;

	public PointOfInterestIcon(PointOfInterest.PoiStatus status) {
		paintText.setColor(Color.BLACK);
		paintText.setTextSize(ADDRESS_TEXT_SIZE);

		paintTextWhite.setColor(Color.RED);
		paintTextWhite.setTextSize(ADDRESS_TEXT_SIZE);
		paintTextWhite.setFakeBoldText(true);

		this.status = status;
		switch (status) {
		case SERVICE_ACTIVITY_RECEIVED:
			paint.setColor(RED_COLOR);
			iconId = R.drawable.sa_received;
			break;
		case SERVICE_ACTIVITY_ACCEPTED:
			paint.setColor(BLUE_COLOR);
			iconId = R.drawable.sa_assigned;
			break;
		case SERVICE_ACTIVITY_IN_DIRECTION:
			paint.setColor(GREEN_COLOR);
			iconId = R.drawable.sa_in_direction;
			break;
		case SERVICE_LOCATION_ACTIVE:
			paint.setColor(PURPLE_COLOR);
			iconId = R.drawable.sl_active;
			break;
		case SERVICE_LOCATION_GO_BACK:
			paint.setColor(PURPLE_COLOR);
			iconId = R.drawable.sl_go_back;
			break;
		case SERVICE_LOCATION_COMPLETED:
			paint.setColor(PURPLE_COLOR);
			iconId = R.drawable.sl_completed;
			break;
		case MISSION_ACTIVE:
			paint.setColor(ORANGE_COLOR);
			iconId = R.drawable.mission_active;
			break;
		case MISSION_ENABLED:
			paint.setColor(ORANGE_COLOR);
			iconId = R.drawable.mission_enabled;
			break;
		case MARKER_INSTALLER:
			paint.setColor(ORANGE_COLOR);
			iconId = R.drawable.marker;
			break;
		case SERVICE_LOCATION_COMPLETED_NOW:
			paint.setColor(ORANGE_COLOR);
			iconId = R.drawable.service_location_completed_now;
			break;
		case END_ROUTE:
			paint.setColor(ORANGE_COLOR);
			iconId = R.drawable.ic_stopsign;
			break;
		case SERVICE_LOCATION_CONSTRUCTION:
			paint.setColor(PURPLE_COLOR);
			iconId = R.drawable.ic_construction_location;
			break;
		default:
			paint.setColor(Color.WHITE);
			iconId = R.drawable.red_dot;
		}
	}

	public void draw(Canvas canvas, OsmandMapTileView view, PointOfInterest poi) {
		float x = view.getRotatedMapXForPoint(poi.getLatitude(), poi.getLongitude());
		float y = view.getRotatedMapYForPoint(poi.getLatitude(), poi.getLongitude());

		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(view.getResources(), iconId);
		}

		if (view.getZoom() >= 18) {
			String address = poi.getAddress();
			canvas.drawText(address, x, y - (bitmap.getHeight() / 2), paintText);
		}

		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);

		int poiPosition = poiPositionOnRoute(poi);
		if (poiPosition > -1)
			canvas.drawText((++poiPosition) + "", x + (bitmap.getWidth() / 2), y + 10, paintTextWhite);

		switch (status) {
		case MISSION_ENABLED:
		case MISSION_ACTIVE:
			canvas.drawText(poi.getLabel(), x + (bitmap.getWidth() / 2), y + bitmap.getHeight() / 3, paintText);
			break;
		default:
			break;

		}

	}

	/**
	 * Find position of poi in route
	 * 
	 * @param poi
	 * @return -1 if poi doesn't belong's to route or SL is completed
	 */
	private int poiPositionOnRoute(PointOfInterest poi) {
		switch (poi.getStatus()) {
		case SERVICE_LOCATION_ACTIVE:
		case SERVICE_LOCATION_CONSTRUCTION:
		case SERVICE_LOCATION_GO_BACK:
			for (ServiceLocation sl : poiManager.routeSlList) {
				if (poi.getId().equals(sl.getId()))
					return poiManager.routeSlList.indexOf(sl);
			}

			break;

		default:
			break;
		}

		return -1;
	}
}
