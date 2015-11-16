package com.operasoft.snowboard.view.worksheet;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.WorksheetLabour;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.worksheet.row.LabourRowView;
import com.operasoft.snowboard.view.worksheet.row.RowPageView;

public class LabourListPageView extends ListRowPageView<Worksheets, WorksheetLabour> {

	public LabourListPageView(Context context) {
		super(context);
	}

	@Override
	protected List<WorksheetLabour> getRowDtos() {
		return this.dto.getWorksheetLabourList();
	}

	public List<User> users;
	public List<Products> products;

	@Override
	protected RowPageView<WorksheetLabour> getNewRowView() {
		LabourRowView labourView = new LabourRowView(getContext());
		if (users != null)
			labourView.initUsers(users);
		if (products != null)
			labourView.initProducts(products);
		return labourView;
	}

	@Override
	public int getViewResourceId() {
		return R.layout.worksheet_labour;
	}

	@Override
	public void bindListControls(Context ctx) {

	}

	@Override
	protected WorksheetLabour getNewRowDto() {
		return new WorksheetLabour();
	}

	@Override
	protected ViewGroup getContainerView() {
		return (ViewGroup) findViewById(R.id.lstlabours);
	}

}
