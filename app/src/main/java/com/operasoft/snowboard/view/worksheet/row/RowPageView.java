package com.operasoft.snowboard.view.worksheet.row;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.operasoft.snowboard.database.Dto;
import com.operasoft.snowboard.view.worksheet.WorksheetView;

public abstract class RowPageView<R extends Dto> extends WorksheetView<R> implements OnClickListener {

	public RowPageView(Context ctx) {
		super(ctx);
	}

	private View deleteRowView;

	@Override
	public final void bindControls(Context ctx) {
		bindRowControls(ctx);
		deleteRowView = getDeleteRowView();
		deleteRowView.setOnClickListener(this);
		deleteRowView.setClickable(true);
	}

	protected abstract View getDeleteRowView();

	public abstract void bindRowControls(Context ctx);

	public RowListener<R> listener;

	@Override
	public void onClick(View v) {
		if (deleteRowView == v && listener != null)
			listener.ondelete(this.dto, this);
	}

}
