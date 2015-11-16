package com.operasoft.snowboard;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.ViewGroup;

import net.osmand.access.AccessibleActivity;
import net.osmand.access.NavigationInfo;
import net.osmand.plus.activities.MapActivityActions;
import net.osmand.plus.activities.MapActivityLayers;
import net.osmand.plus.views.OsmandMapTileView;

/**
 * Created by Michael on 16.11.2015.
 */
public class TestActivity1 extends AccessibleActivity implements SensorEventListener {
    public static OsmandMapTileView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        addContentView(R.layout.end_route_1, new ViewGroup.LayoutParams(800, 600));

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void onInspectionDone () {
        Intent intent = new Intent(getApplicationContext(), TestActivity2.class);
        startActivity(intent);
    }

}
