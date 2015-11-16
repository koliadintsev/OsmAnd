package com.operasoft.snowboard.maplayers;

import java.util.List;

import net.osmand.plus.views.OsmandMapTileView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.operasoft.android.config.Config;
import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Contract;
import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.Divisions;
import com.operasoft.snowboard.database.EndRoute;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.database.WorksheetsDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestActionListener;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.util.PickDropEmployeesCustomDialog;
import com.operasoft.snowboard.util.SaCompletedCustomDialog;
import com.operasoft.snowboard.util.SelectCalloutTypeCustomDialog;
import com.operasoft.snowboard.util.Session;
import com.operasoft.snowboard.util.TaskListCustomDialog;
import com.operasoft.snowboard.view.HeaderView;
import com.operasoft.snowboard.view.InfoView;
import com.operasoft.snowboard.view.worksheet.row.DtoAdapter;

/**
 * This class defines the Pop up menu to display when a POI is selected by a driver. When the driver
 * press a button to perform an action, the menu must invoke the proper action method in the
 * PointOfInterestActionListener associated with it.
 * 
 * @author Christian
 */
public class PointOfInterestMenu implements OnClickListener {

	/**
	 * This is the listener to invoke when the driver performs an action on a POI.
	 */
	private final PointOfInterestActionListener actionListener;

	private final OsmandMapTileView mView;
	private Dialog dialog;
	private PointOfInterest mPoi;
	private boolean isForeMan = false;

	private void showContractSelection(final PointOfInterest poi, View dialogView, List<Contract> contracts, final Button worksheetButton) {
		final View panelButtons = dialogView.findViewById(R.id.panelbuttons);
		final ViewGroup containerList = (ViewGroup) dialogView.findViewById(R.id.ll_contract_list);
		final ListView contractsListView = (ListView) dialogView.findViewById(R.id.listcontracts);
		containerList.setVisibility(View.VISIBLE);
		panelButtons.setVisibility(View.INVISIBLE);
		final DtoAdapter<Contract> contractAdapter = new DtoAdapter<Contract>(dialogView.getContext(), contracts) {
			@Override
			public String getTitle(Contract contract) {
				return contract.getContract_number() + " / " + contract.getJobNumber();
			}
		};

		contractsListView.setAdapter(contractAdapter);

		contractsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				Contract contract = (Contract) view.getTag();
				if (contract != null)
					poi.setSelectedContract(contract);
				containerList.setVisibility(View.INVISIBLE);
				panelButtons.setVisibility(View.VISIBLE);
				setWorksheetButton(worksheetButton, poi);

			}
		});

	}

	public PointOfInterestMenu(PointOfInterestActionListener actionListener, OsmandMapTileView view) {
		this.actionListener = actionListener;
		mView = view;
		if (Session.getDriver() != null)
			isForeMan = Session.getDriver().isForeman();
	}

	public void createDialog(PointOfInterest poi) {
		createDialog(poi, 0);
	}

	private void setWorksheetButton(Button btn, PointOfInterest poi) {
		final String companyid = Session.getCompanyId();
		final String contractid = poi.getContract().getId();
		btn.setText("Edit worksheet");
		WorksheetsDao wDao = new WorksheetsDao();
		Worksheets worksheet = wDao.findOpenWorksheet(companyid, contractid);
		if (worksheet == null) {
			worksheet = new Worksheets();
			worksheet.setCompanyId(companyid);
			worksheet.setContractId(contractid);
			worksheet.setService_location_id(poi.getContract().getService_location_id());

			if (Session.getDriver() != null)
				worksheet.setCreatorId(Session.getDriver().getId());

			worksheet.setStatus(null);
			btn.setText("+Worksheet");
		} else {
			btn.setText("Edit worksheet");
		}
		btn.setTag(worksheet);
		btn.setVisibility(View.VISIBLE);
	}

	/**
	 * This method is used to draw the contextual menu to display when a POI is linked to a SA The
	 * following buttons must be made available: - Callout - Incident - Accept (if not accepted yet)
	 * - Refuse - En Route - Completed
	 */
	public void createDialog(PointOfInterest poi, long closeDelayInMillis) {

		// show menu only if there is active contract with that POI
		try {
			if (poi.getEndroute() != null) {
				// show dialog info
				showInfoEndRoute(poi.getEndroute());
				return;
			}
			poi.getContract().getId();
		} catch (NullPointerException e) {
			Toast.makeText(mView.getContext(), "No Contract found for this Location on the season selected", Toast.LENGTH_LONG).show();
			return;
		}

		PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();
		
		// if many contracts show contract list to choose one
		LayoutInflater inflater = (LayoutInflater) mView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogBodyView = inflater.inflate(R.layout.dialog_sl_menu, null);

		mPoi = poi;

		if (dialog != null)
			if (dialog.isShowing())
				dialog.dismiss();

		Button button1 = ((Button) dialogBodyView.findViewById(R.id.sl_menu_button1));
		Button button2 = ((Button) dialogBodyView.findViewById(R.id.sl_menu_button2));
		Button button3 = ((Button) dialogBodyView.findViewById(R.id.sl_menu_button3));
		Button button4 = ((Button) dialogBodyView.findViewById(R.id.sl_menu_button4));
		Button button5 = ((Button) dialogBodyView.findViewById(R.id.sl_menu_button5));
		Button button6 = ((Button) dialogBodyView.findViewById(R.id.sl_menu_button6));
		Button button7 = ((Button) dialogBodyView.findViewById(R.id.sl_menu_button7));
		Button button8 = ((Button) dialogBodyView.findViewById(R.id.sl_menu_button8));
		Button button9 = ((Button) dialogBodyView.findViewById(R.id.sl_menu_button9));
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		button5.setOnClickListener(this);
		button6.setOnClickListener(this);
		button7.setOnClickListener(this);
		button8.setOnClickListener(this);
		button9.setOnClickListener(this);

		TextView tvComment = (TextView) dialogBodyView.findViewById(R.id.tv_sl_menu_comments);
		TextView tvClientNotes = (TextView) dialogBodyView.findViewById(R.id.tv_sl_menu_clientnotes);

		if (poi.getDriverComments() == null || poi.getDriverComments().equalsIgnoreCase("null"))
			tvComment.setText("Driver Comments:\n" + "");
		else
			tvComment.setText("Driver Comments:\n" + poi.getDriverComments());
		tvComment.setMovementMethod(new ScrollingMovementMethod());

		int nbContracts = 0;
		if (poi.getSlId() != null) {
			List<Contract> contracts = (new ContractsDao()).getActiveContractForServiceLocation(poi.getSlId(), Session.getCurrentSeason(), poiMgr.getContractType());
			nbContracts = contracts.size();
			if (nbContracts > 1)
				showContractSelection(poi, dialogBodyView, contracts, button9);
			else
				setWorksheetButton(button9, poi);
		}
		if (nbContracts == 0)
			button9.setVisibility(View.GONE);

		switch (mPoi.getStatus()) {
		case SERVICE_ACTIVITY_RECEIVED:
		case SERVICE_ACTIVITY_ACCEPTED:
		case SERVICE_ACTIVITY_IN_DIRECTION:
		case MISSION_ENABLED:
		case MISSION_ACTIVE:
			try {
				if (poi.getCurrentServiceActivity().getClientNotes().equalsIgnoreCase("null")) {
					tvClientNotes.setText("Job Comments:\n" + "");
					tvClientNotes.setVisibility(View.VISIBLE);
				} else {
					tvClientNotes.setText("Job Comments:\n" + poi.getCurrentServiceActivity().getClientNotes());
					tvClientNotes.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
			}
			break;
		case MARKER_INSTALLER:
			tvClientNotes.setText("Marker Comments:\n" + poi.getCurrentMarkerInstallation().getComments());
			tvClientNotes.setVisibility(View.VISIBLE);
			break;
		default:
			tvClientNotes.setVisibility(View.GONE);
		}

		switch (mPoi.getStatus()) {
		case SERVICE_LOCATION_ACTIVE:
		case SERVICE_LOCATION_COMPLETED_NOW:
		case SERVICE_LOCATION_COMPLETED:
		case SERVICE_LOCATION_CONSTRUCTION:
			button1.setText("Services Performed");
			button2.setText("Incident");
			button3.setText("Go Back");
			button4.setText("Call Out");

			button5.setVisibility(View.GONE);
			button6.setVisibility(View.GONE);
			button7.setText("Employee Drop Off");
			button8.setText("Employee Pick Up");
			/* TODO Enable this once we are ready to integrate Schumacher changes
			button5.setText("EnRoute");
			if (isForeMan)
				button6.setText("Foreman Worksheet");
			else {
				button6.setVisibility(View.GONE);
			}

			if (InPolygon(mPoi)) {
				button7.setText("Employee Drop Off");
				button8.setText("Employee Pick Up");
			} else {
				button7.setVisibility(View.GONE);
				button8.setVisibility(View.GONE);
			}
			*/

			break;
		case SERVICE_LOCATION_GO_BACK:
			button1.setText("Services Performed");
			button2.setText("Incident");
			button3.setText("Go back complete");
			button4.setText("Call Out");
			button5.setVisibility(View.GONE);

			button6.setVisibility(View.GONE);
			button7.setText("Employee Drop Off");
			button8.setText("Employee Pick Up");
			/* TODO Enable this once we are ready to integrate Schumacher changes
			if (isForeMan)
				button6.setText("Foreman Worksheet");
			else {
				button6.setVisibility(View.GONE);
			}

			if (InPolygon(mPoi)) {
				button7.setText("Employee Drop Off");
				button8.setText("Employee Pick Up");
			} else {
				button7.setVisibility(View.GONE);
				button8.setVisibility(View.GONE);
			}
			*/

			break;
		case SERVICE_ACTIVITY_ACCEPTED:
			button1.setText("Accept");
			button1.setEnabled(false);
			button1.setTextColor(Color.LTGRAY);
			button2.setText("EnRoute");
			button3.setText("Refuse");
			button4.setText("Complete");
			button5.setVisibility(View.GONE);
			button7.setVisibility(View.GONE);
			button8.setVisibility(View.GONE);

			button6.setVisibility(View.GONE);
			/* TODO Enable this once we are ready to integrate Schumacher changes
			if (isForeMan)
				button6.setText("Foreman Worksheet");
			else {
				button6.setVisibility(View.GONE);
			}
			*/
			break;
		case SERVICE_ACTIVITY_RECEIVED:
			button1.setText("Accept");
			button2.setText("EnRoute");
			button3.setText("Refuse");
			button4.setText("Complete");
			button5.setVisibility(View.GONE);
			button7.setVisibility(View.GONE);
			button8.setVisibility(View.GONE);

			button6.setVisibility(View.GONE);
			/* TODO Enable this once we are ready to integrate Schumacher changes
			if (isForeMan)
				button6.setText("Foreman Worksheet");
			else {
				button6.setVisibility(View.GONE);
			}
			*/
			break;
		case SERVICE_ACTIVITY_IN_DIRECTION:
			button1.setText("Accept");
			button1.setTextColor(Color.LTGRAY);
			button1.setEnabled(false);
			button2.setText("EnRoute");
			button2.setTextColor(Color.LTGRAY);
			button2.setEnabled(false);
			button3.setText("Refuse");
			button4.setText("Complete");
			button5.setText("Arrived");
			button7.setVisibility(View.GONE);
			button8.setVisibility(View.GONE);

			button6.setVisibility(View.GONE);
			/* TODO Enable this once we are ready to integrate Schumacher changes
			if (isForeMan)
				button6.setText("Foreman Worksheet");
			else {
				button6.setVisibility(View.GONE);
			}
			*/
			break;
		case MISSION_ENABLED:
		case MISSION_ACTIVE:
			button1.setText("Accept");
			button2.setText("Quick Complete");
			button3.setText("Cancel");
			button4.setText("Complete");
			button5.setVisibility(View.GONE);
			button7.setVisibility(View.GONE);
			button8.setVisibility(View.GONE);

			button6.setVisibility(View.GONE);
			/* TODO Enable this once we are ready to integrate Schumacher changes
			if (isForeMan)
				button6.setText("Foreman Worksheet");
			else {
				button6.setVisibility(View.GONE);
			}
			*/
			break;
		case MARKER_INSTALLER:
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.GONE);
			button3.setText("Cancel");
			button4.setText("Complete");
			button5.setVisibility(View.GONE);
			button7.setVisibility(View.GONE);
			button8.setVisibility(View.GONE);

			button6.setVisibility(View.GONE);
			/* TODO Enable this once we are ready to integrate Schumacher changes
			if (isForeMan)
				button6.setText("Foreman Worksheet");
			else {
				button6.setVisibility(View.GONE);
			}
			*/
			break;
		}

		PointOfInterestIcon icon = new PointOfInterestIcon(poi.getStatus());
		showDialog(dialogBodyView, poi.getAddress() + " / " + poi.getName(), icon.iconId);

		if (closeDelayInMillis > 0) {
			dialogBodyView.postDelayed(new Runnable() {
				@Override
				public void run() {
					//FIX2.11 #56
					if (dialog != null) {
						try {
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						dialog = null;
					}
				}
			}, closeDelayInMillis);
		}

	}

	private void showInfoEndRoute(EndRoute endroute) {
		InfoView infoView = new InfoView(mView.getContext());
		infoView.display(endroute.getDriverName(), endroute.vehicle == null ? null : "VEHICLE:" + endroute.vehicle.getName() + " " + endroute.vehicle.getEsn_id(), endroute.company == null ? null
				: endroute.company.getCompanyName());
		showDialog(infoView, "Ended route on " + endroute.getDateTime(), R.drawable.ic_stopsign);

	}

	private void showDialog(View bodyView, String ttle, int iconid) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
		builder.setView(bodyView);
		final OnClickListener closeDialogListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
			}
		};
		HeaderView header = new HeaderView(mView.getContext(), iconid, ttle);
		header.setOnClickListener(closeDialogListener);
		builder.setCustomTitle(header);
		dialog = builder.create();
		dialog.setCancelable(true);
		dialog.getWindow().setGravity(Gravity.TOP);
		dialog.show();
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onClick(View v) {

		int touchBtn = 1;

		switch (v.getId()) {
		// SL menu: Services Performed
		// SA/Mission menu: Accept
		case R.id.sl_menu_button1:
			switch (mPoi.getStatus()) {
			case SERVICE_LOCATION_ACTIVE:
			case SERVICE_LOCATION_COMPLETED:
			case SERVICE_LOCATION_COMPLETED_NOW:
			case SERVICE_LOCATION_GO_BACK:
			case SERVICE_LOCATION_CONSTRUCTION:
// TODO IMPLEMENT/TEST this logic in a future release				
//				if (isInPoi()) {
					actionListener.serviceActivityCreated(mPoi, mPoi.getCurrentServiceActivity());
/*					
				} else {
					AlertDialog.Builder altDialog = new AlertDialog.Builder(mView.getContext());
					altDialog.setMessage("You must be in the Service Location to enter the work performed.");
					altDialog.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
					altDialog.show();
				}
*/				
				break;
			case SERVICE_ACTIVITY_ACCEPTED:
			case SERVICE_ACTIVITY_IN_DIRECTION:
				break;
			case SERVICE_ACTIVITY_RECEIVED:
				actionListener.serviceActivityAccepted(mPoi, mPoi.getCurrentServiceActivity());
				break;
			case MISSION_ENABLED:
			case MISSION_ACTIVE:
				actionListener.serviceActivityAccepted(mPoi, mPoi.getCurrentServiceActivity());
				break;
			case MARKER_INSTALLER:
			}
			break;
		case R.id.sl_menu_button2:
			// SL menu: Incident
			// SA menu: En route
			// Mission menu: Quick complete
			switch (mPoi.getStatus()) {
			case SERVICE_LOCATION_ACTIVE:
			case SERVICE_LOCATION_COMPLETED:
			case SERVICE_LOCATION_COMPLETED_NOW:
			case SERVICE_LOCATION_GO_BACK:
			case SERVICE_LOCATION_CONSTRUCTION:
				actionListener.incidentCreated(mPoi);
				break;
			case SERVICE_ACTIVITY_ACCEPTED:
			case SERVICE_ACTIVITY_RECEIVED:
			case SERVICE_ACTIVITY_IN_DIRECTION:
				actionListener.serviceActivityInDirection(mPoi, mPoi.getCurrentServiceActivity());
				break;
			case MISSION_ENABLED:
			case MISSION_ACTIVE:
				actionListener.serviceActivityCompleted(mPoi, mPoi.getCurrentServiceActivity());
				break;
			case MARKER_INSTALLER:
			}
			break;
		case R.id.sl_menu_button3:
			// SL menu: Go back / Cancel
			// SA menu: Reject
			switch (mPoi.getStatus()) {
			case SERVICE_LOCATION_ACTIVE:
			case SERVICE_LOCATION_COMPLETED:
			case SERVICE_LOCATION_COMPLETED_NOW:
			case SERVICE_LOCATION_CONSTRUCTION:
				actionListener.goBackTriggered(mPoi);
				break;
			case SERVICE_LOCATION_GO_BACK:
				actionListener.goBackCancelTriggered(mPoi);
				break;
			case SERVICE_ACTIVITY_ACCEPTED:
			case SERVICE_ACTIVITY_RECEIVED:
			case SERVICE_ACTIVITY_IN_DIRECTION:
			case MISSION_ENABLED:
			case MISSION_ACTIVE:
				actionListener.serviceActivityRefused(mPoi, mPoi.getCurrentServiceActivity());
				break;
			case MARKER_INSTALLER:
			}
			break;
		case R.id.sl_menu_button4:
			// SL menu: Call out
			// SA menu: Complete
			switch (mPoi.getStatus()) {
			case SERVICE_LOCATION_ACTIVE:
			case SERVICE_LOCATION_COMPLETED:
			case SERVICE_LOCATION_COMPLETED_NOW:
			case SERVICE_LOCATION_GO_BACK:
			case SERVICE_LOCATION_CONSTRUCTION:
				SelectCalloutTypeCustomDialog selectCallOut = new SelectCalloutTypeCustomDialog(mView.getContext(), mPoi, actionListener);
				selectCallOut.createDialog();
				break;
			case SERVICE_ACTIVITY_ACCEPTED:
			case SERVICE_ACTIVITY_RECEIVED:
			case SERVICE_ACTIVITY_IN_DIRECTION:
			case MISSION_ENABLED:
			case MISSION_ACTIVE:
				SaCompletedCustomDialog dialog = new SaCompletedCustomDialog(mView.getContext(), mPoi, actionListener, false);
				dialog.createDialog();
				break;
			case MARKER_INSTALLER:
				actionListener.markerInstalled(mPoi);
			}
			break;
		case R.id.sl_menu_button5:
			switch (mPoi.getStatus()) {
			case SERVICE_LOCATION_ACTIVE:
			case SERVICE_LOCATION_COMPLETED_NOW:
				actionListener.serviceLocationToserviceActivityEnroute(mPoi);
				break;
			case SERVICE_ACTIVITY_IN_DIRECTION:
				TaskListCustomDialog dialog = new TaskListCustomDialog(mView.getContext(), mPoi);
				dialog.createDialog();
				// if (InPolygon()) {
				// TaskListCustomDialog dialog = new TaskListCustomDialog(
				// mView.getContext(), mPoi);
				// dialog.createDialog();
				// } else {
				// Toast.makeText(mView.getContext(),
				// "No Polygon available at this location",
				// Toast.LENGTH_LONG).show();
				// dialog.dismiss();
				// }
			}
			break;
		case R.id.sl_menu_button6:
			dialog.dismiss();
			actionListener.ForemanDaily(mPoi);
			break;
		case R.id.sl_menu_button7:
			switch (mPoi.getStatus()) {
			case SERVICE_LOCATION_ACTIVE:
			case SERVICE_LOCATION_COMPLETED:
			case SERVICE_LOCATION_COMPLETED_NOW:
			case SERVICE_LOCATION_GO_BACK:
			case SERVICE_LOCATION_CONSTRUCTION:
				PickDropEmployeesCustomDialog dialog = new PickDropEmployeesCustomDialog(mView.getContext(), mPoi, PickDropEmployeesCustomDialog.dialogStatus.EMPLOYEE_DROP_LIST);
				dialog.createDialog();
			}
			break;
		case R.id.sl_menu_button8:
			switch (mPoi.getStatus()) {
			case SERVICE_LOCATION_ACTIVE:
			case SERVICE_LOCATION_COMPLETED:
			case SERVICE_LOCATION_COMPLETED_NOW:
			case SERVICE_LOCATION_GO_BACK:
			case SERVICE_LOCATION_CONSTRUCTION:
				PickDropEmployeesCustomDialog dialog = new PickDropEmployeesCustomDialog(mView.getContext(), mPoi, PickDropEmployeesCustomDialog.dialogStatus.EMPLOYEE_PICK_LIST);
				dialog.createDialog();
			}
			break;

		case R.id.sl_menu_button9:

			Worksheets worksheet = (Worksheets) v.getTag();

			mView.getApplication().startWorksheetActivity(worksheet.getId(), worksheet.getCompanyId(), worksheet.getContractId());

			break;

		default:
			touchBtn = 0;
		}

		if (touchBtn != 0) {
			dialog.dismiss();
		}
	}

	private boolean isInPoi() {
		PointOfInterest poi = PointOfInterestManager.getInstance().getInsidePolygonPoi();
		if (poi != null) {
			return poi.getId().equals(mPoi.getId());
		}
		return false;
	}

	/**
	 * Find if user is in selected polygon or not
	 * 
	 * @param slId
	 * @return boolean
	 */
	private boolean InPolygon(PointOfInterest poi) {
		List<TIT_RoutePoint> nodes = CommonUtils.getPolyNodes(poi.getPolygon());
		double[] nlat = new double[nodes.size()];
		double[] nlon = new double[nodes.size()];

		for (int i = 0; i < nodes.size(); i++) {
			nlat[i] = nodes.get(i).getLatitude();
			nlon[i] = nodes.get(i).getLongitude();
		}

		if (Session.clocation != null)
			if (CommonUtils.isPointInPolygon(nlon, nlat, nodes.size(), Session.clocation == null ? 0.0f : (float) Session.clocation.getLongitude(), Session.clocation == null ? 0.0f
					: (float) Session.clocation.getLatitude())) {
				return true;
			}

		return false;
	}

}
