package com.operasoft.snowboard.maplayers;

import net.osmand.plus.views.OsmandMapTileView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.engine.VehicleLocation;
import com.operasoft.snowboard.engine.VehicleLocation.Status;

public class VehicleIcon {

	static final private float NAME_TEXT_SIZE = 15.0f;

	private Paint paintText = new Paint();
	public int iconId = R.drawable.red_dot;
	private Bitmap bitmap;
	
	public VehicleIcon(Status status) {
		paintText.setColor(Color.BLACK);
		paintText.setTextSize(NAME_TEXT_SIZE);

		switch (status) {
			case IN_MOVEMENT:
				iconId = R.drawable.vehicle_movement;
				break;
			case STOPPED_LESS_THAN_30:
				iconId = R.drawable.vehicle_less_30;
				break;
			case STOPPED_LESS_THAN_60:
				iconId = R.drawable.vehicle_less_60;
				break;
			case STOPPED_LESS_THAN_DAY:
				iconId = R.drawable.vehicle_less_day;
				break;
			case STOPPED_MORE_THAN_DAY:
				iconId = R.drawable.vehicle_more_day;
				break;
		}
	}

	public void draw(Canvas canvas, OsmandMapTileView view, VehicleLocation location) {
		float x = view.getRotatedMapXForPoint(location.getLatitude(), location.getLongitude());
		float y = view.getRotatedMapYForPoint(location.getLatitude(), location.getLongitude());

		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(view.getResources(), iconId);
		}
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		if (view.getZoom() >= 18) {
			String name = location.getName();
			canvas.drawText(name, x, y - (bitmap.getHeight() / 2), paintText);
		}
	}

}
