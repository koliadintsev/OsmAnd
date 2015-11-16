package com.operasoft.snowboard.view.worksheet;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.operasoft.snowboard.database.Dto;
import com.operasoft.snowboard.view.worksheet.row.RowListener;
import com.operasoft.snowboard.view.worksheet.row.RowPageView;

/**
 * @author dounaka
 *
 * @param <T>
 * @param <R>
 */
public abstract class ListRowPageView<T extends Dto, R extends Dto> extends WorksheetView<T> implements RowListener<R> {

	protected final ArrayList<RowPageView<R>> rowViews = new ArrayList<RowPageView<R>>();
	protected ViewGroup mPanelRows;

	protected abstract void bindListControls(Context ctx);

	protected abstract List<R> getRowDtos();

	protected abstract RowPageView<R> getNewRowView();

	protected abstract R getNewRowDto();

	protected abstract ViewGroup getContainerView();

	public ListRowPageView(Context context) {
		super(context);
	}

	@Override
	protected final void display(T dto) {
		rowViews.clear();
		mPanelRows.removeAllViews();
		for (R rowDto : getRowDtos()) {
			createRowView(rowDto);
		}
	}

	@Override
	protected final T update(T worksheet) {
		getRowDtos().clear();
		for (WorksheetView<R> rowView : rowViews) {
			getRowDtos().add(rowView.getDto());
		}
		return worksheet;
	}

	public final WorksheetView<R> createRowView(R rowDto) {
		RowPageView<R> view = getNewRowView();
		view.listener = this;
		rowViews.add(view);
		mPanelRows.addView(view);
		view.show(rowDto);
		return view;
	}

	@Override
	public void ondelete(R rowdto, View v) {
		mPanelRows.removeView(v);
		rowViews.remove(v);
	}

	@Override
	public final void bindControls(Context ctx) {
		bindListControls(ctx);
		mPanelRows = getContainerView();
	}

	public void addNewRow() {
		View v2 = createRowView(getNewRowDto());
		v2.requestFocus();
	}

}
