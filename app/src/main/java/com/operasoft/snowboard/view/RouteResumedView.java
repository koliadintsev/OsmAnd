package com.operasoft.snowboard.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.operasoft.snowboard.R;

public class RouteResumedView extends AppView implements OnClickListener {

	private Button btnMinus6, btnMinus5, btnMinus4, btnMinus3, btnMinus2, btnMinus1, btnNone;

	public RouteResumedView(Context ctx) {
		super(ctx);
	}

	public RouteResumedView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RouteResumedView(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
	}

	@Override
	protected int getViewResId() {
		return R.layout.dialog_route_serviced;
	}

	@Override
	protected void bindControls(Context ctx) {
		btnMinus6 = (Button) findViewById(R.id.btnDayminus6);
		btnMinus5 = (Button) findViewById(R.id.btnDayminus5);
		btnMinus4 = (Button) findViewById(R.id.btnDayminus4);
		btnMinus3 = (Button) findViewById(R.id.btnDayminus3);
		btnMinus2 = (Button) findViewById(R.id.btnDayminus2);
		btnMinus1 = (Button) findViewById(R.id.btnDayminus1);
		btnNone = (Button) findViewById(R.id.btnslnone);

		btnNone.setOnClickListener(this);
		btnMinus6.setOnClickListener(this);
		btnMinus5.setOnClickListener(this);
		btnMinus4.setOnClickListener(this);
		btnMinus3.setOnClickListener(this);
		btnMinus2.setOnClickListener(this);
		btnMinus1.setOnClickListener(this);

		final SimpleDateFormat sdf = new SimpleDateFormat("E");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		btnMinus1.setText(sdf.format(calendar.getTime()));

		calendar.add(Calendar.DAY_OF_YEAR, -1);
		btnMinus2.setText(sdf.format(calendar.getTime()));

		calendar.add(Calendar.DAY_OF_YEAR, -1);
		btnMinus3.setText(sdf.format(calendar.getTime()));

		calendar.add(Calendar.DAY_OF_YEAR, -1);
		btnMinus4.setText(sdf.format(calendar.getTime()));

		calendar.add(Calendar.DAY_OF_YEAR, -1);
		btnMinus5.setText(sdf.format(calendar.getTime()));

		calendar.add(Calendar.DAY_OF_YEAR, -1);
		btnMinus6.setText(sdf.format(calendar.getTime()));

	}

	@Override
	public void onClick(View v) {

		if (listener == null)
			return;
		if (v == btnMinus6)
			listener.onSelectServiceCompleted(6);
		else if (v == btnMinus5)
			listener.onSelectServiceCompleted(5);
		else if (v == btnMinus4)
			listener.onSelectServiceCompleted(4);
		else if (v == btnMinus3)
			listener.onSelectServiceCompleted(3);
		else if (v == btnMinus2)
			listener.onSelectServiceCompleted(2);
		else if (v == btnMinus1)
			listener.onSelectServiceCompleted(1);
		else if (v == btnNone)
			listener.onSelectServicedNone();
	}

	public RouteResumedListener listener;

	public interface RouteResumedListener {
		void onSelectServiceCompleted(int dayInPast);

		void onSelectServicedNone();

	}

}
