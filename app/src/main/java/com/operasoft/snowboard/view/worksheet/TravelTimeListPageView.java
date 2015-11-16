package com.operasoft.snowboard.view.worksheet;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.WorksheetTravelTime;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.worksheet.row.RowPageView;
import com.operasoft.snowboard.view.worksheet.row.TravelTimeRowView;

/**
 * @author dounaka
 *
 */
public class TravelTimeListPageView extends ListRowPageView<Worksheets, WorksheetTravelTime> {

	public List<User> users;

	ViewGroup mPanelRowViews;

	public TravelTimeListPageView(Context ctx) {
		super(ctx);
	}

	@Override
	public void bindListControls(Context ctx) {
		mPanelRowViews = (ViewGroup) findViewById(R.id.lstworktasks);
	}

	@Override
	public int getViewResourceId() {
		return R.layout.worksheet_traveltime;
	}

	@Override
	protected List<WorksheetTravelTime> getRowDtos() {
		return this.dto.getWorksheetTravelTimeList();
	}

	@Override
	protected RowPageView<WorksheetTravelTime> getNewRowView() {
		TravelTimeRowView travelView = new TravelTimeRowView(getContext());
		travelView.listener = this;
		if (users != null)
			travelView.initUsers(users);
		return travelView;
	}

	@Override
	protected WorksheetTravelTime getNewRowDto() {
		return new WorksheetTravelTime();
	}

	@Override
	protected ViewGroup getContainerView() {
		return mPanelRowViews;
	}

}
