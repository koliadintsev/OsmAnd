package com.operasoft.snowboard.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.operasoft.snowboard.database.User;

public class UserAdapter extends BaseAdapter {

	final ArrayList<User> mUsers = new ArrayList<User>();

	private final Context mContext;

	public UserAdapter(Context ctx, List<User> users) {
		super();
		mUsers.addAll(users);
		mContext = ctx;
	}

	@Override
	public int getCount() {
		return mUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return mUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UserView userView = null;
		if (convertView != null) {
			userView = (UserView) convertView;
		} else {
			userView = new UserView(mContext);
		}
		userView.show(mUsers.get(position));
		return userView;
	}

}
