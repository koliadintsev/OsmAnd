package com.operasoft.snowboard;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.operasoft.snowboard.database.Route;

public class RouteListAdapter extends ArrayAdapter<Route> {

	Context context;
	int layoutResourceId;
	List<Route> data = null;
	View mView;
	Drawable bbkImage, bbrImage;

	public RouteListAdapter(Context context, int layoutResourceId, List<Route> routes, View view) {
		super(context, layoutResourceId, routes);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = routes;
		this.mView = view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RouteHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RouteHolder();
			holder.txtName = (TextView) row.findViewById(R.id.text1);
			holder.imgIcon = (ImageView) row.findViewById(R.id.iMg_arrow_dialog);
			row.setTag(holder);
		} else {
			holder = (RouteHolder) row.getTag();
		}
		Route route = data.get(position);
		holder.txtName.setText(route.getName());
		if (position % 2 == 0) {
			if (bbkImage == null)
				bbkImage = mView.getResources().getDrawable(R.drawable.dialog_route_black_background);
			row.setBackgroundDrawable(bbkImage);
		} else {
			if (bbrImage == null)
				bbrImage = mView.getResources().getDrawable(R.drawable.dialog_route_brown_background);
			row.setBackgroundDrawable(bbrImage);
		}
		return row;
	}

	static class RouteHolder {
		TextView txtName;
		ImageView imgIcon;
	}

}