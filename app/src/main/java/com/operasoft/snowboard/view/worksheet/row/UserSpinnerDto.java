package com.operasoft.snowboard.view.worksheet.row;

import java.util.List;

import android.content.Context;

import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.view.SpinnerTextView;

public class UserSpinnerDto extends DtoAdapter<User> {
	public UserSpinnerDto(Context ctx, List<User> dtoList) {
		super(ctx, dtoList);
	}

	@Override
	public String getTitle(User user) {
		return user.getFirstName() + " " + user.getLastName();
	}

	@Override
	public void updateDisplay(SpinnerTextView view, User dto) {
		if (dto.tag != null)
			view.setBackgroundColor(0x4400ff00);
		else
			view.setBackgroundColor(0x44999999);
	}

}
