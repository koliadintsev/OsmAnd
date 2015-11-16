package com.operasoft.snowboard.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.osmand.osm.LatLon;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.views.OsmandMapTileView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Company;
import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.dbsync.Utils;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestActionHandler;
import com.operasoft.snowboard.engine.PointOfInterestActionListener;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.engine.RouteBuilder;
import com.operasoft.snowboard.engine.PointOfInterest.PoiStatus;
import com.operasoft.snowboard.maplayers.PointOfInterestMenu;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;

public class WOAcceptedListDialog {

	private Context mContext;
	private Dialog assingedDialog = null, recievedDialog = null;
	private Collection<PointOfInterest> mPoi;
	private ArrayList<PointOfInterest> mPois;
	private LinearLayout llList;
	private int index;
	private PointOfInterestActionListener actionListener;
	private String disUnit;
	private TextView tvTitleText_re;
	
	public WOAcceptedListDialog(Context context, View view, Collection<PointOfInterest> collection) {
		this.mContext = context;
		this.mPoi = collection;
		this.actionListener = new PointOfInterestActionHandler(mContext);
		disUnit = Session.getDistanceUnit();

	}
	
	public OnClickListener acceptedWOListener = new OnClickListener() {
		private int poiCount;
		private int poiCount_re;
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_slctivity_active:
				Config.compassState = 3;
				Session.MapAct.setMapLockState();
				mContext.startActivity(new Intent(mContext, MapActivity.class));
				break;
			case R.id.ll_sactivity_assigned:
				createAssignedDialog(false);
				break;

			case R.id.ll_sactivity_received:
				createRecievedDialog(false);
				break;

			case R.id.ll_marker:
				Config.compassState = 3;
				Session.MapAct.setMapLockState();
				Utils.setMarkerZoom();
				mContext.startActivity(new Intent(mContext, MapActivity.class));
				break;

			case R.id.ll_sactivity_in_direction:

				mPois = new ArrayList<PointOfInterest>();
				if (mPoi != null) {
					for (PointOfInterest poi : mPoi) {
						if (poi.getStatus() == PoiStatus.SERVICE_ACTIVITY_IN_DIRECTION) {
							mPois.add(poi);
						}
					}
				}

				PointOfInterest poi = mPois.get(0);
				SaCompletedCustomDialog dialogSA = new SaCompletedCustomDialog(mContext, poi, actionListener, false);
				dialogSA.createDialog();
				break;

			default:
				break;
			}
		}
		
		private void navigateTo(double lat, double lon) {
			PointOfInterestManager poiManager = PointOfInterestManager.getInstance();
			Session.route = new Route();
			Session.route.setLinePath("LINESTRING(" + lat + " " + lon + ")");
			Session.route.setId("temp");
			poiManager.selectRoute(Session.route, 0);

			Session.MapAct.navigateToPoint(new LatLon(lat, lon), true);

			Config.MARKER_INSTALLATION = false;
			OsmandSettings.setRouteTime(mContext, System.nanoTime());
			final OsmandSettings settings = Session.MapAct.getMyApplication().getSettings();
			settings.clearPointToNavigate();
			if (Session.getCompany().getLanguage().equals("F"))
				settings.VOICE_PROVIDER.set("fr-tts");
			else
				settings.VOICE_PROVIDER.set("en-tts");

			new AsyncTask<Void, Void, Void>() {
				private ProgressDialog pDialog;

				@Override
				protected void onPreExecute() {
					pDialog = ProgressDialog.show(mContext, "", "Please wait calculating route.");
					final List<TIT_RoutePoint> points = Utils.getGeoPolygon(Session.route.getLinePath());
					OsmandSettings settings = Session.MapAct.getMyApplication().getSettings();
					settings = Session.MapAct.getMyApplication().getSettings();
					try {
						if (points.size() > 0) {
							// Setting route start point in center of map
							settings.setMapLocationToShow(points.get(0).getLatitude(), points.get(0).getLongitude(), 15, null);
						} else {
							if (Session.clocation != null)
								settings.setMapLocationToShow(Session.clocation.getLatitude(), Session.clocation.getLongitude(), 15, null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				protected Void doInBackground(Void... params) {
					RouteBuilder builder = new RouteBuilder(Session.route, Session.MapAct);
					builder.run();

					Config.compassState = 3;
					Intent intent = new Intent(mContext, MapActivity.class);
					Session.MapAct.startActivity(intent);
					Session.setRouteSlPosition(0);
					try {
						final PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();
						if (poiMgr.routeSlList != null && !poiMgr.routeSlList.isEmpty()) {
							PointOfInterest poi = new PointOfInterest(poiMgr.routeSlList.get(0).getId());
							poi.attachServiceLocation(poiMgr.routeSlList.get(0));
							// Fix2.11 #61
							if (Session.route != null && Session.route.getPopUp() != null
									&& Session.route.getPopUp().equals(Route.SHOW_POPUP)) {
								PointOfInterestActionListener actionListener = new PointOfInterestActionHandler(mContext);
								OsmandMapTileView tileView = new OsmandMapTileView(mContext);
								PointOfInterestMenu menu = new PointOfInterestMenu(actionListener, tileView);
								menu.createDialog(poi);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					if (pDialog != null)
						if (pDialog.isShowing())
							pDialog.dismiss();
				}
			}.execute();

		}
		
		
		private void createAssignedDialog(boolean isDate) {
			poiCount = 0;

			mPois = new ArrayList<PointOfInterest>();
			for (PointOfInterest poi : mPoi) {
				if (poi.getStatus() == PoiStatus.SERVICE_ACTIVITY_ACCEPTED) {
					double distance = Utils.distance(poi.getLatitude(), poi.getLongitude(), Session.clocation.getLatitude(),
							Session.clocation.getLongitude(), disUnit.equals(Company.KILOMETERS) ? 'K' : 'M');
					mPois.add(poi);
					mPois.get(poiCount).setDistance(distance);
					poiCount++;
				}
			}

			Collections.sort(mPois, isDate ? DATE_ORDER : DISTANCE_ORDER);

			String titleText = " " + poiCount + " Accepted Service Activities";
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (assingedDialog == null) {
				assingedDialog = new Dialog(mContext);
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				View layout = inflater.inflate(R.layout.dialoag_listview_punch_clock,
						(ViewGroup) assingedDialog.findViewById(R.id.root_punch));
				// Dialog close button and Text on click listener.
				ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
				ImageView iMgTitle = (ImageView) layout.findViewById(R.id.iMg_dialog_title);
				iMgTitle.setImageResource(R.drawable.sa_assigned);
				((LinearLayout) layout.findViewById(R.id.ll_sort)).setVisibility(View.VISIBLE);
				TextView closeText = (TextView) layout.findViewById(R.id.textView2);
				tvTitleText_re = (TextView) layout.findViewById(R.id.textView1);
				tvTitleText_re.setText(titleText);

				View layout1 = inflater.inflate(R.layout.sa_accepted_dialog, null);
				llList = (LinearLayout) layout1.findViewById(R.id.ll_sal_list);

				iMg_cancel_dialog.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						assingedDialog.dismiss();
					}
				});
				closeText.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						assingedDialog.dismiss();
					}
				});

				final Button btnSort = (Button) layout.findViewById(R.id.btn_sort);
				btnSort.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (btnSort.getText().toString().equals("Due Date/Time")) {
							((TextView) assingedDialog.findViewById(R.id.tv_format)).setText("Due Date/Time");
							btnSort.setText("Distance");
							createAssignedDialog(true);
						} else {
							((TextView) assingedDialog.findViewById(R.id.tv_format)).setText("Distance");
							btnSort.setText("Due Date/Time");
							createAssignedDialog(false);
						}
					}
				});

				builder.setCustomTitle(layout);
				builder.setView(layout1);
				assingedDialog = builder.create();
				assingedDialog.setCancelable(true);
				assingedDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						assingedDialog = null;
					}
				});
			} else
				llList.removeAllViews();
			for (int k = 0; k < poiCount; k++) {
				inflater.inflate(R.layout.add_sa_accepted_rows, llList);
			}

			for (index = 0; index < llList.getChildCount(); index++) {
				LinearLayout llChild = (LinearLayout) llList.getChildAt(index);
				TextView address = (TextView) (llChild).findViewById(R.id.tv_sa_address);
				TextView jobComment = (TextView) (llChild).findViewById(R.id.tv_sa_job_comment);
				TextView tvDistance = (TextView) (llChild).findViewById(R.id.tv_distance);
				Button btnRefuse = (Button) (llChild).findViewById(R.id.asa_button8);
				Button btnCompleted = (Button) (llChild).findViewById(R.id.asa_button9);
				final Button btnNavigate = (Button) llChild.findViewById(R.id.asa_button_navigate);

				address.setTag(Integer.toString(index));
				address.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PointOfInterest poi = mPois.get(Integer.parseInt(v.getTag().toString()));
						Session.MapAct.setMapLocation(poi.getLatitude(), poi.getLongitude(), 18);
						assingedDialog.dismiss();
						navigateTo(poi.getLatitude(), poi.getLongitude());
					}
				});
				jobComment.setTag(Integer.toString(index));
				jobComment.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PointOfInterest poi = mPois.get(Integer.parseInt(v.getTag().toString()));
						Session.MapAct.setMapLocation(poi.getLatitude(), poi.getLongitude(), 18);
						navigateTo(poi.getLatitude(), poi.getLongitude());
						assingedDialog.dismiss();
					}
				});
				tvDistance.setTag(Integer.toString(index));
				tvDistance.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PointOfInterest poi = mPois.get(Integer.parseInt(v.getTag().toString()));
						Session.MapAct.setMapLocation(poi.getLatitude(), poi.getLongitude(), 18);
						assingedDialog.dismiss();
					}
				});

				btnRefuse.setTag(Integer.toString(index));
				btnRefuse.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String idxStr = (String) v.getTag();
						PointOfInterest poi = mPois.get(Integer.parseInt(idxStr));
						actionListener.serviceActivityRefused(poi, mPois.get(Integer.parseInt(idxStr)).getCurrentServiceActivity());
						((LinearLayout) llList.getChildAt(Integer.parseInt(idxStr))).setVisibility(View.GONE);
						tvTitleText_re.setText((--poiCount) + " Accepted Service Activities");
						llList.refreshDrawableState();

						if (poiCount < 1)
							assingedDialog.dismiss();
					}
				});

				btnCompleted.setTag(Integer.toString(index));
				btnCompleted.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String idxStr = (String) v.getTag();
						PointOfInterest poi = mPois.get(Integer.parseInt(idxStr));
						actionListener.serviceActivityCompleted(poi, poi.getCurrentServiceActivity());
						((LinearLayout) llList.getChildAt(Integer.parseInt(idxStr))).setVisibility(View.GONE);
						tvTitleText_re.setText((--poiCount) + " Accepted Service Activities");
						llList.refreshDrawableState();

						if (poiCount < 1)
							assingedDialog.dismiss();
					}
				});

				btnNavigate.setTag(Integer.toString(index));
				btnNavigate.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int position = Integer.parseInt(v.getTag().toString());
						navigateTo(mPois.get(position).getLatitude(), mPois.get(position).getLongitude());
						assingedDialog.dismiss();
					}
				});

				address.setText(mPois.get(index).getAddress());
				jobComment.setText(mPois.get(index).getCurrentServiceActivity().getClientNotes());
				if (Session.clocation != null) {
					// double distance = Utils.distance(mPois.get(index).getLatitude(),
					// mPois.get(index).getLongitude(), Session.clocation
					// .getLatitude(), Session.clocation.getLongitude(),
					// disUnit.equals(Company.KILOMETERS) ? 'K' : 'M');

					SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					SimpleDateFormat newFormat = new SimpleDateFormat("MMM.dd,yyyy HH:mm:ss");
					String dateTime = null;
					try {
						Date date = oldFormat.parse(mPois.get(index).getCurrentServiceActivity().getDateTime());
						dateTime = newFormat.format(date);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (isDate)
						tvDistance.setText(dateTime);
					else
						tvDistance.setText("" + new DecimalFormat("0.#").format(mPois.get(index).getDistance()) + " " + disUnit);

				}
			}

			if (assingedDialog != null) {
				if (!assingedDialog.isShowing()) {
					assingedDialog.show();
					assingedDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				}
			}
		}

		private void createRecievedDialog(boolean isDate) {

			mPois = new ArrayList<PointOfInterest>();
			poiCount_re = 0;

			if (mPoi != null) {
				for (PointOfInterest poi : mPoi) {
					if (poi.getStatus() == PoiStatus.SERVICE_ACTIVITY_RECEIVED) {
						double distance = Utils.distance(poi.getLatitude(), poi.getLongitude(), Session.clocation.getLatitude(),
								Session.clocation.getLongitude(), disUnit.equals(Company.KILOMETERS) ? 'K' : 'M');
						mPois.add(poi);
						mPois.get(poiCount_re).setDistance(distance);
						poiCount_re++;
					}
				}
			}

			Collections.sort(mPois, isDate ? DATE_ORDER : DISTANCE_ORDER);
			String titleText_re = " " + poiCount_re + " Assigned Service Activities";
			LayoutInflater inflater_re = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (recievedDialog == null) {
				recievedDialog = new Dialog(mContext);
				AlertDialog.Builder builder_re = new AlertDialog.Builder(mContext);
				View layout_re = inflater_re.inflate(R.layout.dialoag_listview_punch_clock,
						(ViewGroup) recievedDialog.findViewById(R.id.root_punch));
				// Dialog close button and Text on click listener.
				ImageView iMg_cancel_dialog_re = (ImageView) layout_re.findViewById(R.id.iMg_cancel_dialog);
				ImageView iMg_title_dialog_re = (ImageView) layout_re.findViewById(R.id.iMg_dialog_title);
				iMg_title_dialog_re.setImageResource(R.drawable.sa_received);
				((LinearLayout) layout_re.findViewById(R.id.ll_sort)).setVisibility(View.VISIBLE);
				TextView closeText_re = (TextView) layout_re.findViewById(R.id.textView2);
				tvTitleText_re = (TextView) layout_re.findViewById(R.id.textView1);
				tvTitleText_re.setText(titleText_re);
				View layout1_re = inflater_re.inflate(R.layout.sa_accepted_dialog, null);
				llList = (LinearLayout) layout1_re.findViewById(R.id.ll_sal_list);
				builder_re.setCustomTitle(layout_re);
				builder_re.setView(layout1_re);
				recievedDialog = builder_re.create();
				recievedDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						recievedDialog = null;
					}
				});

				iMg_cancel_dialog_re.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						recievedDialog.dismiss();
					}
				});
				closeText_re.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						recievedDialog.dismiss();
					}
				});

				final Button btnSort = (Button) layout_re.findViewById(R.id.btn_sort);
				btnSort.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (btnSort.getText().toString().equals("Due Date/Time")) {
							((TextView) recievedDialog.findViewById(R.id.tv_format)).setText("Due Date/Time");
							btnSort.setText("Distance");
							createRecievedDialog(true);
						} else {
							((TextView) recievedDialog.findViewById(R.id.tv_format)).setText("Distance");
							btnSort.setText("Due Date/Time");
							createRecievedDialog(false);
						}
					}
				});

			} else
				llList.removeAllViews();

			for (int i = 0; i < poiCount_re; i++) {
				inflater_re.inflate(R.layout.add_sa_accepted_rows, llList);
			}

			for (int index_re = 0; index_re < llList.getChildCount(); index_re++) {
				TextView address = (TextView) ((LinearLayout) llList.getChildAt(index_re)).findViewById(R.id.tv_sa_address);
				TextView jobComment = (TextView) ((LinearLayout) llList.getChildAt(index_re)).findViewById(R.id.tv_sa_job_comment);
				TextView tvDistance = (TextView) ((LinearLayout) llList.getChildAt(index_re)).findViewById(R.id.tv_distance);
				Button btnAccept = (Button) ((LinearLayout) llList.getChildAt(index_re)).findViewById(R.id.asa_button9);
				Button btnRefuse = (Button) ((LinearLayout) llList.getChildAt(index_re)).findViewById(R.id.asa_button8);
				final Button btnNavigate = (Button) ((LinearLayout) llList.getChildAt(index_re)).findViewById(R.id.asa_button_navigate);
				btnRefuse.setTag(Integer.toString(index_re));
				btnRefuse.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String idxStr = (String) v.getTag();
						PointOfInterest poi = mPois.get(Integer.parseInt(idxStr));
						actionListener.serviceActivityRefused(poi, mPois.get(Integer.parseInt(idxStr)).getCurrentServiceActivity());
						((LinearLayout) llList.getChildAt(Integer.parseInt(idxStr))).setVisibility(View.GONE);
						tvTitleText_re.setText((--poiCount_re) + " Assigned Service Activities");
						llList.refreshDrawableState();

						if (poiCount_re < 1)
							recievedDialog.dismiss();
					}
				});

				btnAccept.setText("Accept");
				btnAccept.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.accept));
				btnAccept.setTag(Integer.toString(index_re));
				btnAccept.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String idxStr = (String) v.getTag();
						PointOfInterest poi = mPois.get(Integer.parseInt(idxStr));
						actionListener.serviceActivityAccepted(poi, mPois.get(Integer.parseInt(idxStr)).getCurrentServiceActivity());
						// dialog.dismiss();
						((LinearLayout) llList.getChildAt(Integer.parseInt(idxStr))).setVisibility(View.GONE);
						tvTitleText_re.setText((--poiCount_re) + " Assigned Service Activities");
						llList.refreshDrawableState();

						if (poiCount_re < 1)
							recievedDialog.dismiss();
					}
				});

				address.setTag(Integer.toString(index_re));
				address.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PointOfInterest poi = mPois.get(Integer.parseInt(v.getTag().toString()));
						Session.MapAct.setMapLocation(poi.getLatitude(), poi.getLongitude(), 18);
						recievedDialog.dismiss();
					}
				});

				jobComment.setTag(Integer.toString(index_re));
				jobComment.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PointOfInterest poi = mPois.get(Integer.parseInt(v.getTag().toString()));
						Session.MapAct.setMapLocation(poi.getLatitude(), poi.getLongitude(), 18);
						navigateTo(poi.getLatitude(), poi.getLongitude());
						recievedDialog.dismiss();
					}
				});
				tvDistance.setTag(Integer.toString(index_re));
				tvDistance.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PointOfInterest poi = mPois.get(Integer.parseInt(v.getTag().toString()));
						Session.MapAct.setMapLocation(poi.getLatitude(), poi.getLongitude(), 18);
						recievedDialog.dismiss();
					}
				});
				btnNavigate.setTag(Integer.toString(index_re));
				btnNavigate.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int position = Integer.parseInt(v.getTag().toString());
						navigateTo(mPois.get(position).getLatitude(), mPois.get(position).getLongitude());
						recievedDialog.dismiss();
					}
				});

				address.setText(mPois.get(index_re).getAddress());
				jobComment.setText(mPois.get(index_re).getCurrentServiceActivity().getClientNotes());
				if (Session.clocation != null) {
					if (isDate) {
						SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						SimpleDateFormat newFormat = new SimpleDateFormat("MMM.dd,yyyy HH:mm:ss");
						String dateTime = null;
						try {
							Date date = oldFormat.parse(mPois.get(index_re).getCurrentServiceActivity().getDateTime());
							dateTime = newFormat.format(date);
						} catch (Exception e) {
							e.printStackTrace();
						}
						tvDistance.setText(dateTime);
					} else
						tvDistance.setText("" + new DecimalFormat("0.#").format(mPois.get(index_re).getDistance()) + " " + disUnit);
				}

			}

			if (recievedDialog != null) {
				if (!recievedDialog.isShowing()) {
					recievedDialog.setCancelable(true);
					recievedDialog.show();
					recievedDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				}
			}
		}
		
	};
	
	public Comparator<PointOfInterest> DISTANCE_ORDER = new Comparator<PointOfInterest>() {

		@Override
		public int compare(PointOfInterest o1, PointOfInterest o2) {
			return o1.getDistance() >= o2.getDistance() ? 1 : -1;
		}

	};

	public Comparator<PointOfInterest> DATE_ORDER = new Comparator<PointOfInterest>() {

		@Override
		public int compare(PointOfInterest o1, PointOfInterest o2) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d1 = null;
			Date d2 = null;
			try {
				d1 = f.parse(o1.getCurrentServiceActivity().getDateTime());
				d2 = f.parse(o2.getCurrentServiceActivity().getDateTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (d1.getTime() > d2.getTime() ? 1 : -1);
		}

	};
}
