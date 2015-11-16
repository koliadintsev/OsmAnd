package com.operasoft.snowboard.map.actions;

import java.util.Collection;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterest.PoiStatus;
import com.operasoft.snowboard.engine.PointOfInterestEventListener;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.util.SAacceptedListDialog;

/**
 *
 */
public class PointOfInterestMapUpdateAction extends AbstractComponentUpdateAction implements PointOfInterestEventListener {

	private Activity activity;

	private LinearLayout llSaAssigned, llSaInDirection, llSaReceived, llSlActive, llSlCompleted, llSlGoBack, llMissionActive,
			llMissionEnable, llMarker, llSlCompletedNow, llSlConstruction;

	public PointOfInterestMapUpdateAction(Activity activity) {
		this.activity = activity;
		init();
	}

	private void init() {
		llSaAssigned = ((LinearLayout) activity.findViewById(R.id.ll_sactivity_assigned));
		llSaInDirection = ((LinearLayout) activity.findViewById(R.id.ll_sactivity_in_direction));
		llSaReceived = ((LinearLayout) activity.findViewById(R.id.ll_sactivity_received));
		llSlActive = ((LinearLayout) activity.findViewById(R.id.ll_slctivity_active));
		llSlCompleted = ((LinearLayout) activity.findViewById(R.id.ll_sl_completed));
		llSlGoBack = ((LinearLayout) activity.findViewById(R.id.ll_slctivity_go_back));
		llMissionActive = ((LinearLayout) activity.findViewById(R.id.ll_mission_active));
		llMissionEnable = ((LinearLayout) activity.findViewById(R.id.ll_mission_enable));
		llMarker = ((LinearLayout) activity.findViewById(R.id.ll_marker));
		llSlCompletedNow = ((LinearLayout) activity.findViewById(R.id.ll_sl_completed_now));
		llSlConstruction = ((LinearLayout) activity.findViewById(R.id.ll_sl_construction));
	}

	@Override
	public void poiAdded(PointOfInterest poi) {
		postComponentUpdate();
	}

	@Override
	public void poiModified(PointOfInterest poi) {
		postComponentUpdate();
	}

	@Override
	public void poiRemoved(PointOfInterest poi) {
		postComponentUpdate();
	}

	@Override
	public void onPoiListReloaded(Collection<PointOfInterest> activePois) {
		postComponentUpdate();
	}

	/**
	 * This method runs in the UI thread to update the poi header
	 */
	public void updatePoiHeader() {
		llSaAssigned.setVisibility(View.GONE);
		llSaInDirection.setVisibility(View.GONE);
		llSaReceived.setVisibility(View.GONE);
		llSlActive.setVisibility(View.GONE);
		llSlCompleted.setVisibility(View.GONE);
		llSlGoBack.setVisibility(View.GONE);
		llMissionActive.setVisibility(View.GONE);
		llMissionEnable.setVisibility(View.GONE);
		llMarker.setVisibility(View.GONE);
		llSlCompletedNow.setVisibility(View.GONE);
		llSlConstruction.setVisibility(View.GONE);
		
		int saAssigned = 0;
		int saInDirection = 0;
		int saReceived = 0;
		int slActive = 0;
		int slComplete = 0;
		int slGoBack = 0;
		int mActive = 0;
		int mEnabled = 0;
		int marker = 0;
		int slCompletedNow = 0;
		int slConstruction = 0;
		// Map<Counters, Integer> countPois = poiManager.countPois();
		PointOfInterestManager poiManager = PointOfInterestManager.getInstance();

		for (PointOfInterest poi : poiManager.listActivePois()) {

			try {
				if (poi.getStatus() == PoiStatus.MISSION_ACTIVE) {
					mActive++;
					llMissionActive.setVisibility(View.VISIBLE);
				} else if (poi.getStatus() == PoiStatus.MISSION_ENABLED) {
					mEnabled++;
					llMissionEnable.setVisibility(View.VISIBLE);
				} else if (poi.getStatus() == PoiStatus.MARKER_INSTALLER) {
					marker++;
					llMarker.setVisibility(View.VISIBLE);
				} else if (poi.getStatus() == PoiStatus.SERVICE_ACTIVITY_RECEIVED) {
					saReceived++;
					llSaReceived.setVisibility(View.VISIBLE);
				} else if (poi.getStatus() == PoiStatus.SERVICE_ACTIVITY_ACCEPTED) {
					saAssigned++;
					llSaAssigned.setVisibility(View.VISIBLE);
				} else if (poi.getStatus() == PoiStatus.SERVICE_ACTIVITY_IN_DIRECTION) {
					saInDirection++;
					llSaInDirection.setVisibility(View.VISIBLE);
				}

				if (poi.isServiceLocationAttached()) {
					switch (poi.getSlStatus()) {
					case COMPLETED:
						slComplete++;
						llSlCompleted.setVisibility(View.VISIBLE);
						break;
					case COMPLETED_NOW:
						slCompletedNow++;
						llSlCompletedNow.setVisibility(View.VISIBLE);
						break;
					case GO_BACK:
						slGoBack++;
						llSlGoBack.setVisibility(View.VISIBLE);
						break;
					case CONSTRUCTION:
						slConstruction++;
						llSlConstruction.setVisibility(View.VISIBLE);
						break;
					default:
						slActive++;
						llSlActive.setVisibility(View.VISIBLE);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		((TextView) activity.findViewById(R.id.tv_sactivity_assigned)).setText(saAssigned + "");
		((TextView) activity.findViewById(R.id.tv_sactivity_in_direction)).setText(saInDirection + "");
		((TextView) activity.findViewById(R.id.tv_sactivity_received)).setText(saReceived + "");
		((TextView) activity.findViewById(R.id.tv_slctivity_active)).setText(slActive + "");
		((TextView) activity.findViewById(R.id.tv_sl_completed)).setText(slComplete + "");
		((TextView) activity.findViewById(R.id.tv_slctivity_go_back)).setText(slGoBack + "");
		((TextView) activity.findViewById(R.id.tv_mission_active)).setText(mActive + "");
		((TextView) activity.findViewById(R.id.tv_mission_enable)).setText(mEnabled + "");
		((TextView) activity.findViewById(R.id.tv_marker)).setText(marker + "");
		((TextView) activity.findViewById(R.id.tv_sl_completed_now)).setText(slCompletedNow + "");
		((TextView) activity.findViewById(R.id.tv_sl_construction)).setText(slConstruction + "");

		SAacceptedListDialog acceptedHandler = new SAacceptedListDialog(activity, activity.getWindow().getDecorView().getRootView(),
				poiManager.listActivePois());
		llSaReceived.setOnClickListener(acceptedHandler.acceptedSAListener);
		llSaAssigned.setOnClickListener(acceptedHandler.acceptedSAListener);
		llSaInDirection.setOnClickListener(acceptedHandler.acceptedSAListener);
		llSlActive.setOnClickListener(acceptedHandler.acceptedSAListener);
		llMarker.setOnClickListener(acceptedHandler.acceptedSAListener);
	}

	@Override
	protected void updateComponent() {
		updatePoiHeader();
	}
}
