package com.operasoft.snowboard.view.worksheet;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.EquipmentTypes;
import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.WorksheetEquipment;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.worksheet.row.EquipmentRowView;
import com.operasoft.snowboard.view.worksheet.row.RowPageView;

public class EquipmentListPageView extends ListRowPageView<Worksheets, WorksheetEquipment> {

	private LinearLayout mLstEquipments;
	public List<EquipmentTypes> equipments;
	public List<User> users;
	public List<Vehicle> vehicles;
	public List<Products> products;

	public EquipmentListPageView(Context ctx) {
		super(ctx);
	}

	@Override
	public int getViewResourceId() {
		return R.layout.worksheet_equipment;
	}

	@Override
	public void bindListControls(Context ctx) {
		mLstEquipments = (LinearLayout) findViewById(R.id.lstequipments);
	}

	@Override
	protected List<WorksheetEquipment> getRowDtos() {
		return this.dto.getWorksheetEquipmentList();
	}

	@Override
	protected RowPageView<WorksheetEquipment> getNewRowView() {
		EquipmentRowView equipmentView = new EquipmentRowView(getContext());
		equipmentView.listener = this;
		if (users != null)
			equipmentView.initUsers(users);
		if (equipments != null)
			equipmentView.initEquipments(equipments);
		if (vehicles != null)
			equipmentView.initVehicles(vehicles);
		if (products != null)
			equipmentView.initProducts(products);
		return equipmentView;
	}

	@Override
	protected WorksheetEquipment getNewRowDto() {
		return new WorksheetEquipment();
	}

	@Override
	protected ViewGroup getContainerView() {
		return mLstEquipments;
	}

}
