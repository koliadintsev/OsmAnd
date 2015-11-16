package com.operasoft.snowboard.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.operasoft.snowboard.R;

/**
 * @author dounaka
 * Generic view to display app header
 */
public class HeaderView extends AppView {
	private TextView mTxtTitle;
	private ImageView mImgClose, mImgIcon;

	public HeaderView(Context ctx, int iconid, String title) {
		super(ctx);
		mTxtTitle.setText(title);
		if (iconid != -1)
			mImgIcon.setImageResource(iconid);
		else
			mImgIcon.setVisibility(View.GONE);
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		mImgClose.setOnClickListener(listener);
	}

	@Override
	protected int getViewResId() {
		return R.layout.dialog_header_menu;
	}

	@Override
	protected void bindControls(Context ctx) {
		mImgClose = (ImageView) findViewById(R.id.iv_dhm_cancel);
		mImgIcon = (ImageView) findViewById(R.id.iv_dhm_icon);
		mTxtTitle = (TextView) findViewById(R.id.tv_dhm_title);
		mTxtTitle.requestFocus();

	}

}
