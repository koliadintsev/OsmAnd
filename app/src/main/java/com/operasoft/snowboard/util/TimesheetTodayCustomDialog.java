package com.operasoft.snowboard.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.DropEmployeesDao;
import com.operasoft.snowboard.database.Punch;
import com.operasoft.snowboard.database.PunchDao;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.database.UsersDao;

public class TimesheetTodayCustomDialog {

	private Context mContext;
	private Dialog dialog;
	private List<Punch> punchlList = new ArrayList<Punch>();
	private String cDate;
	private Date date = new Date();
	private PunchDao punchDao;
	private UsersDao usersDao;
	private DropEmployeesDao dropEmployeesDao;
	private ServiceLocationDao serviceLocationDao;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	TimesheetTodayCustomDialog(Context context) {
		this.mContext = context;
		cDate = dateFormat.format(date);
		punchDao = new PunchDao();
		Utils utils = Utils.getInstance(context);
		String imeiNum = utils.getIMEI();
		punchlList = punchDao.listEmployees(cDate, imeiNum);
		usersDao = new UsersDao();
		dropEmployeesDao = new DropEmployeesDao();
		serviceLocationDao = new ServiceLocationDao();
	}

	void createDialog() {

		dialog = new Dialog(mContext);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialoag_listview_punch_clock, (ViewGroup) dialog.findViewById(R.id.root_punch));
		// Dialog close button and Text on click listener.
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
		ImageView iMg_title_dialog = (ImageView) layout.findViewById(R.id.iMg_dialog_title);
		iMg_title_dialog.setImageResource(R.drawable.add);
		TextView closeText = (TextView) layout.findViewById(R.id.textView2);
		TextView tvTitleText = (TextView) layout.findViewById(R.id.textView1);
		tvTitleText.setText("Time Sheet Today");
		iMg_cancel_dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		closeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		builder.setCustomTitle(layout);
		View layout1;
		layout1 = inflater.inflate(R.layout.dialog_list_worksheet, null);
		LinearLayout llList = (LinearLayout) layout1.findViewById(R.id.ll_timesheet_today);
		for (int i = 0; i < punchlList.size(); i++) {
			inflater.inflate(R.layout.timesheet_today_list_item, llList);
		}
		for (int i = 0; i < llList.getChildCount(); i++) {
			TextView employeeName = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_employee_name);
			TextView employeePunchTime = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_time_punch_in);
			TextView employeePunchOutTime = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_punch_out_time);
			TextView employeeDropTime = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_drop_off_time);
			TextView employeeDropSite = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_drop_off_site);
			TextView employeePickTime = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_pick_up_time);
			employeeName.setText(usersDao.getById(punchlList.get(i).getUserId()).getFirstName());
			String[] punch = punchlList.get(i).getDateTime().split(" ");
			employeePunchTime.setText(punch[1]);
			employeePunchOutTime.setText(punchDao.getOutTimefromUserId(cDate, punchlList.get(i).getUserId(), punchlList.get(i).getDateTime()));
			employeeDropTime.setText(dropEmployeesDao.getEmployeedropTime(cDate, punchlList.get(i).getUserId()));
			String dropLocation = dropEmployeesDao.getEmployeedropLocation(cDate, punchlList.get(i).getUserId());
			employeePickTime.setText(dropEmployeesDao.getEmployeepickTime(cDate, punchlList.get(i).getUserId(), punchlList.get(i).getDateTime()));
			if (!dropLocation.equals("not dropped"))
				employeeDropSite.setText(serviceLocationDao.getById(dropEmployeesDao.getEmployeedropLocation(cDate, punchlList.get(i).getUserId())).getAddress());
		}
		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

	}

}
