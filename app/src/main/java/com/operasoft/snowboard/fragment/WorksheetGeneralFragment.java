package com.operasoft.snowboard.fragment;

import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.worksheet.GeneralPageView;
import com.operasoft.snowboard.view.worksheet.WorksheetView;

public class WorksheetGeneralFragment extends WorksheetPageFragment {

	GeneralPageView generalPage = null;

	@Override
	protected WorksheetView<Worksheets> getWorkSheetPageView() {
		generalPage = new GeneralPageView(getActivity());
		return generalPage;
	}

}