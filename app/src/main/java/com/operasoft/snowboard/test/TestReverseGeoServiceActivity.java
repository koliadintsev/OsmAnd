package com.operasoft.snowboard.test;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.operasoft.android.service.ReverseGeocodingService;
import com.operasoft.android.service.ReverseGeocodingService.ReverseGeocodingBinder;
import com.operasoft.geom.Point;
import com.operasoft.geom.StreetPoint;

/**
 * @author dounaka
 *
 */
public class TestReverseGeoServiceActivity extends Activity implements OnClickListener {

	private ListView mLst;

	private Button mBtnRequest;
	private BroadcastReceiver mReceiver;
	private LocalBroadcastManager mLocalBroadcastManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout mainPanel = new LinearLayout(this);
		mainPanel.setOrientation(LinearLayout.VERTICAL);
		setContentView(mainPanel);

		mBtnRequest = new Button(this);

		mBtnRequest.setWidth(600);
		mBtnRequest.setText("send requests");
		mBtnRequest.setOnClickListener(this);
		mainPanel.addView(mBtnRequest);

		mLst = new ListView(this);
		mainPanel.addView(mLst);

		mLst.setBackgroundColor(Color.BLACK);

		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ReverseGeocodingService.ACTION_FOUND_ADDRESS);
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(ReverseGeocodingService.ACTION_FOUND_ADDRESS)) {
					StreetPoint streetpt = (StreetPoint) intent.getExtras().get(ReverseGeocodingService.STREET_POINT);
					positions.put(streetpt.getId(), streetpt);
					refreshList();
				}
			}
		};
		mLocalBroadcastManager.registerReceiver(mReceiver, filter);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
	}

	HashMap<String, StreetPoint> positions = new HashMap<String, StreetPoint>();

	@Override
	public void onClick(View v) {
		double startlng = -73.60, startlat = 45.54;
		double lat, lng;
		int random = (int) (Math.random() * 10);

		Point point = null;
		StreetPoint streetPoint = null;
		double[] diffs = new double[] { 0.001, 0.002, 0.003, 0.004, 0.005, 0.006 };
		for (double diff : diffs) {
			point = new Point();
			point.latitude = startlat + diff * random;
			point.longitude = startlng + diff * random;

			streetPoint = mService.getStreetName(point);
			if (streetPoint != null) {
				positions.put(streetPoint.getId(), streetPoint);
			}
		}
		refreshList();
	}

	private void refreshList() {
		final ArrayList<StreetPoint> points = new ArrayList<StreetPoint>();
		points.addAll(positions.values());
		BaseAdapter pointAdapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return points.size();
			}

			@Override
			public Object getItem(int position) {
				return points.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView txt = new TextView(TestReverseGeoServiceActivity.this);
				if (convertView != null) {
					txt = (TextView) convertView;
				} else
					txt = new TextView(TestReverseGeoServiceActivity.this);
				StreetPoint point = points.get(position);
				txt.setText(point.latitude + ":" + point.longitude + point.street);
				return txt;
			}
		};
		mLst.setAdapter(pointAdapter);
	}

	ReverseGeocodingService mService;
	boolean mBound = false;

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, ReverseGeocodingService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			ReverseGeocodingBinder binder = (ReverseGeocodingBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	public static void main(String[] args) {
		System.out.println("test ---- ");
	}
}
