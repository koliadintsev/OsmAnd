package com.operasoft.snowboard;

import java.util.Date;
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

import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.DropEmployees;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestManager;

public class ContractListAdapter extends ArrayAdapter<DropEmployees> {

	private Context                                       context;
	private int                                           layoutResourceId;
	private List<DropEmployees>                           data = null;
	private Drawable                                      bbkImage, bbrImage;
	private ServiceLocationDao                            serviceLocationDao;
	private ContractsDao                                  contractsDao;
	
	public ContractListAdapter(Context context, int layoutResourceId, List<DropEmployees> routes) {
		super(context, layoutResourceId, routes);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = routes; 
		serviceLocationDao = new ServiceLocationDao();
		contractsDao = new ContractsDao();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RouteHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new RouteHolder();
//			holder.txtName = (TextView) row.findViewById(R.id.text1);
//			holder.imgIcon = (ImageView) row.findViewById(R.id.iMg_arrow_dialog);
			holder.saAdrs = (TextView) row.findViewById(R.id.tv_sl_adrs);
			holder.jobNum = (TextView) row.findViewById(R.id.tv_job_number);
			holder.date = (TextView) row.findViewById(R.id.tv_date);
			row.setTag(holder);
		} else {
			holder = (RouteHolder) row.getTag();
		}
		DropEmployees drop = data.get(position);
		ServiceLocation serviceLocation = new ServiceLocation();
		serviceLocation = serviceLocationDao.getById(drop.getServiceLocationId());
		holder.saAdrs.setText(serviceLocation.getAddress());
		PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();
		PointOfInterest poi = poiMgr.getPOI(serviceLocation.getId());
		if (poi != null) {
			if (poi.getContract() != null)
				holder.jobNum.setText(poi.getContract().getJobNumber());
		}
		holder.date.setText(new Date() + "");
		if (position % 2 == 0) {
			if (bbkImage == null)
				bbkImage = context.getResources().getDrawable(R.drawable.dialog_route_black_background);
			row.setBackgroundDrawable(bbkImage);
		} else {
			if (bbrImage == null)
				bbrImage = context.getResources().getDrawable(R.drawable.dialog_route_brown_background);
			row.setBackgroundDrawable(bbrImage);
		}
		return row;
	}

	static class RouteHolder {
		TextView saAdrs;
		TextView jobNum;
		TextView date;
		ImageView imgIcon;
	}

}