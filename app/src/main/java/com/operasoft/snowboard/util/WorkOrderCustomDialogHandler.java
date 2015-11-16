package com.operasoft.snowboard.util;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Assets;
import com.operasoft.snowboard.database.AssetsDao;
import com.operasoft.snowboard.database.Damage;
import com.operasoft.snowboard.database.DamageType;
import com.operasoft.snowboard.database.DamageTypeDao;
import com.operasoft.snowboard.database.Uploads;
import com.operasoft.snowboard.database.WorkOrder;
import com.operasoft.snowboard.database.WorkOrderPicture;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.push.DamagePushSync;
import com.operasoft.snowboard.dbsync.push.UploadsPushSync;
import com.operasoft.snowboard.dbsync.push.WorkOrderPushSync;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestManager;

import android.app.DatePickerDialog;

public class WorkOrderCustomDialogHandler implements DatePickerDialog.OnDateSetListener{

	private Context mContext;
	private PointOfInterest mPoi;
	private Dialog dialog;
	private Spinner spinner;
	private HashMap<String, String> spinnerItemsListMap;
	private ArrayList<String> spinnerItemsList;
	private ArrayList<String> workType;
	private ArrayList<String> methodWorkList;
	private EditText addressEdit;

	private LinearLayout imageLayout;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private String fullPathFilename = "";
	private List<String> allImages;
	private LinearLayout horizLayout;
	private Spinner workOrderType; 
	private Spinner assetAndLot;
	private GregorianCalendar dueDate;
	private EditText dueDateField;
	private Spinner methodWork;
	private EditText workToDo;
	private String filenameOnly;
	private EditText comments;
	List<Assets> liste;
	

	private String NEW_LOCATION = "New Location";
	private String ASSET = "Asset";
	private String CITIZEN_LOT = "Citizen Lot";
	private String CITY_LOT = "City Lot";
	private String MANUAL_DESC = "Manual Description";
	private String WORK_ITEM_SELECTION = "Work Item Selection";
	
	private boolean isNewLocationSelect = true;
	private boolean isManualDescSelect = true;


	public WorkOrderCustomDialogHandler(Context context, PointOfInterest poi) {
		mContext = context;
		mPoi = poi;
		allImages = new ArrayList<String>();
	}

	public void createDialog() {

		dialog = new Dialog(mContext);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_wo_header_menu, (ViewGroup) dialog.findViewById(R.id.root_punch));

		// Dialog close button and Text on click listener.
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iv_dhm_cancel);
		TextView closeText = (TextView) layout.findViewById(R.id.tv_dhm_cancel);
		iMg_cancel_dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				try {
					//Delete the pictures from local tablet if they aren't sent to Server. (Saving Space)
					onCloseDeletePictures();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		closeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		((ImageView) layout.findViewById(R.id.iv_dhm_icon)).setBackgroundResource(R.drawable.mission);
		((TextView) layout.findViewById(R.id.tv_dhm_title)).setText("Work Order");
		builder.setCustomTitle(layout);
		View layout1 = inflater.inflate(R.layout.dialog_work_order, null);

		workToDo = (EditText)layout1.findViewById(R.id.wo_to_do);
		dueDateField = (EditText)layout1.findViewById(R.id.wo_due_date);
		methodWork = (Spinner) layout1.findViewById(R.id.wo_spn_method_work);
		imageLayout = (LinearLayout) layout1.findViewById(R.id.woimageLayout);	
		horizLayout = (LinearLayout) layout1.findViewById(R.id.wohorizLayout);
		workOrderType = (Spinner) layout1.findViewById(R.id.wo_spn_type);
		assetAndLot = (Spinner) layout1.findViewById(R.id.wo_spn_lot_asset);
		spinner = (Spinner) layout1.findViewById(R.id.wo_spn_damages_type);
		addressEdit = (EditText) layout1.findViewById(R.id.wo_address);
		comments = (EditText) layout1.findViewById(R.id.wo_comments);
		
		
		((Button) layout1.findViewById(R.id.wo_btn_submit)).setOnClickListener(woListener);
		((ImageView) layout1.findViewById(R.id.wonewPicture)).setOnClickListener(takePictureListener);
		workOrderType.setOnItemSelectedListener(workOrderTypeListener);
		dueDateField.setOnFocusChangeListener(onFocusDueDate);
		methodWork.setOnItemSelectedListener(onSelectedMethodWork);
		

		spinnerItemsListMap = new HashMap<String, String>();
		spinnerItemsList = new ArrayList<String>();
		DamageTypeDao damagetypes = new DamageTypeDao();

		List<DamageType> spinnerItems = damagetypes.listAll();
		for (DamageType damage : spinnerItems) {
			spinnerItemsList.add(damage.getName());
			spinnerItemsListMap.put(damage.getName(), damage.getId());
		}

		
		workType = new ArrayList<String>();
		workType.add(NEW_LOCATION);
		workType.add(ASSET);
		workType.add(CITY_LOT);
		workType.add(CITIZEN_LOT);

		ArrayAdapter<String> adapterWO = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, workType);
		adapterWO.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		workOrderType.setAdapter(adapterWO);

		
		methodWorkList = new ArrayList<String>();
		methodWorkList.add(MANUAL_DESC);
		methodWorkList.add(WORK_ITEM_SELECTION);
		ArrayAdapter<String> adapterMethod = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, methodWorkList);
		adapterMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		methodWork.setAdapter(adapterMethod);


		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerItemsList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();

	}

	public void sendWoDetails() {
		Runnable sendWo = new Runnable(){ 
			@Override
			public void run() {
				List<Uploads> uploads = new ArrayList<Uploads>();
				if(allImages.size()>0){
					for(String path : allImages){
						Uploads up = createUploads(path);					
						UploadsPushSync.getInstance().pushData(mContext, up);
						uploads.add(up);
					}
				}
				
				WorkOrder wo = createWorkOrdertoPush();
				
				if(uploads.size()>0){
					for(Uploads up : uploads)
						wo.add(createWOPic(up));
				}
				
				WorkOrderPushSync.getInstance().pushData(mContext, wo);
				allImages.clear();
			}
		};
		new Thread(sendWo).start();
	}
	
	private Uploads createUploads(String path){
		Uploads up = new Uploads();
		up.setFilename(path);
		up.setImei_no(Session.getImeiCompany().getImeiNo());
		up.setModel(WorkOrderPicture.model);
		up.setCompanyId(Session.getCompanyId());
		if (Session.clocation != null) {
			up.setGps_cordinates(Session.clocation.getLatitude() + " " + Session.clocation.getLongitude());
		}
		return up;
	}
	
	private WorkOrder createWorkOrdertoPush(){
		WorkOrder wo = new WorkOrder();
		
		if(Session.route != null){
			String route_id = Session.route.getId();
			wo.setRoute_id(route_id);
		}
		
		wo.setStatus(Damage.DAMAGE_PENDING);
		wo.setDamageTypeId(spinnerItemsListMap.get(spinner.getSelectedItem()));
		Format formatter = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate =  formatter.format(dueDate.getTime());
		wo.setDueDate(formattedDate);
		//wo.setDescription(damageEdit.getText().toString());
		wo.setCompanyId(Session.getCompanyId());
		wo.setCreatorId(Session.getDriver().getId());
		wo.setWorkType(methodWork.getSelectedItem().toString().equals(MANUAL_DESC)?"Manual":"WorkItemSelection");
		if(isNewLocationSelect){
			if(addressEdit.getText() != null && !"".equals(addressEdit.getText().toString())){
				wo.setForeignKey("NewLocation");
				wo.setForeignValue(addressEdit.getText().toString());
			}			
		}
		wo.setVehicleId(Session.getVehicle().getId());
		wo.setDateTime(CommonUtils.UtcDateNow());
		if(isManualDescSelect){
			wo.setWorkToDo(workToDo.getText().toString());
		}else{
			wo.setDescription(comments.getText().toString());
		}
		if (Session.clocation != null) {
			wo.setGpsCoordinates(Session.clocation.getLatitude() + " " + Session.clocation.getLongitude());
		}
		return wo;
	}
	
	private WorkOrderPicture createWOPic(Uploads up){
		WorkOrderPicture pic = new WorkOrderPicture();
		pic.setUploadId(up.getId());
		pic.setCreatorId(Session.getDriver().getId());
		pic.setFilename(up.getFilename());
		return pic;
	}



	protected boolean checkMandatoryFields() {
		if(dueDate == null){
			Activity host = (Activity)mContext;
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(host);
			alertDialogBuilder.setMessage(R.string.missing_due_date);
			alertDialogBuilder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int id) {
					dialog.dismiss();
				}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show alert
			alertDialog.show();
			
			return false;
		}else if(isNewLocationSelect){
			if("".equals(addressEdit.getText().toString())){
				Activity host = (Activity)mContext;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(host);
				alertDialogBuilder.setMessage(R.string.missing_address);
				alertDialogBuilder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						dialog.dismiss();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show alert
				alertDialog.show();
				return false;
			}			
		}
		return true;
	}

	public String getFullPathFilename() {
		return fullPathFilename;
	}

	public void setFullPathFilename(String fullPathFilename) {
		this.fullPathFilename = fullPathFilename;
	}


	private Uri createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		filenameOnly = JPEG_FILE_PREFIX + Session.getImeiCompany().getImeiNo() +"_"+ timeStamp + JPEG_FILE_SUFFIX;
		fullPathFilename = ImageUtils.IMAGE_FILEPATH+filenameOnly;	
		Uri uri = Uri.parse(fullPathFilename);
		return uri;
	}

	public void addImage(Context context, final String path){
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		horizLayout.setVisibility(View.VISIBLE);
		ImageView pictureView = new ImageView(context);
		/* Get the size of the ImageView */
		int targetW = imageLayout.getWidth();
		int targetH = imageLayout.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

		/* Associate the Bitmap to the ImageView */
		pictureView.setImageBitmap(bitmap);
		pictureView.setPadding(0, 0, 10, 0);
		pictureView.setAdjustViewBounds(true);
		pictureView.setTag(filenameOnly);
		pictureView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Use Standard Android Picture Viewer
				Intent intentQuick = new Intent(Intent.ACTION_VIEW);
				Log.d("URI FOR PICTURE", Uri.parse(path).toString());

				intentQuick.setDataAndType(Uri.fromFile(new File(path)), "image/*");
				intentQuick.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				Activity host = (Activity)mContext;
				host.startActivity(intentQuick);
			}
		});
		pictureView.setOnLongClickListener(onLongClickDelete);
		imageLayout.addView(pictureView);
		allImages.add(filenameOnly);
	}


	public void onCloseDeletePictures() throws Exception{
		if(allImages.size()>0){
			for(String filename : allImages){
				deletePicture(ImageUtils.IMAGE_FILEPATH+filename);
			}
		}
		allImages.clear();
	}
	
	public void deletePicture(String path) throws Exception{
		File deleteFile = new File(path);
		if (deleteFile.exists()) {
			boolean isDeleted = deleteFile.delete();
	
			if(!isDeleted)
				throw new Exception("Couldn't delete the file: " + path);
		}
	}
	
	private void fillUpSpinnerAssetAndLot(String selection){
		if(selection.equals(ASSET)){
			ArrayList<String> assetList = new ArrayList<String>();
			/*assetList.add("Please select an asset.");*/
			
			AssetsDao assetsDao = new AssetsDao();
			liste = assetsDao.getAllAssets();
			
			for(int i=0;i<liste.size();i++){
				assetList.add(liste.get(i).getNumber() + '-' + liste.get(i).getName());
			}
			
			ArrayAdapter<String> adapterWO = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, assetList);
			adapterWO.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			assetAndLot.setAdapter(adapterWO);
		}else if(selection.equals(CITIZEN_LOT)){
			ArrayList<String> assetList = new ArrayList<String>();
			assetList.add("Please select a citizen lot.");
			ArrayAdapter<String> adapterWO = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, assetList);
			adapterWO.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			assetAndLot.setAdapter(adapterWO);
		}else if(selection.equals(CITY_LOT)){
			ArrayList<String> assetList = new ArrayList<String>();
			assetList.add("Please select a city lot.");
			ArrayAdapter<String> adapterWO = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, assetList);
			adapterWO.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			assetAndLot.setAdapter(adapterWO);
		}

	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		dueDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
		Format formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
		dueDateField.setText(formatter.format(dueDate.getTime()));
	}

	/*
	 * Listeners
	 */
	
	public OnLongClickListener onLongClickDelete = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			if(v instanceof ImageView){	
				final ImageView vv = (ImageView)v;
				Activity host = (Activity)mContext;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(host);
				alertDialogBuilder.setMessage(R.string.delete_picture);
				// set positive button: Yes message
				alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						ImageView button = ((ImageView)vv);
						try {
							deletePicture(ImageUtils.IMAGE_FILEPATH+String.valueOf(button.getTag()));
							int index = allImages.indexOf(String.valueOf(button.getTag()));
							allImages.remove(index);

							imageLayout.removeViewAt(index);

							if(allImages.size() == 0){
								horizLayout.setVisibility(View.GONE);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				// set negative button: No message
				alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int id) {
						// cancel the alert box and put a Toast to the user
						dialog.cancel();
					}
				});					 
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show alert
				alertDialog.show();
			}
			return false;
		}
	};

	public OnClickListener takePictureListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			Uri uri = null;
			try {
				uri = createImageFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Start Intent for Camera
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getFullPathFilename())));

			Activity host = (Activity)v.getContext();
			host.startActivityForResult(intent, ACTION_TAKE_PHOTO_B);
		}
	};
	
	public OnItemSelectedListener workOrderTypeListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if(NEW_LOCATION.equals(workType.get(position))){
				addressEdit.setVisibility(View.VISIBLE);
				assetAndLot.setVisibility(View.GONE);
				isNewLocationSelect = true;
			}else{
				addressEdit.setVisibility(View.GONE);
				addressEdit.setText("");
				assetAndLot.setVisibility(View.VISIBLE);
				fillUpSpinnerAssetAndLot(workType.get(position));
				isNewLocationSelect = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	}; 

	public OnClickListener woListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_incident:
				mPoi = PointOfInterestManager.getInstance().getInsidePolygonPoi();
				createDialog();
				break;
			case R.id.wo_btn_submit:
				if(checkMandatoryFields()){
					sendWoDetails();
					dialog.dismiss();
				}
			}
		}
	};
	
	public OnFocusChangeListener onFocusDueDate = new OnFocusChangeListener(){
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus){
				int yearMain = Calendar.getInstance().get(Calendar.YEAR);
				int monthMain = Calendar.getInstance().get(Calendar.MONTH);
				int dayMain = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				DatePickerDialog dp1 = new DatePickerDialog(mContext, WorkOrderCustomDialogHandler.this, yearMain , monthMain, dayMain+1);
				dp1.show();
			}
		}
	};
	
	public OnItemSelectedListener onSelectedMethodWork = new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if(MANUAL_DESC.equals(methodWorkList.get(position))){
				workToDo.setVisibility(View.VISIBLE);
				comments.setVisibility(View.GONE);
				isManualDescSelect = true;
			}else{
				workToDo.setVisibility(View.GONE);
				comments.setVisibility(View.VISIBLE);
				workToDo.setText("");
				isManualDescSelect = false;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

}
