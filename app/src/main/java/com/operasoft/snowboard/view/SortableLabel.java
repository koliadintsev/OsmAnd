package com.operasoft.snowboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.operasoft.snowboard.R;

/**
 * @author dounaka
 * label used for a grid with a picture to control the columns sort
 */
public class SortableLabel extends AppView implements OnClickListener {
	private ImageView mBtnUpDown;

	private TextView mTxtLabel;
	private String sortedField;
	private boolean up = true;

	public SortableLabel(Context ctx) {
		super(ctx);
	}

	public SortableLabel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SortableLabel(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
	}

	@Override
	protected int getViewResId() {
		return R.layout.sortable_label;
	}

	@Override
	protected void bindControls(Context ctx) {

		mBtnUpDown = (ImageView) findViewById(R.id.btnsort);
		mBtnUpDown.setClickable(true);
		mBtnUpDown.setOnClickListener(this);
		mBtnUpDown.setVisibility(INVISIBLE);

		mTxtLabel = (TextView) findViewById(R.id.txtlabel);
		mTxtLabel.setClickable(true);
		mTxtLabel.setOnClickListener(this);

	}

	public void setText(String displayName, String fieldname) {
		mTxtLabel.setText(displayName);
		sortedField = fieldname;
		// setUp();
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnUpDown || v == mTxtLabel) {
			change();
			if (listener != null)
				listener.sort(this);
		}
	}

	public String getSortedField() {
		return this.sortedField;
	}

	public boolean isUp() {
		return this.up;
	}

	private void displayImage() {
		mBtnUpDown.setVisibility(VISIBLE);
		if (up)
			mBtnUpDown.setImageResource(R.drawable.ic_arrow_up);
		else
			mBtnUpDown.setImageResource(R.drawable.ic_arrow_down);
	}

	public void setUp() {
		this.up = true;
		displayImage();
	}

	public void setDown() {
		this.up = false;
		displayImage();
	}

	public void noSort() {
		this.up = true;
		mBtnUpDown.setVisibility(INVISIBLE);
	}

	public void change() {
		if (mBtnUpDown.getVisibility() == INVISIBLE) {
			mBtnUpDown.setVisibility(VISIBLE);
			this.up = true;
		} else
			this.up = !this.up;
		displayImage();
	}

	public SortableLabelListener listener;

	public interface SortableLabelListener {
		void sort(SortableLabel sortableLabel);
	}

}
