package com.operasoft.snowboard.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.operasoft.snowboard.R;

public class SpinnerTextView extends AppView {

	private TextView mTxtView;

	public SpinnerTextView(Context ctx) {
		super(ctx);
	}

	@Override
	protected int getViewResId() {
		return R.layout.spinner_textview;
	}

	@Override
	protected void bindControls(Context ctx) {
		mTxtView = (TextView) findViewById(R.id.txtspinner);
	}

	public void setText(String txt) {
		mTxtView.setText(txt);
	}

	public View getView() {
		return mTxtView;
	}
}
