package com.operasoft.snowboard.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.util.Log;

import com.operasoft.snowboard.database.Contract;
import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.EndRoute;
import com.operasoft.snowboard.database.MarkerInstallation;
import com.operasoft.snowboard.database.Polygonable;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.util.Session;

/**
 * This class represents a point of interest to display on a map along with the interactions that are possible for the driver.
 * 
 * For Snowboard, a point of interest may carry information from various sources: - Service Location (on a route or not) - Service
 * Activities (with various states) - Mission Service Activities - Marker Installations
 * 
 * All this data is logically linked to a service location. Hence, we can look at a PointOfInterest (POI) as a service location with a given
 * state based on what the driver is supposed to do.
 * 
 * POIs can be added/removed to a PointOfInterestMap object in 2 different ways: - User Interaction (through the menu) - Database
 * Synchronization (new/updated data coming from Snowman server)
 * 
 * The PointIfInterestLayer needs to display all the POIs found in the PointOfInterestMap.
 * 
 * @author Christian
 * 
 */
/**
 * @author dounaka
 *
 */
public class PointOfInterest implements Polygonable {
	/**
	 * This enum defines the internal status of the POI. This dictates how the POI needs to be displayed as well as the list of actions
	 * available to the driver based at any given time.
	 */
	public enum PoiStatus {
		INACTIVE, // POI is not active and should no longer be displayed
		SERVICE_LOCATION_ACTIVE, // POI needs to be displayed as a service location that needs to be serviced
		SERVICE_LOCATION_COMPLETED, SERVICE_LOCATION_COMPLETED_NOW, // POI needs to be displayed as a service location that was properly completed.
		SERVICE_LOCATION_GO_BACK, // POI needs to be displayed as a service location where the driver needs to go back.
		SERVICE_LOCATION_CONSTRUCTION, // POI needs to be displayed as a construction service location.
		MARKER_INSTALLER, // POI needs to be displayed as a marker installation.
		MISSION_ENABLED, // POI needs to be displayed as an item in a mission.
		MISSION_ACTIVE, // POI needs to be displayed as a the next item to perform in the mission.
		SERVICE_ACTIVITY_RECEIVED, // POI needs to be displayed as a service activity that has not been accepted yet by the driver
		SERVICE_ACTIVITY_ACCEPTED, // POI needs to be displayed as a service activity that has been accepted by the driver
		SERVICE_ACTIVITY_IN_DIRECTION, // POI needs to be displayed as a service activity that is being actively handled by the driver
		END_ROUTE
	}

	public enum SlStatus {
		INACTIVE, ACTIVE, COMPLETED, GO_BACK, COMPLETED_NOW, CONSTRUCTION
	}

	/**
	 * The POI ID to use in The PointOfInterestMap. It is the ServiceLocation's database ID
	 */
	private String id;

	private PoiStatus status = PoiStatus.INACTIVE;

	/**
	 * The service location object currently assigned to this POI that needs to be displayed This field is set when the driver selects a
	 * given route or the "All Service Locations" option from the menu. Otherwise, it is set to null.
	 */
	private ServiceLocation sl = null;
	/**
	 * Flag to indicate where we are at with our service location completeness.
	 */
	private SlStatus slStatus = SlStatus.INACTIVE;

	/**
	 * The selected contract that will be used on worksheet, damage and new service activity
	 */
	private Contract selectedContract = null;

	/**
	 * The contract associated with this POI.
	 */
	private Contract contract = null;

	/**
	 * The label to display next to the POI icon
	 */
	private double distance = 0;

	/**
	 * The position where the driver has ended the route
	 */
	private EndRoute endroute = null;

	public EndRoute getEndroute() {
		return endroute;
	}

	/**
	 * The list of "marker installation" activities currently assigned to this POI that needs to be displayed
	 */
	private List<MarkerInstallation> markerList = null;

	/**
	 * The list of "mission" service activities currently assigned to this POI that needs to be displayed
	 */
	private List<ServiceActivity> missionList = null;

	/**
	 * The list of "regular" service activities currently assigned to this POI that needs to be displayed
	 */
	private List<ServiceActivity> saList = null;

	/**
	 * The label to display next to the POI icon
	 */
	private String label = "";

	public PointOfInterest(String id) {
		super();
		this.id = id;
		this.status = PoiStatus.INACTIVE;
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return Returns the current POI status. This dictates how the driver can interact with this POI.
	 */
	public PoiStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return (status.toString() + " " + getLatitude() + ":" + getLongitude());
	}

	public int missionListSize() {
		if (missionList != null) {
			return missionList.size();
		}

		return 0;
	}

	public int saListSize() {
		if (saList != null) {
			return saList.size();
		}

		return 0;
	}

	public int saAssignedSize() {
		int count = 0;
		if (saList != null) {
			for (ServiceActivity sa : saList) {
				if (sa.getStatus().equalsIgnoreCase(ServiceActivity.SA_ASSIGNED)) {
					count++;
				}
			}
		}

		return count;
	}

	public int saAcceptedSize() {
		int count = 0;
		if (saList != null) {
			for (ServiceActivity sa : saList) {
				if (sa.getStatus().equalsIgnoreCase(ServiceActivity.SA_ACCEPTED)) {
					count++;
				}
			}
		}

		return count;
	}

	public int saInDirectionSize() {
		int count = 0;
		if (saList != null) {
			for (ServiceActivity sa : saList) {
				if (sa.getStatus().equalsIgnoreCase(ServiceActivity.SA_IN_DIRECTION)) {
					count++;
				}
			}
		}

		return count;
	}

	public int markerListSize() {
		if (markerList != null) {
			return markerList.size();
		}

		return 0;
	}

	/**
	 * Returns whether a service location is currently attached to this POI
	 */
	public boolean isServiceLocationAttached() {
		return (sl != null) && (slStatus != SlStatus.INACTIVE);
	}

	public SlStatus getSlStatus() {
		return slStatus;
	}

	public String getSlId() {
		if (sl != null) {
			return sl.getId();
		}
		return null;
	}

	/**
	 * Returns the driver comments associated with this POI. Those comments must be displayed as soon as the driver enters the POI polygon.
	 */
	public String getDriverComments() {
		if (sl != null) {
			return sl.getComments();
		} else {
			// TODO Log error
		}
		return "";
	}

	public String getAddress() {
		if (sl != null) {
			if (sl.getAddress() != null)
				return sl.getAddress();
		} else {
			// TODO Log error
		}
		return "";
	}

	/**
	 * Returns the polygon associated with this POI. The polygon is used to figure out if the vehicle is inside or outside the POI area.
	 */
	@Override
	public String getPolygon() {
		if (sl != null) {
			return sl.getPolygon();
		} else {
			// TODO Log error
		}
		return "";
	}

	/**
	 * Returns the POI center point's longitude
	 */
	public double getLongitude() {
		if (endroute != null) {
			try {
				return Double.valueOf(endroute.getLongitude());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (sl != null) {
			return sl.getLongitude();
		} else {
			// TODO Log error
		}
		return 0;
	}

	/**
	 * Returns the POI center point's latitude
	 */
	public double getLatitude() {
		if (endroute != null) {
			try {
				return Double.valueOf(endroute.getLatitude());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (sl != null) {
			return sl.getLatitude();
		} else {
			// TODO Log error
		}
		return 0;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * This method must be invoked when a service location must be serviced by the driver. This must be invoked when one of the following
	 * event occurs: - Driver selects a new route; all service locations from that routes needs to be attached to a POI. - Driver selects
	 * "All service locations" mode; all active service locations associated with the company needs to be attached to a POI. - A new service
	 * location is received from Snowman for the route currently selected - A new service location is received from Snowman when the driver
	 * is in the "All service locations" mode
	 * 
	 * @return Whether or not the operation caused the POI status to change
	 */
	public boolean attachServiceLocation(ServiceLocation sl) {
		Log.v("PointOfInterest", "Add SL " + sl.getId() + " to POI " + getId());
		this.sl = sl;
		this.slStatus = SlStatus.ACTIVE;
		return updatePoiStatus();
	}

	/**
	 * This method must be invoked when a service location should no longer be serviced by the driver. This must be invoked when one of the
	 * following event occurs: - Driver selects a new route; all service locations of the previously selected route must be detached from
	 * their respective POI. - Driver goes out of "All service locations" mode; all service locations needs to be detached from the
	 * respective POI.
	 * 
	 * @return Whether or not the operation caused the POI status to change
	 */
	public boolean detachServiceLocation() {
		if (slStatus != SlStatus.INACTIVE) {
			Log.i("PointOfInterest", "Remove SL " + sl.getId() + " to POI " + getId());
			slStatus = SlStatus.INACTIVE;
			return updatePoiStatus();
		}

		return false;
	}

	public boolean attachConstructionLocation(ServiceLocation sl) {
		Log.v("PointOfInterest", "Add SL " + sl.getId() + " to POI " + getId());
		this.sl = sl;
		this.slStatus = SlStatus.CONSTRUCTION;
		return updatePoiStatus();
	}

	/**
	 * This method must be invoked when the POI is in SERVICE_LOCATION_ACTIVE or SERVICE_LOCATION_GO_BACK and the driver has been in the
	 * ServiceLocation's polygon for more than 30 seconds.
	 * 
	 * @return Whether or not the operation caused the POI status to change
	 */
	public boolean markSLAsCompleted() {
		slStatus = SlStatus.COMPLETED;
		return updatePoiStatus();
	}

	/**
	 * This method must be invoked when the POI is in SERVICE_LOCATION_ACTIVE or SERVICE_LOCATION_COMPLETED and the driver selects the
	 * "Go back" action from the popup menu.
	 * 
	 * @return Whether or not the operation caused the POI status to change
	 */
	public boolean markSLAsGoBack() {
		slStatus = SlStatus.GO_BACK;
		return updatePoiStatus();
	}

	/**
	 * Attaches a marker installation to the POI.
	 * 
	 * @return Whether or not the operation caused the POI status to change
	 */
	public boolean attachMarkerInstallation(MarkerInstallation mi, ServiceLocation miSL) {
		if (markerList == null) {
			markerList = new Vector<MarkerInstallation>();
		}
		// Marker installations are added using a FIFO approach.
		if (!markerList.contains(mi)) {
			markerList.add(mi);

			boolean statusChanged = updatePoiStatus();

			if (sl == null) {
				if (miSL != null) {
					sl = miSL;
				} else {
					ServiceLocationDao dao = new ServiceLocationDao();
					sl = dao.findServiceLocationByContractId(mi.getContractId());
				}
				slStatus = SlStatus.INACTIVE;
			}

			return statusChanged;
		}

		return false;
	}

	/**
	 * Detach a marker installation from the POI
	 * 
	 * @return Whether or not the operation caused the POI status to change
	 */
	public boolean detachMarkerInstallation(MarkerInstallation mi) {
		if (markerList != null) {
			markerList.remove(mi);
			return updatePoiStatus();
		} else {
			// TODO Log error
		}

		return false;
	}

	public MarkerInstallation getCurrentMarkerInstallation() {
		if ((markerList != null) && (!markerList.isEmpty())) {
			return markerList.get(0);
		}

		return null;
	}

	/**
	 * Attaches a service activity to perform on the selected POI.
	 * 
	 * @param sa
	 * 
	 * @return Whether or not the operation caused the POI status to change
	 */
	public boolean attachServiceActivity(ServiceActivity sa) {
		if (!sa.isWorkRequired()) {
			Log.w("PointOfInterest", "No work required for SA " + sa.getId());
			return false;
		}

		if (sa.isMissionSA()) {
			Log.i("PointOfInterest", "Add mission " + sa.getId() + " to POI " + getId());
			if (missionList == null) {
				missionList = new Vector<ServiceActivity>();
			}
			if (!missionList.contains(sa)) {
				int index = 0;
				for (ServiceActivity obj : missionList) {
					if (sa.getSequenceNumber() > obj.getSequenceNumber()) {
						index++;
					} else {
						break;
					}
				}
				// Mission SAs are sorted based on their sequence number
				missionList.add(index, sa);
				updateLabel();
			}
		} else {
			Log.i("PointOfInterest", "Add SA " + sa.getId() + " to POI " + getId());
			if (saList == null) {
				saList = new Vector<ServiceActivity>();
			}
			// Regular SAs are added using a FIFO approach
			if (!saList.contains(sa)) {
				saList.add(sa);
			}
		}
		if (sl == null) {
			ServiceLocationDao dao = new ServiceLocationDao();
			sl = dao.findServiceLocationByContractId(sa.getContractId());
			if (sl == null) {
				Log.e("POI", "No SL found for contract " + sa.getContractId());
			}
			slStatus = SlStatus.INACTIVE;
		}

		return updatePoiStatus();
	}

	/**
	 * Detaches a SA from a POI
	 * 
	 * @return Whether or not the operation caused the POI status to change
	 */
	public boolean detachServiceActivity(ServiceActivity sa) {
		if (sa.isMissionSA()) {
			Log.i("PointOfInterest", "Remove mission " + sa.getId() + " to POI " + getId());
			if ((missionList != null) && (missionList.contains(sa))) {
				missionList.remove(sa);
				updateLabel();
			} else {
				Log.e("PointOfInterest", "Failed to remove mission " + sa.getId() + " to POI " + getId());
			}
		} else {
			Log.i("PointOfInterest", "remove SA " + sa.getId() + " to POI " + getId());
			if ((saList != null) && (saList.contains(sa))) {
				saList.remove(sa);
			} else {
				Log.e("PointOfInterest", "Failed to remove SA " + sa.getId() + " to POI " + getId());
			}
		}
		return updatePoiStatus();
	}

	public boolean updateServiceActivity(ServiceActivity sa) {
		if (sa.isMissionSA()) {
			if (missionList != null) {
				int index = -1;
				for (int i = 0; i < missionList.size(); i++) {
					ServiceActivity sa1 = missionList.get(i);
					if (sa.getId().equals(sa1.getId())) {
						index = i;
						break;
					}
				}
				if (index != -1) {
					missionList.add(index, sa);
					missionList.remove(index + 1);
				} else {
					Log.e("PointOfInterest", "Could not find Mission " + sa.getId() + " in POI " + getId());
				}
			} else {
				Log.w("PointOfInterest", "missionList is null, allocating a new one");
				missionList = new Vector<ServiceActivity>();
				missionList.add(sa);
			}
			updateLabel();
		} else {
			if (saList != null) {
				int index = -1;
				for (int i = 0; i < saList.size(); i++) {
					ServiceActivity sa1 = saList.get(i);
					if (sa.getId().equals(sa1.getId())) {
						index = i;
						break;
					}
				}
				if (index != -1) {
					saList.add(index, sa);
					saList.remove(index + 1);
				} else {
					Log.e("PointOfInterest", "Could not find SA " + sa.getId() + " in POI " + getId());
				}
			} else {
				Log.w("PointOfInterest", "saList is null, allocating a new one");
				saList = new Vector<ServiceActivity>();
				saList.add(sa);
			}
		}

		return updatePoiStatus();
	}

	// /**
	// * Detach the current service activity from the POI
	// */
	// Not sure this is still needed
	// public boolean detachCurrentServiceActivity() {
	// // TODO Revisit this method to see
	// if (saList != null) {
	// saList.remove(0);
	// return updatePoiStatus();
	// }
	//
	// return false;
	// }

	private void updateLabel() {
		StringBuilder newLabel = new StringBuilder();
		int i = 0;
		for (ServiceActivity sa : missionList) {
			if (sa.isPrioritizedMissionSA()) {
				if (i > 0) {
					newLabel.append(", ");
				}
				newLabel.append(sa.getSequenceNumber());
				i++;
			}
		}

		label = newLabel.toString();
	}

	/**
	 * Retrieves the current service activity associated with this POI.
	 * 
	 * @return
	 */
	public ServiceActivity getCurrentServiceActivity() {
		if ((saList != null) && (!saList.isEmpty())) {
			return saList.get(0);
		} else if ((missionList != null) && (!missionList.isEmpty())) {
			return missionList.get(0);
		}

		return null;
	}

	public Contract getSelectedContract() {
		return selectedContract;
	}

	public void setSelectedContract(Contract contract) {
		this.selectedContract = contract;
	}

	/**
	 * Returns the contract associated with this POI
	 */
	public Contract getContract() {
		if (selectedContract != null)
			return selectedContract;
		if ((contract == null) && (sl != null)) {
			ContractsDao dao = new ContractsDao();
			// TODO Make sure the multi-contract scenario is properly handled everywhere (selectedContract should have been set already)
			List<Contract> contracts = dao.getActiveContractForServiceLocation(sl.getId(), Session.getCurrentSeason(), PointOfInterestManager.getInstance().getContractType());
			if (!contracts.isEmpty()) {
				contract = contracts.get(0);
			} else {
				contract = null;
			}
//			contract = dao.getContractForServiceLocationId(sl.getId(), Session.getCurrentSeason());
			//contract = dao.getContractForServiceLocationId(sl.getId());
		}

		return contract;
	}

	/**
	 * Private method used to update the POI status based on the data currently attached to the POI object.
	 * 
	 * @return
	 */
	public boolean updatePoiStatus() {
		// Remember current status for logging purposes
		PoiStatus oldStatus = status;

		boolean checkSlStatus = false;

		if ((saList != null) && (!saList.isEmpty())) {
			ServiceActivity sa = saList.get(0);
			if (slStatus == SlStatus.COMPLETED_NOW) {
				status = PoiStatus.SERVICE_LOCATION_COMPLETED_NOW;
			} else if (sa.getStatus().equalsIgnoreCase(ServiceActivity.SA_IN_DIRECTION)) {
				status = PoiStatus.SERVICE_ACTIVITY_IN_DIRECTION;
			} else if (sa.getStatus().equalsIgnoreCase(ServiceActivity.SA_ACCEPTED)) {
				status = PoiStatus.SERVICE_ACTIVITY_ACCEPTED;
			} else if (sa.getStatus().equalsIgnoreCase(ServiceActivity.SA_ASSIGNED)) {
				status = PoiStatus.SERVICE_ACTIVITY_RECEIVED;
			} else {
				Log.e("PointOfInterest", "Invalid SA status: " + sa.getStatus());
				checkSlStatus = true;
			}
		} else if ((missionList != null) && (!missionList.isEmpty())) {
			int activeMissionSa = PointOfInterestManager.getInstance().getActiveMissionSA();
			if (missionList.get(0).getSequenceNumber() == activeMissionSa) {
				status = PoiStatus.MISSION_ACTIVE;
			} else {
				status = PoiStatus.MISSION_ENABLED;
			}
		} else if ((markerList != null) && (!markerList.isEmpty())) {
			status = PoiStatus.MARKER_INSTALLER;
		} else if ((sl != null) || (checkSlStatus)) {
			if (slStatus == SlStatus.GO_BACK) {
				status = PoiStatus.SERVICE_LOCATION_GO_BACK;
			} else if (slStatus == SlStatus.COMPLETED) {
				status = PoiStatus.SERVICE_LOCATION_COMPLETED;
			} else if (slStatus == SlStatus.ACTIVE) {
				status = PoiStatus.SERVICE_LOCATION_ACTIVE;
			} else if (slStatus == SlStatus.COMPLETED_NOW) {
				status = PoiStatus.SERVICE_LOCATION_COMPLETED_NOW;
			} else if (slStatus == SlStatus.CONSTRUCTION) {
				status = PoiStatus.SERVICE_LOCATION_CONSTRUCTION;
			} else {
				status = PoiStatus.INACTIVE;
			}
		} else {
			status = PoiStatus.INACTIVE;
		}

		Log.v("PointOfInterest", "Status: old = " + oldStatus + ", new: " + status);
		return (oldStatus != status);
	}

	public boolean detachAllMarkerInstallations() {
		if ((markerList != null) && (!markerList.isEmpty())) {
			markerList.clear();
			return updatePoiStatus();
		}
		return false;
	}

	public boolean detachAllOthersServiceActivities() {
		List<ServiceActivity> cleanupList = new ArrayList<ServiceActivity>();

		if (saList != null) {
			for (ServiceActivity sa : saList) {
				if (!sa.isMine()) {
					cleanupList.add(sa);
				}
			}

			for (ServiceActivity sa : cleanupList) {
				saList.remove(sa);
			}
		}

		if (missionList != null) {
			cleanupList.clear();
			for (ServiceActivity sa : missionList) {
				if (!sa.isMine()) {
					cleanupList.add(sa);
				}
			}

			for (ServiceActivity sa : cleanupList) {
				missionList.remove(sa);
			}
		}

		return updatePoiStatus();
	}

	/**
	 * @return Whether or not the operation caused the POI status to change
	 */
	public boolean attachServiceLocationCompletedNow(ServiceLocation sl) {
		Log.i("PointOfInterest", "Add SL " + sl.getId() + " to POI " + getId());
		this.sl = sl;
		this.slStatus = SlStatus.COMPLETED_NOW;
		return updatePoiStatus();
	}

	public void attachServiceLocationWithStatus(ServiceLocation aServiceLocation) {
		if (aServiceLocation == null)
			return;
		if (aServiceLocation.isVisited()) {
			attachServiceLocationCompleted(aServiceLocation);
		} else if (aServiceLocation.isCompleted()) {
			attachServiceLocationCompletedNow(aServiceLocation);
		} else
			attachServiceLocation(aServiceLocation);

	}

	/**
	 * @return Whether or not the operation caused the POI status to change
	 */
	public boolean attachServiceLocationCompleted(ServiceLocation sl) {
		Log.i("PointOfInterest", "Add SL " + sl.getId() + " to POI " + getId());
		this.sl = sl;
		this.slStatus = SlStatus.COMPLETED;
		return updatePoiStatus();
	}

	public boolean markSLAsCompletedNow() {
		slStatus = SlStatus.COMPLETED_NOW;
		return updatePoiStatus();
	}

	public PointOfInterest(EndRoute er) {
		if (er != null) {
			this.status = PoiStatus.END_ROUTE;
			this.id = er.getId();
			this.endroute = er;
			Log.v("PointOfInterest", "Add ER " + er.getId() + " :: " + endroute.getRouteId() + " to POI " + getId());
		}
	}

	@Override
	public String getName() {
		if (sl != null && sl.getName() != null)
			return sl.getName();
		return null;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

}
