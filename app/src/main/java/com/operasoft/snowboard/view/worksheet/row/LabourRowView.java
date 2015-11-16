package com.operasoft.snowboard.view.worksheet.row;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.WorksheetLabour;

public class LabourRowView extends RowPageView<WorksheetLabour> {

	private DtoSpinner<Products> mSpinnerProduct;
	private DtoSpinner<User> mSpinnerEmployee;
	Spinner mSpinnerMinutes;
	private ImageView imgdelete;
	private EditText mEditHours;
	private TextView mTxtDate;

	public LabourRowView(Context context) {
		super(context);
	}

	@Override
	public int getViewResourceId() {
		return R.layout.worksheet_labour_row;
	}

	public void initUsers(List<User> employees) {
		mSpinnerEmployee.setDtoAdapter(new UserSpinnerDto(getContext(), employees));

	}

	public void initProducts(List<Products> products) {
		mSpinnerProduct.setDtoAdapter(new DtoAdapter<Products>(getContext(), products) {
			@Override
			public String getTitle(Products product) {
				return product.getName();
			}
		});
	}

	@Override
	public void bindRowControls(Context ctx) {
		mSpinnerEmployee = (DtoSpinner<User>) findViewById(R.id.spinneremployee);
		mSpinnerProduct = (DtoSpinner<Products>) findViewById(R.id.spinnertask);
		mSpinnerMinutes = (Spinner) findViewById(R.id.spinnerminutes);
		mTxtDate = (TextView) findViewById(R.id.editdate);
		mEditHours = (EditText) findViewById(R.id.edithours);
		imgdelete = (ImageView) findViewById(R.id.imgdelete);
		imgdelete.setOnClickListener(this);
		imgdelete.setClickable(true);
		mSpinnerMinutes.setAdapter(new MinutesAdapter(getContext()));
		mTxtDate.setOnClickListener(this.dateInputClickListener);
		mEditHours.setOnFocusChangeListener(selectTextListener);
		mEditHours.setOnTouchListener(selectTextListener);
	}

	@Override
	protected void display(WorksheetLabour labour) {
		mSpinnerEmployee.setValueById(labour.getUserId());
		mSpinnerProduct.setValueById(labour.getProductId());
		setDate(mTxtDate, labour.getLabourDate());
		mEditHours.setText("" + (int) labour.getHours());
		setMinutes(mSpinnerMinutes, labour.getHours());
	}

	@Override
	protected WorksheetLabour update(WorksheetLabour labour) {
		labour.setLabourDate(mTxtDate.getText().toString());
		labour.setUserId(mSpinnerEmployee.getValueId());
		labour.setProductId(mSpinnerProduct.getValueId());
		int hours = 0;
		try {
			String nbHours = mEditHours.getText().toString();
			hours = Integer.parseInt(nbHours);
		} catch (NumberFormatException e) {}
		labour.setHours(hours + getMinutes(mSpinnerMinutes));
		return labour;
	}

	@Override
	protected View getDeleteRowView() {
		return imgdelete;
	}

}
