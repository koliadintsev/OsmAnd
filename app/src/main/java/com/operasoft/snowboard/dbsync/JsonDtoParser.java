package com.operasoft.snowboard.dbsync;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.*;
import com.operasoft.snowboard.dbsync.push.LoginPushSync;
import com.operasoft.snowboard.util.JSONParser;

/**
 * This class creates the various DTO objects received during a DB sync operation from their JSON representation.
 * 
 * @author Christian
 * 
 */
public class JsonDtoParser extends JSONParser {


	private ContractsDao contractDao = new ContractsDao();
	private GpsConfigDao androidDao = new GpsConfigDao();
	private ServiceActivityDao saDao = new ServiceActivityDao();
	private ServiceLocationDao slDao = new ServiceLocationDao();
	private RouteDao routeDao = new RouteDao();
	private MarkerInstallationDao miDao = new MarkerInstallationDao();
	private GeofenceDao geofenceDao = new GeofenceDao();
	private RouteSelectedDao rsDao = new RouteSelectedDao();
	private LoginDao loginDao = new LoginDao();
	private WorksheetsDao worksheetsDao = new WorksheetsDao();
	private WorkOrderDao workOrderDao = new WorkOrderDao();
	private DeficiencyDao deficiencyDao = new DeficiencyDao();
	private UploadsDao uploadsDao = new UploadsDao();
	private AssetsDao assetsDao = new AssetsDao();
	private AssetTypesDao assetTypesDao = new AssetTypesDao();
	private DeficiencyPictureDao deficiencyPictureDao = new DeficiencyPictureDao();
	private WorkOrderItemDao itemDao = new WorkOrderItemDao();
	private WorkOrderPictureDao pictureDao = new WorkOrderPictureDao();
	
	public List<ServiceActivity> parseServiceActivities(JSONArray jsArray) throws JSONException {
		List<ServiceActivity> list = new ArrayList<ServiceActivity>(jsArray.length());

		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			ServiceActivity sa = parseServiceActivity(jsonObject);
			if (sa != null) {
				list.add(sa);
			} else {
				throw new JSONException("Invalid ServiceActivity received from server for jsonObject " + jsonObject);
			}
		}

		return list;
	}

	public ServiceActivity parseServiceActivity(JSONObject jsonObject) throws JSONException {
		return parseServiceActivity(jsonObject, "SbServiceActivity");
	}

	public ServiceActivity parseServiceActivity(JSONObject jsonObject, String model) throws JSONException {
		jsonObject = jsonObject.getJSONObject(model);
		if (jsonObject == null) {
			throw new JSONException("Failed to parse ServiceLocation");
		}
		
		return saDao.buildDto(jsonObject);
	}

	public List<Geofence> parseGeofences(JSONArray jsArray) throws JSONException {
		List<Geofence> list = new ArrayList<Geofence>(jsArray.length());

		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			Geofence sa = parseGeofence(jsonObject);
			if (sa != null) {
				list.add(sa);
			} else {
				throw new JSONException("Invalid Geofence received from server for jsonObject " + jsonObject);
			}
		}

		return list;
	}

	public Geofence parseGeofence(JSONObject jsonObject) throws JSONException {
		return parseGeofence(jsonObject, "SbGeofence");
	}

	public Geofence parseGeofence(JSONObject jsonObject, String model) throws JSONException {
		jsonObject = jsonObject.getJSONObject(model);
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Geofence");
		}
		
		return geofenceDao.buildDto(jsonObject);
	}
	
	public List<ServiceLocation> parseServiceLocations(JSONArray jsArray) throws JSONException {
		List<ServiceLocation> list = new ArrayList<ServiceLocation>(jsArray.length());

		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			ServiceLocation sa = parseServiceLocation(jsonObject);
			if (sa != null) {
				list.add(sa);
			} else {
				throw new JSONException("Invalid ServiceLocation received from server for jsonObject " + jsonObject);
			}
		}

		return list;
	}

	public ServiceLocation parseServiceLocation(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("SbServiceLocation");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse ServiceLocation");
		}
		
		return slDao.buildDto(jsonObject);
	}

	public List<Route> parseRoutes(JSONArray jsArray) throws JSONException {
		List<Route> list = new ArrayList<Route>(jsArray.length());

		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			Route route = parseRoute(jsonObject);
			if (route != null) {
				list.add(route);
			} else {
				throw new JSONException("Invalid Route received from server for jsonObject " + jsonObject);
			}
		}
		return list;
	}

	public Route parseRoute(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("SbRoute");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Route");
		}
		
		return routeDao.buildDto(jsonObject);
	}

	public List<MarkerInstallation> parseMarkerInstallation(JSONArray jsArray) throws JSONException {
		List<MarkerInstallation> list = new ArrayList<MarkerInstallation>(jsArray.length());

		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			MarkerInstallation mi = parseMarkerInstallation(jsonObject);
			if (mi != null) {
				list.add(mi);
			} else {
				throw new JSONException("Invalid MarkerInstallation received from server for jsonObject " + jsonObject);
			}
		}

		return list;
	}

	public MarkerInstallation parseMarkerInstallation(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("SbMarkerInstallation");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse MarkerInstallation");
		}
		
		return miDao.buildDto(jsonObject);
	}

	public UserWorkStatusLogs parseSnowmanUserWorkStatusLog(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("UserWorkStatusLog");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Snowman Punch");
		}

		UserWorkStatusLogs dto = new UserWorkStatusLogs();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}
	
	public ServiceActivityDetails parseSnowmanSADetailsForm(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("ServiceActivity");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Snowman ServiceActivity");
		}

		ServiceActivityDetails dto = new ServiceActivityDetails();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}

	public LoginSession parseSnowmanLoginSession(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("LoginSession");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Snowman LoginSession");
		}

		LoginSession dto = new LoginSession();
		dto.setId(parseString(jsonObject, "id"));

		return dto;
	}

	public VehicleRefuelLog parseSnowmanRefuel(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("VehicleRefuelLog");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Snowman VehicleRefuelLog");
		}

		VehicleRefuelLog dto = new VehicleRefuelLog();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}

	public MarkerInstallation parseSnowmanMarker(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("MarkerInstallation");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Snowman MarkerInstallation");
		}

		MarkerInstallation dto = new MarkerInstallation();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}

	public Callout parseSnowmanCallout(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("Callout");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Snowman Callout");
		}

		Callout dto = new Callout();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}

	public InspectionJournal parseSnowmanInspectionJournal(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("InspectionJournal");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Snowman InspectionJournal");
		}

		InspectionJournal dto = new InspectionJournal();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}

	public InspectionJournalDefect parseSnowmanInspectionJournalDefect(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("InspectionJournalDefect");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Snowman InspectionJournalDefect");
		}

		InspectionJournalDefect dto = new InspectionJournalDefect();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}

	public Damage parseSnowmanDamages(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("Damage");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Snowman VehicleRefuelLog");
		}

		Damage dto = new Damage();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}

	
	public Deficiency parseDeficiency(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("RouteDeficiency");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse RouteDeficiency");
		}
		
		Deficiency dto = deficiencyDao.buildDto(jsonObject);

		JSONArray jsArray = jsonObject.optJSONArray("DeficiencyPicture");
		if (jsArray != null) {
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject json = jsArray.getJSONObject(i);
				DeficiencyPicture picture = deficiencyPictureDao.buildDto(json);
				if (picture != null) {
					dto.add(picture);
				}
			}
		}
		return dto;
	}
	
	public TransportActivity parseSnowmanTransportActivity(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("TransportActivity");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Snowman TransportActivity");
		}

		TransportActivity dto = new TransportActivity();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}
	
// ++ AssetTypes +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public AssetTypes parseAssetTypes(JSONObject json, String model) throws JSONException {
		JSONObject jsonObj = json.getJSONObject(model);
		AssetTypes dto = assetTypesDao.buildDto(jsonObj);
		if (dto == null) {
			throw new JSONException("Invalid AssetTypes received from server for jsonObject: " + json.toString());
		}

		return dto;
	}
		
// ++ Uploads +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public Uploads parseUploads(JSONObject json, String model) throws JSONException {
		JSONObject jsonObj = json.getJSONObject(model);
		Uploads dto = uploadsDao.buildDto(jsonObj);
		if (dto == null) {
			throw new JSONException("Invalid Uploads received from server for jsonObject: " + json.toString());
		}

		return dto;
	}
// ++ WorkOrder +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public List<WorkOrder> parseWorkOrders(JSONArray jsArray) throws JSONException {
		List<WorkOrder> list = new ArrayList<WorkOrder>(jsArray.length());

		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			WorkOrder dto = parseWorkOrder(jsonObject, workOrderDao.model /*"SbWorkOrder"*/);
			if (dto != null) {
				list.add(dto);
			} else {
				throw new JSONException("Invalid WorkOrder received from server for jsonObject " + jsonObject.toString());
			}
		}
		return list;
	}
	public WorkOrder parseWorkOrder(JSONObject json, String model) throws JSONException {
		JSONObject jsonObj = json.getJSONObject(model);
		if (jsonObj == null) {
			throw new JSONException("Failed to parse WorkOrder");
		}
		
		WorkOrder dto = workOrderDao.buildDto(jsonObj);
		
		// parse WorkOrderItem
		JSONArray jsArray = json.optJSONArray(itemDao.model);
		if (jsArray != null) {
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject jsonObject = jsArray.getJSONObject(i);
				WorkOrderItem item = itemDao.buildDto(jsonObject);
				if (item != null) {
					dto.add(item);
				}
			}
		}

		// parse WorkOrderPicture
		jsArray = json.optJSONArray(pictureDao.model);
		if (jsArray != null) {
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject jsonObject = jsArray.getJSONObject(i);
				WorkOrderPicture picture = pictureDao.buildDto(jsonObject);
				if (picture != null) {
					picture.setCreatorId(dto.getCreatorId());
					dto.add(picture);
				}
			}
		}
		
		return dto;
	}
	
// ++ Asset +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public List<Assets> parseAssets(JSONArray jsArray) throws JSONException {
		List<Assets> list = new ArrayList<Assets>(jsArray.length());

		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			Assets dto = parseAsset(jsonObject, "SbAssets");
			if (dto != null) {
				list.add(dto);
			} else {
				throw new JSONException("Invalid Asset received from server for jsonObject " + jsonObject.toString());
			}
		}
		return list;
	}
	public Assets parseAsset(JSONObject json, String model) throws JSONException {
		JSONObject jsonObj = json.getJSONObject(model);
		if (jsonObj == null) {
			throw new JSONException("Failed to parse Asset");
		}
		
		Assets dto = assetsDao.buildDto(jsonObj);
		
		// parse AssetPicture
		JSONArray jsArray = json.optJSONArray("AssetPictures");
		if (jsArray != null) {
			AssetPicturesDao pictureDao = new AssetPicturesDao();
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject jsonObject = jsArray.getJSONObject(i);
				AssetPictures picture = pictureDao.buildDto(jsonObject);
				if (picture != null) {
					dto.add(picture);
				}
			}
		}
		
		return dto;
	}

// ++ WorkSheet +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public List<Worksheets> parseWorksheets(JSONArray jsArray) throws JSONException {
		List<Worksheets> list = new ArrayList<Worksheets>(jsArray.length());

		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			Worksheets dto = parseWorksheet(jsonObject, "SbWorksheet");
			if (dto != null) {
				list.add(dto);
			} else {
				throw new JSONException("Invalid Worksheet received from server for jsonObject " + jsonObject);
			}
		}
		return list;
	}
	
	public Worksheets parseWorksheet(JSONObject json, String model) throws JSONException {
		JSONObject jsonObj = json.getJSONObject(model);
		if (jsonObj == null) {
			throw new JSONException("Failed to parse Worksheets");
		}
		
		Worksheets dto = worksheetsDao.buildDto(jsonObj);
		
		// Parse Labour elements
		JSONArray jsArray = json.optJSONArray("WorksheetLabour");
		if (jsArray != null) {
			WorksheetLabourDao labourDao = new WorksheetLabourDao();
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject jsonObject = jsArray.getJSONObject(i);
				WorksheetLabour labour = labourDao.buildDto(jsonObject);
				if (labour != null) {
					dto.add(labour);
				}
			}
		}
		
		// Parse Equipment elements
		jsArray = json.optJSONArray("WorksheetEquipment");
		if (jsArray != null) {
			WorksheetEquipmentDao equipmentDao = new WorksheetEquipmentDao();
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject jsonObject = jsArray.getJSONObject(i);
				WorksheetEquipment equipment = equipmentDao.buildDto(jsonObject);
				if (equipment != null) {
					dto.add(equipment);
				}
			}
		}
		
		// Parse Material elements
		jsArray = json.optJSONArray("WorksheetMaterial");
		if (jsArray != null) {
			WorksheetMaterialDao materialDao = new WorksheetMaterialDao();
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject jsonObject = jsArray.getJSONObject(i);
				WorksheetMaterial material = materialDao.buildDto(jsonObject);
				if (material != null) {
					dto.add(material);
				}
			}
		}

		// Parse Material elements
		jsArray = json.optJSONArray("WorksheetTravelTime");
		if (jsArray != null) {
			WorksheetTravelTimeDao travelDao = new WorksheetTravelTimeDao();
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject jsonObject = jsArray.getJSONObject(i);
				WorksheetTravelTime travelTime = travelDao.buildDto(jsonObject);
				if (travelTime != null) {
					dto.add(travelTime);
				}
			}
		}
		
		return dto;
	}
	
	public WorksheetMaintenance parseWorksheetMaintenance(JSONObject jsonObject, String model) throws JSONException {
		jsonObject = jsonObject.getJSONObject(model);
		if (jsonObject == null) {
			throw new JSONException("Failed to parse ServiceLocation");
		}
		
		WorksheetMaintenance dto = new WorksheetMaintenance();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}
	public EndRoute parseEndRoutes(JSONObject jsonObject, String model) throws JSONException {
		jsonObject = jsonObject.getJSONObject(model);
		if (jsonObject == null) {
			throw new JSONException("Failed to parse ServiceLocation");
		}
		
		EndRoute dto = new EndRoute();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}
	public DropEmployees parseDropEmployees(JSONObject jsonObject, String model) throws JSONException {
		jsonObject = jsonObject.getJSONObject(model);
		if (jsonObject == null) {
			throw new JSONException("Failed to parse ServiceLocation");
		}
		
		DropEmployees dto = new DropEmployees();
		dto.setId(parseString(jsonObject, "id"));
		dto.setCreated(parseDate(jsonObject, "created"));

		return dto;
	}
	public List<RouteSelected> parseRouteSelections(JSONArray jsArray) throws JSONException {
		List<RouteSelected> list = new ArrayList<RouteSelected>(jsArray.length());

		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			RouteSelected rs = parseRouteSelection(jsonObject);
			if (rs != null) {
				list.add(rs);
			} else {
				throw new JSONException("Invalid RouteSelection received from server for jsonObject " + jsonObject);
			}
		}

		return list;
	}

	public RouteSelected parseRouteSelection(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("RouteSelection");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse RouteSelection");
		}

		return rsDao.buildDto(jsonObject);
	}

	public Login parseLoginPushSync(JSONObject jsonObject, String model) throws JSONException {
		jsonObject = jsonObject.getJSONObject("SmLogin");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse Login");
		}

		return loginDao.buildDto(jsonObject);
	}

	public List<GpsConfig> parseGpsConfigs(JSONArray jsArray) throws JSONException {
		List<GpsConfig> list = new ArrayList<GpsConfig>(jsArray.length());

		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			GpsConfig dto = parseGpsConfig(jsonObject);
			if (dto != null) {
				list.add(dto);
			} else {
				throw new JSONException("Invalid GpsConfig received from server for jsonObject " + jsonObject);
			}
		}

		return list;
	}

	public GpsConfig parseGpsConfig(JSONObject jsonObject) throws JSONException {
		jsonObject = jsonObject.getJSONObject("SbGpsConfig");
		if (jsonObject == null) {
			throw new JSONException("Failed to parse GpsConfig");
		}
		
		return androidDao.buildDto(jsonObject);
	}
}