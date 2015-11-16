package com.operasoft.snowboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.operasoft.snowboard.R;

/**
 * @author dounaka
 * Generic view to display information
 * Title and icon are displayed in Header
 */
public class InfoView extends AppView {

	private TextView mTxtLine01, mTxtLine02, mTxtLine03;

	public InfoView(Context ctx) {
		super(ctx);
	}

	public InfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InfoView(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
	}

	@Override
	protected int getViewResId() {
		return R.layout.dialog_info;
	}

	@Override
	protected void bindControls(Context ctx) {
		mTxtLine01 = (TextView) findViewById(R.id.txtinfoline01);
		mTxtLine02 = (TextView) findViewById(R.id.txtinfoline02);
		mTxtLine03 = (TextView) findViewById(R.id.txtinfoline03);
	}

	public void display(String line01, String line02, String line03) {
		if (line01 == null)
			mTxtLine01.setVisibility(View.INVISIBLE);
		else
			mTxtLine01.setText(line01);

		if (line02 == null)
			mTxtLine02.setVisibility(View.INVISIBLE);
		else
			mTxtLine02.setText(line02);

		if (line03 == null)
			mTxtLine03.setVisibility(View.INVISIBLE);
		else
			mTxtLine03.setText(line03);

	}
}
