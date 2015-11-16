package com.operasoft.snowboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public abstract class AppView extends FrameLayout {
	public AppView(Context ctx) {
		super(ctx);
		initView(ctx);
	}

	public AppView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public AppView(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
		initView(context);
	}

	private void initView(final Context ctx) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(getViewResId(), this, true);
		bindControls(ctx);
	}

	protected abstract int getViewResId();

	protected abstract void bindControls(Context ctx);
}
