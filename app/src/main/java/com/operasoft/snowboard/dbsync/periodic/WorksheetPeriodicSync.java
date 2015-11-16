package com.operasoft.snowboard.dbsync.periodic;

import java.util.List;

import org.json.JSONArray;

import android.util.Log;

import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.database.RouteDao;
import com.operasoft.snowboard.database.RouteSequence;
import com.operasoft.snowboard.database.RouteSequenceDao;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.WorksheetEquipment;
import com.operasoft.snowboard.database.WorksheetEquipmentDao;
import com.operasoft.snowboard.database.WorksheetLabour;
import com.operasoft.snowboard.database.WorksheetLabourDao;
import com.operasoft.snowboard.database.WorksheetMaterial;
import com.operasoft.snowboard.database.WorksheetMaterialDao;
import com.operasoft.snowboard.database.WorksheetTravelTime;
import com.operasoft.snowboard.database.WorksheetTravelTimeDao;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.database.WorksheetsDao;
import com.operasoft.snowboard.dbsync.DbSyncManager;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.onetime.RouteSequenceOneTimeSync;

/**
 * This class implements the worksheet periodic sync logic. When an existing worksheet is being updated on Snowboard,
 * we need to clean all of its associated entries our local database and request a one-time update
 * to retrieve the latest info for this worksheet on Snowman.
 * @author Christian
 *
 */
public class WorksheetPeriodicSync extends DefaultPeriodicSync {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private WorksheetsDao dao;
	private WorksheetLabourDao labourDao = null;
	private WorksheetEquipmentDao equipmentDao = null;
	private WorksheetMaterialDao materialDao = null;
	private WorksheetTravelTimeDao travelTimeDao = null;
	
	public WorksheetPeriodicSync() {
		super("Worksheet", new WorksheetsDao());
		//setSuperUserOnly(true);
		setSyncChildren(true);
		this.dao = (WorksheetsDao) super.dao;
		labourDao = new WorksheetLabourDao();
		equipmentDao = new WorksheetEquipmentDao();
		materialDao = new WorksheetMaterialDao();
		travelTimeDao = new WorksheetTravelTimeDao();
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		List<Worksheets> list = dtoParser.parseWorksheets(jsArray);
		
		for (Worksheets dto : list) {
			Log.d("Worksheet Sync", "Received " + dto.getId());
			
			// We need to clear the information related to this worksheet in our DB
			equipmentDao.deleteListAttachedWithWorksheet(dto.getId());
			labourDao.deleteListAttachedWithWorksheet(dto.getId());
			materialDao.deleteListAttachedWithWorksheet(dto.getId());
			travelTimeDao.deleteListAttachedWithWorksheet(dto.getId());
			
			// Keep the general information about this worksheet to minimize future syncs
			dao.insertOrReplace(dto);
			
			if (dto.isOpen()) {
				// The Worksheet is still opened, let's add the new version in our DB
				for (WorksheetLabour labour : dto.getWorksheetLabourList()) {
					labourDao.insertOrReplace(labour);
				}
				for (WorksheetEquipment equipment : dto.getWorksheetEquipmentList()) {
					equipmentDao.insertOrReplace(equipment);
				}
				for (WorksheetMaterial material : dto.getWorksheetMaterialList()) {
					materialDao.insertOrReplace(material);
				}
				for (WorksheetTravelTime traveltime : dto.getWorksheetTravelTimeList()) {
					travelTimeDao.insertOrReplace(traveltime);
				}
			}
		}
	}	
}
