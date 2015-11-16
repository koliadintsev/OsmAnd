package com.operasoft.snowboard.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.array;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.operasoft.snowboard.ContractListAdapter;
import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.DropEmployees;
import com.operasoft.snowboard.database.DropEmployeesDao;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.engine.PointOfInterest;

public class ContractListCustomDialog {
	
	private Context                             mContext;
	private Dialog                              dialog;
	private DropEmployeesDao                    dropEmployeesDao;
	private List<DropEmployees>                 dropEmployeesList = new ArrayList<DropEmployees>();
	private String                              cDate;
	private SimpleDateFormat                    dateFormatter;
	private Date                                date = new Date(); 
	private ListView                            serviceLocationListView;
	private ListView                            employeeListView;
	private ArrayList<String>                   employeesList = new ArrayList<String>();
	private ArrayList<DropEmployees>            employeesPickList = new ArrayList<DropEmployees>();
	private dialogStatus                        status = dialogStatus.SERVICE_LOCATION;
	private UsersDao                            userDao;
	private ServiceLocationDao                  slDao;

	private enum dialogStatus {
		SERVICE_LOCATION, EMPLOYEES_LIST
	}
	
	public ContractListCustomDialog(Context context) {
		this.mContext = context;
		this.dropEmployeesDao = new DropEmployeesDao();
		this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		this.serviceLocationListView = new ListView(mContext);
		this.employeeListView = new ListView(mContext);
		this.userDao = new UsersDao();
		this.slDao = new ServiceLocationDao();
		cDate = dateFormatter.format(date);
	}
	
	public void createDialog(){
		
		dialog = new Dialog(mContext);
		dropEmployeesList = dropEmployeesDao.getdropEmployeeDistinct(cDate);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.title_contract_list,(ViewGroup) dialog.findViewById(R.id.root_punch));
		TextView tv1 = (TextView) layout.findViewById(R.id.tv_sl_adrs);
		TextView tv2 = (TextView) layout.findViewById(R.id.tv_job_number);
		TextView tv3 = (TextView) layout.findViewById(R.id.tv_date);
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
		TextView closeText = (TextView) layout.findViewById(R.id.textView2);
		iMg_cancel_dialog.setOnClickListener(contractListClickListener);
		closeText.setOnClickListener(contractListClickListener);
		
		//whether to show employees list or service location list
		switch (status) {
		case EMPLOYEES_LIST:
			tv1.setText("Employee Name");
			tv2.setText("");
			tv3.setText("");
			ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mContext,R.layout.custom_list_item, employeesList);
			employeeListView.setAdapter(listAdapter);
			builder.setView(employeeListView);
			break;
		case SERVICE_LOCATION:
			ContractListAdapter adapter = new ContractListAdapter(mContext, R.layout.contract_list_row, dropEmployeesList);
			serviceLocationListView.setAdapter(adapter);
			builder.setView(serviceLocationListView);
			break;

		default:
			break;
		}
	builder.setCustomTitle(layout);
	dialog = builder.create();
	dialog.setCancelable(false);
	dialog.show();
	dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
	serviceLocationListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				employeesPickList = dropEmployeesDao.getDropEmployeesOnSL(dropEmployeesList.get(pos).getServiceLocationId(), cDate);
				for (DropEmployees drop : employeesPickList) {
					employeesList.add(userDao.getById(drop.getEmployeeId()).getFirstName());
				}
				if (employeesList.size() == 0) {
					dialog.dismiss();
				PointOfInterest poi = new PointOfInterest(dropEmployeesList.get(pos).getServiceLocationId());
				poi.attachServiceLocation(slDao.getById(dropEmployeesList.get(pos).getServiceLocationId()));
				ForemanDailySheetDialogHandler foremanDialogHandler = new ForemanDailySheetDialogHandler(mContext, poi);
				foremanDialogHandler.createDialog();
				} else {
					dialog.dismiss();
					status = dialogStatus.EMPLOYEES_LIST;
					createDialog();
				}
			}
		});
	}

	OnClickListener contractListClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iMg_cancel_dialog:
			case R.id.textView2:
				dialog.dismiss();
				break;

			default:
				break;
			}
		}
	};
}
