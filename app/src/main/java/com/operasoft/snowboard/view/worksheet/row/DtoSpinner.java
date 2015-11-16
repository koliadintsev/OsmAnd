package com.operasoft.snowboard.view.worksheet.row;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

import com.operasoft.snowboard.database.Dto;

public class DtoSpinner<T extends Dto> extends Spinner {

	public DtoSpinner(Context context) {
		super(context);
	}

	public DtoSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DtoSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setDtoAdapter(DtoAdapter<T> adapter) {
		super.setAdapter(adapter);
		idBySequence.clear();
		int seq = 0;
		for (T dto : adapter.dtos) {
			idBySequence.put(dto.getId(), seq++);
		}
	}

	public void setValueById(String id) {
		if (id == null || !idBySequence.containsKey(id))
			return;
		final int seq = idBySequence.get(id);
		setSelection(seq);
	}

	public String getValueId() {
		if (getSelectedView() == null || getSelectedView().getTag() == null)
			return null;
		return ((Dto) getSelectedView().getTag()).getId();
	}

	public T getSelectedDto() {
		if (getSelectedView() == null || getSelectedView().getTag() == null)
			return null;
		return ((T) getSelectedView().getTag());
	}

	HashMap<String, Integer> idBySequence = new HashMap<String, Integer>();

}
