package com.operasoft.snowboard.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Contract;
import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.Dto;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.database.WorksheetCompanyable;
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
import com.operasoft.snowboard.dbsync.push.WorksheetPushSync;
import com.operasoft.snowboard.util.Session;

public class WorksheetFragment extends Fragment implements OnClickListener {

	private WorksheetPagerAdapter mWorksheetAdapter;
	private ViewPager mViewPager;
	private static final String[] titles = new String[] { "General", "Labour", "Equipment", "Material", "Travel Time" };

	private ViewGroup mPanelTitle, mPanelToolbar;
	Worksheets worksheet;
	WorksheetPageFragment current, previous;

	Contract currentContract;
	ServiceLocation currentServiceLocation;
	Button mBtnSave, mBtnSubmit, mBtnAddMore;
	TextView mTxtContractJobNumber, mTxtServiceLocation;

	public WorksheetFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_worksheet, null);
		mViewPager = (ViewPager) view.findViewById(R.id.pagerworksheet);
		mPanelTitle = (ViewGroup) view.findViewById(R.id.paneltitle);
		mPanelToolbar = (ViewGroup) view.findViewById(R.id.paneltoolbar);
		mTxtContractJobNumber = (TextView) view.findViewById(R.id.txtcontractjobnumber);
		mTxtServiceLocation = (TextView) view.findViewById(R.id.txtservicelocation);
		mBtnSave = (Button) view.findViewById(R.id.btnsave);
		mBtnSubmit = (Button) view.findViewById(R.id.btnsubmit);
		mBtnAddMore = (Button) view.findViewById(R.id.btnaddmore);

		mBtnSave.setOnClickListener(this);
		mBtnSubmit.setOnClickListener(this);
		mBtnAddMore.setOnClickListener(this);
		mBtnSave.setVisibility(View.GONE);

		final PagerTabStrip strip = (PagerTabStrip) (view.findViewById(R.id.pagertitlestrip));
		strip.setDrawFullUnderline(true);
		strip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		strip.setTabIndicatorColor(Color.DKGRAY);
		strip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		buildViewPager();
		return view;
	}

	private void displayHeader(Worksheets worksheet) {
		if (currentContract == null) {
			currentContract = (new ContractsDao()).getById(worksheet.getContractId());
		}
		if (currentContract != null) {
			mTxtContractJobNumber.setText(currentContract.getContract_number() + " / " + currentContract.getJobNumber());
			if (currentServiceLocation == null) {
				currentServiceLocation = (new ServiceLocationDao()).getById(currentContract.getService_location_id());
			}
		}
		if (currentServiceLocation != null) {
			mTxtServiceLocation.setText(currentServiceLocation.getName() + " / " + currentServiceLocation.getAddress());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getActivity().getIntent().getExtras() != null) {
			final String worksheetid = getActivity().getIntent().getExtras().getString("worksheetid");
			final WorksheetsDao wDao = new WorksheetsDao();
			if (worksheetid != null)
				worksheet = wDao.getById(worksheetid);
			if (worksheet != null) {
				final String startDate = worksheet.getStartDate();
				if (startDate == null || startDate.trim().length() == 0) {
					final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					worksheet.setStartDate(dateFormat.format(new Date()));
				}
				displayHeader(worksheet);
			}
			Log.d("wid", "id:" + worksheet.getId());
		}
		if (worksheet == null)
			getActivity().finish();
	}

	// keep track of the page that has been read
	HashSet<Integer> visitedPages = new HashSet<Integer>();

	void buildViewPager() {
		mWorksheetAdapter = new WorksheetPagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mWorksheetAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			boolean firstmove = true;

			@Override
			public void onPageSelected(int pageSelected) {
				if (pageSelected == 0)
					mBtnAddMore.setVisibility(View.GONE);
				else
					mBtnAddMore.setVisibility(View.VISIBLE);

				if (firstmove) {
					firstmove = false;
					current = mWorksheetAdapter.pages[0];
					visitedPages.add(0);
				}
				visitedPages.add(pageSelected);
				previous = current;
				current = mWorksheetAdapter.pages[pageSelected];
				if (previous != null)
					previous.getWorksheet(); // need to map the entity with the view
				// current.show(worksheet);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	public class WorksheetPagerAdapter extends FragmentStatePagerAdapter {

		static final int INDEX_LABOUR = 1;
		static final int INDEX_EQUIPMENT = 2;
		static final int INDEX_MATERIAL = 3;
		static final int INDEX_TRAVELTIME = 4;

		final WorksheetPageFragment[] pages = new WorksheetPageFragment[] {

		new WorksheetGeneralFragment(),

		new WorksheetLabourFragment(),

		new WorksheetEquipmentFragment(),

		new WorksheetMaterialFragment(),

		new WorksheetTravelTimeFragment(),

		};

		public WorksheetPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			pages[i].worksheet = WorksheetFragment.this.worksheet;
			return pages[i];
		}

		@Override
		public int getCount() {
			return pages.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position].toUpperCase();
		}
	}

	public void onBackPressed() {
		mPanelTitle.setVisibility(View.VISIBLE);
		mPanelToolbar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if (current == null)
			current = mWorksheetAdapter.pages[0];
		current.getWorksheet();
		if (v == mBtnSave) {
			saveWorksheet(this.worksheet);
			Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
		} else if (v == mBtnSubmit) {

			saveWorksheet(this.worksheet);
			Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();

			publish(this.worksheet);
			Toast.makeText(getActivity(), "published", Toast.LENGTH_SHORT).show();
			getActivity().finish();
		} else if (v == mBtnAddMore) {
			if (current instanceof ListPage) {
				((ListPage) current).addNewRow();
			}

		}
	}

	private void publish(final Worksheets worksheet) {

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPostExecute(Void result) {
			}

			@Override
			protected void onPreExecute() {
			}

			@Override
			protected Void doInBackground(Void... params) {
				WorksheetPushSync.getInstance().pushData(getActivity(), worksheet);
				return null;
			}
		};
		task.execute();

	}

	private String now;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

	private void saveWorksheet(Worksheets worksheet) {
		now = dateFormat.format(new Date());
		WorksheetsDao wDao = new WorksheetsDao();
		wDao.insertOrReplace(worksheet);
		Log.d("wid", "id:" + worksheet.getId());
		WorksheetLabourDao labourDao = new WorksheetLabourDao();

		if (Session.getDriver() != null) {
			if (worksheet.getCreatorId() == null || worksheet.getCreatorId().trim().length() == 0)
				worksheet.setCreatorId(Session.getDriver().getId());
			worksheet.setUserId(Session.getDriver().getId());
		}
		if (visitedPages.contains(WorksheetPagerAdapter.INDEX_EQUIPMENT)) {
			WorksheetEquipmentDao equipmentDao = new WorksheetEquipmentDao();
			//equipmentDao.deleteListAttachedWithWorksheet(worksheet.getId());
			for (WorksheetEquipment equip : worksheet.getWorksheetEquipmentList()) {
				saveDto(equip, equipmentDao, worksheet.getId(), worksheet.getCompanyId());
			}
		}
		if (visitedPages.contains(WorksheetPagerAdapter.INDEX_MATERIAL)) {
			WorksheetMaterialDao materialDao = new WorksheetMaterialDao();
			//materialDao.deleteListAttachedWithWorksheet(worksheet.getId());
			for (WorksheetMaterial material : worksheet.getWorksheetMaterialList()) {
				saveDto(material, materialDao, worksheet.getId(), worksheet.getCompanyId());
			}
		}

		WorksheetTravelTimeDao traveltimeDao = new WorksheetTravelTimeDao();
		if (visitedPages.contains(WorksheetPagerAdapter.INDEX_TRAVELTIME)) {
			//traveltimeDao.deleteListAttachedWithWorksheet(worksheet.getId());
			for (WorksheetTravelTime travel : worksheet.getWorksheetTravelTimeList()) {
				saveDto(travel, traveltimeDao, worksheet.getId(), worksheet.getCompanyId());
			}
		}

		boolean newLabour = false;
		if (visitedPages.contains(WorksheetPagerAdapter.INDEX_LABOUR)) {
			//labourDao.deleteListAttachedWithWorksheet(worksheet.getId());
			for (WorksheetLabour labour : worksheet.getWorksheetLabourList()) {
				if (labour.isNew()) {
					WorksheetTravelTime traveltime = new WorksheetTravelTime();
					traveltime.setUserId(labour.getUserId());
					traveltime.setTravelDate(labour.getLabourDate());
					traveltime.setWorksheetId(worksheet.getId());
					traveltime.setCompanyId(labour.getCompanyId());
					traveltimeDao.insert(traveltime);
					worksheet.getWorksheetTravelTimeList().add(traveltime);
					newLabour = true;
				}
				saveDto(labour, labourDao, worksheet.getId(), worksheet.getCompanyId());
			}
		}
		if (newLabour) {
			// force to read again the list from database (lazy loading)
			worksheet.setWorksheetTravelTimeList(null);
		}
		visitedPages.clear();
	}

	private <T extends Dto> void saveDto(T dto, Dao<T> dao, String worksheetid, String companyid) {
		//dto.setModified(now);
		if (dto.isNew()) {
			//dto.setCreated(now);
			if (dto instanceof WorksheetCompanyable) {
				WorksheetCompanyable worksheetCompany = (WorksheetCompanyable) dto;
				worksheetCompany.setWorksheetId(worksheetid);
				worksheetCompany.setCompanyId(companyid);
			}
		}
		dao.insertOrReplace(dto);
	}
}
