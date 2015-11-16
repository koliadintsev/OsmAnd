package com.operasoft.snowboard.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.worksheet.WorksheetView;

abstract class WorksheetPageFragment extends Fragment {

	private WorksheetView<Worksheets> mPageView;

	public Worksheets worksheet;

	protected abstract WorksheetView<Worksheets> getWorkSheetPageView();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mPageView = getWorkSheetPageView();
		return mPageView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	protected void initPage() {

	}

	@Override
	public void onResume() {
		super.onResume();
		if (worksheet != null) {
			initPage();
			mPageView.show(worksheet);
		}
	}

	public Worksheets getWorksheet() {
		if (mPageView == null) {
			mPageView = getWorkSheetPageView();
			if (mPageView == null) {
				return null;
			}
		}
		return mPageView.getDto();
	}
}
