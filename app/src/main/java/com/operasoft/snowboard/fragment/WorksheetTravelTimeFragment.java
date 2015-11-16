package com.operasoft.snowboard.fragment;

import java.util.ArrayList;

import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.worksheet.TravelTimeListPageView;
import com.operasoft.snowboard.view.worksheet.WorksheetView;

public class WorksheetTravelTimeFragment extends WorksheetPageFragment implements ListPage {

	public ArrayList<User> users = new ArrayList<User>();
	TravelTimeListPageView travelTimeView = null;

	@Override
	protected WorksheetView<Worksheets> getWorkSheetPageView() {
		travelTimeView = new TravelTimeListPageView(getActivity());
		travelTimeView.users = users;
		return travelTimeView;
	}

	@Override
	protected void initPage() {
		users.clear();
		//
		users.addAll((new UsersDao()).getTopActiveEmployees(worksheet.getContractId(), worksheet.getStartDate()));
	}

	@Override
	public void addNewRow() {
		travelTimeView.addNewRow();
	}
}
