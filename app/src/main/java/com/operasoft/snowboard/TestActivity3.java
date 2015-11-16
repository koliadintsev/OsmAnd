package com.operasoft.snowboard;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.osmand.access.AccessibleActivity;

/**
 * Created by Michael on 16.11.2015.
 */
public class TestActivity3 extends AccessibleActivity implements SensorEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Delete old buttons
        ImageView imageView = (ImageView) findViewById(R.id.imgView_wc_login_text);
        ImageButton imageButton1 = (ImageButton) findViewById(R.id.button_punch_clock);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.button_refuel);
        Button button1 = (Button) findViewById(R.id.btn_complete_route);
        View view1 = findViewById(R.id.btn_breadcrumb);

        imageView.setVisibility(View.INVISIBLE);
        imageButton1.setVisibility(View.INVISIBLE);
        imageButton2.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);
        view1.setVisibility(View.INVISIBLE);

        //Add new buttons
        View newView = findViewById(R.id.end_route_3);

        addContentView(newView, new ViewGroup.LayoutParams(212,192));






    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
