package com.operasoft.snowboard.dbsync.periodic;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.util.Log;

import com.operasoft.snowboard.database.WorkOrder;
import com.operasoft.snowboard.database.WorkOrderDao;
import com.operasoft.snowboard.database.WorkOrderItem;
import com.operasoft.snowboard.database.WorkOrderItemDao;
import com.operasoft.snowboard.database.WorkOrderPicture;
import com.operasoft.snowboard.database.WorkOrderPictureDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

public class WorkOrderPeriodicSync extends DefaultPeriodicSync {
	private JsonDtoParser dtoParser = new JsonDtoParser();
	private WorkOrderDao dao;
	private WorkOrderItemDao itemDao = null;
	private WorkOrderPictureDao pictureDao = null;
	
	public WorkOrderPeriodicSync() {
		super("WorkOrder", new WorkOrderDao());
		setSuperUserOnly(true);
		setSyncChildren(true);
		this.dao = (WorkOrderDao) super.dao;
		itemDao = new WorkOrderItemDao();
		pictureDao = new WorkOrderPictureDao();
	}

	
	@Override
	protected ArrayList<NameValuePair> buildRequestParams() {
		ArrayList<NameValuePair> params = super.buildRequestParams();

		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION+"[contains]",pictureDao.model));
		//params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION+"[contains]",itemDao.model));

		return params;
	}
	
	
	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		List<WorkOrder> list = dtoParser.parseWorkOrders(jsArray);
		
		for (WorkOrder dto : list) {
			Log.d("WorkOrder Sync", "Received " + dto.getId());

			if (dto.isOpen()) {
				dao.insertOrReplace(dto);
				
				// The WorkOrder is still opened, let's add the new version in our DB
				for (WorkOrderItem item : dto.getWorkOrderItemList()) {
					itemDao.insertOrReplace(item);
				}
				for (WorkOrderPicture picture : dto.getWorkOrderItemPictures()) {
					pictureDao.insertOrReplace(picture);
				}
			} else {
				itemDao.deleteListAttachedWithWorkorder(dto.getId());
				pictureDao.deleteListAttachedWithWorkorder(dto.getId());
			}
		}
	}
}
