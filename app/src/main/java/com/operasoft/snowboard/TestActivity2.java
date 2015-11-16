package com.operasoft.snowboard;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.ViewGroup;

import net.osmand.access.AccessibleActivity;

/**
 * Created by Michael on 16.11.2015.
 */
public class TestActivity2 extends AccessibleActivity implements SensorEventListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        addContentView(R.layout.end_route_2, new ViewGroup.LayoutParams(800, 600));


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onTestActivity3 (){
        Intent intent = new Intent(getApplicationContext(), TestActivity3.class);
        startActivity(intent);
    }

}
