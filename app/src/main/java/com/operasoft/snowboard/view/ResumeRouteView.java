package com.operasoft.snowboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.operasoft.snowboard.R;

public class ResumeRouteView extends AppView implements OnClickListener {

	private Button btnStartNew, btnResume;

	public ResumeRouteView(Context ctx) {
		super(ctx);
	}

	public ResumeRouteView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResumeRouteView(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
	}

	@Override
	protected int getViewResId() {
		return R.layout.dialog_route_resume;
	}

	@Override
	protected void bindControls(Context ctx) {
		btnStartNew = (Button) findViewById(R.id.btn_start_as_new);
		btnResume = (Button) findViewById(R.id.btn_resume_route);
		btnResume.setOnClickListener(this);
		btnStartNew.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (listener == null)
			return;
		if (v == btnStartNew)
			listener.onStartAsNew();
		else if (v == btnResume)
			listener.onResumeRoute();
	}

	public ResumeRouteListener listener;

	public interface ResumeRouteListener {
		void onStartAsNew();

		void onResumeRoute();

	}

}
