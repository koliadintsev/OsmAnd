package com.operasoft.snowboard.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.view.SortableLabel.SortableLabelListener;

/**
 * @author dounaka
 * display a list of users sortable
 * 
 * 2 modes : 
 * view all : onsite or invehicle for all vehicles
 * not view all : punchin on the current tablet
 * 
 * 
 */
public class StaffListView extends AppView implements SortableLabelListener, OnCheckedChangeListener {

	private final static ArrayList<Vehicle> mVehicles = new ArrayList<Vehicle>();
	private final static ArrayList<ServiceLocation> mServiceLocations = new ArrayList<ServiceLocation>();
	private HashSet<SortableLabel> mLabels;
	private ViewGroup panelstafflist;
	private ListView lstStaffView;
	private CheckBox mChkAllUsers;
	private UserComparator userComparator;

	ArrayList<User> mAllUsers = new ArrayList<User>(), displayedUsers = new ArrayList<User>();
	ArrayList<User> mActiveUsers = new ArrayList<User>();

	SortableLabel mSortName, mSortStatus, mSortDatetime, mSortVehicle, mSortServiceLocation;

	public StaffListView(Context ctx) {
		super(ctx);
	}

	public StaffListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StaffListView(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
	}

	@Override
	protected int getViewResId() {
		return R.layout.staff_list_view;
	}

	@Override
	protected void bindControls(Context ctx) {

		mChkAllUsers = (CheckBox) findViewById(R.id.chkallusers);

		mChkAllUsers.setOnCheckedChangeListener(this);

		lstStaffView = (ListView) findViewById(R.id.lststafflist);
		mSortName = (SortableLabel) findViewById(R.id.sortbyname);
		mSortStatus = (SortableLabel) findViewById(R.id.sortbystatus);
		mSortDatetime = (SortableLabel) findViewById(R.id.sortbydatetime);
		mSortVehicle = (SortableLabel) findViewById(R.id.sortbyvehicle);
		mSortServiceLocation = (SortableLabel) findViewById(R.id.sortbyservicelocation);

		mSortName.listener = this;
		mSortStatus.listener = this;
		mSortDatetime.listener = this;
		mSortVehicle.listener = this;
		mSortServiceLocation.listener = this;

		mSortName.setText("Name", "name");
		mSortStatus.setText("Status", "work_status");
		mSortDatetime.setText("Status date", "work_status_date");
		mSortVehicle.setText("Actual Vehicle", "vehicle");
		mSortServiceLocation.setText("Actual Location", "work_status_date");

		mLabels = new HashSet<SortableLabel>();
		mLabels.add(mSortName);
		mLabels.add(mSortStatus);
		mLabels.add(mSortDatetime);
		mLabels.add(mSortVehicle);
		mLabels.add(mSortServiceLocation);

		currentSort = mSortName;
		currentSort.setUp();

		userComparator = new UserComparator();

	}

	SortableLabel currentSort;

	public static final String STATUS_ONSITE = "On Site";
	public static final String STATUS_INVEHICLE = "In Vehicle";
	public static final String STATUS_INACTIVE = "Inactive";

	private boolean isInVehicle(User user, String vehicleid) {
		if (vehicleid != null

		&& (user.vehicle != null)

		&& user.getWorkStatus().equals("invehicle")

		&& vehicleid.equals(user.vehicle.getId()))
			return true;
		return false;

	}

	private boolean isActive(User user) {
		return user.getWorkStatus() != null && (!user.getWorkStatus().equals("inactive"));
	}

	@Override
	public void sort(final SortableLabel clickedLabel) {
		for (SortableLabel label : mLabels)
			if (label != clickedLabel)
				label.noSort();
		currentSort = clickedLabel;
		show();
	}

	public void initUserList(List<User> users, final String vehicleid) {
		mAllUsers.clear();
		mActiveUsers.clear();
		for (User user : users) {
			if (isActive(user))
				mAllUsers.add(user);
			if (isInVehicle(user, vehicleid))
				mActiveUsers.add(user);

		}

	}

	public void show() {
		displayedUsers.clear();
		if (!mChkAllUsers.isChecked())
			displayedUsers.addAll(mActiveUsers);
		else
			displayedUsers.addAll(mAllUsers);
		Collections.sort(displayedUsers, userComparator);
		lstStaffView.setAdapter(new UserAdapter(getContext(), displayedUsers));
	}

	class UserComparator implements Comparator<User> {
		private int compareValue(String lval, String rval) {
			if (lval == null)
				return -1;
			else if (rval == null)
				return 1;
			else
				return lval.compareTo(rval);
		}

		private int compareField(User lhs, User rhs) {
			if (currentSort == mSortName) {
				return compareValue(lhs.getFullName(), rhs.getFullName());

			} else if (currentSort == mSortStatus) {
				return compareValue(lhs.workStatusLabel, rhs.workStatusLabel);

			} else if (currentSort == mSortDatetime) {
				return compareValue(lhs.getWorkStatusDate(), rhs.getWorkStatusDate());

			} else if (currentSort == mSortVehicle) {
				if (lhs.vehicle != null && rhs.vehicle != null)
					return compareValue(lhs.vehicle.getName(), rhs.vehicle.getName());
				else if (lhs.vehicle != null && rhs.vehicle == null)
					return -1;
				else if (lhs.vehicle == null && rhs.vehicle != null)
					return 1;
				else
					return 0;

			} else if (currentSort == mSortServiceLocation) {
				if (lhs.serviceLocation != null && rhs.serviceLocation != null)
					return compareValue(lhs.serviceLocation.getName(), rhs.serviceLocation.getName());
				else if (lhs.serviceLocation != null && rhs.serviceLocation == null)
					return -1;
				else if (lhs.serviceLocation == null && rhs.serviceLocation != null)
					return 1;
				else
					return 0;
			}
			return 0;
		}

		@Override
		public int compare(User lhs, User rhs) {
			if (currentSort.isUp())
				return compareField(lhs, rhs);
			else
				return (-1) * compareField(lhs, rhs);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		show();
	}
}
