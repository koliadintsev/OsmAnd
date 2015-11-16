package com.operasoft.snowboard.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.DamageType;
import com.operasoft.snowboard.database.DamageTypeDao;
import com.operasoft.snowboard.database.Deficiency;
import com.operasoft.snowboard.database.DeficiencyPicture;
import com.operasoft.snowboard.database.RouteSelectedDao;
import com.operasoft.snowboard.database.Uploads;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.push.DeficiencyPushSync;
import com.operasoft.snowboard.dbsync.push.UploadsPushSync;
import com.operasoft.snowboard.engine.PointOfInterestManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class DeficiencyCustomDialogHandler {

	private Context mContext;
	private Dialog dialog;
	private Spinner spinner;
	private EditText noteEdit, airEdit, groundEdit;
	private HashMap<String, String> spinnerItemsListMap;
	private ArrayList<String> spinnerItemsList;
	
	private LinearLayout imageLayout;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final int ACTION_TAKE_PHOTO_B = 2;
	private String fullPathFilename = "";
	//private List<String> allImages;
	private String image_filename;
	private LinearLayout horizLayout;
	Utils utils;
	private String filenameOnly;
	private View rooView;
	
	
	public DeficiencyCustomDialogHandler(Context mContext, View v) {
		super();
		this.mContext = mContext;
		utils = new Utils(mContext);
		rooView = v;
	}
	
	public void createDialog() {
		dialog = new Dialog(mContext);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_header_menu, (ViewGroup) dialog.findViewById(R.id.root_punch));
		
		// Dialog close button and Text on click listener.
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iv_dhm_cancel);
		TextView closeText = (TextView) layout.findViewById(R.id.tv_dhm_cancel);
		iMg_cancel_dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
						
				try {
					//onCloseDeletePictures();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		closeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					onCloseDeletePictures();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dialog.dismiss();
			}
		});
		
		
		((ImageView) layout.findViewById(R.id.iv_dhm_icon)).setBackgroundResource(R.drawable.deficiency_thumbnail);
		((TextView) layout.findViewById(R.id.tv_dhm_title)).setText(" Deficiency : "+PointOfInterestManager.getInstance().getCurrentRoute().getName().toUpperCase());
		builder.setCustomTitle(layout);
		View layout1 = inflater.inflate(R.layout.dialog_route_deficiency, null);
		((Button) layout1.findViewById(R.id.rd_submit_btn)).setOnClickListener(deficiencyListener);

		((ImageView) layout1.findViewById(R.id.rd_newPicture)).setOnClickListener(takePictureListener);
		imageLayout = (LinearLayout) layout1.findViewById(R.id.rd_imageLayout);	
		horizLayout = (LinearLayout) layout1.findViewById(R.id.rd_horizLayout);
		
		spinnerItemsListMap = new HashMap<String, String>();
		spinnerItemsList = new ArrayList<String>();
		DamageTypeDao damagetypes = new DamageTypeDao();

		List<DamageType> spinnerItems = damagetypes.listAll();
		for (DamageType damage : spinnerItems) {
			spinnerItemsList.add(damage.getName());
			spinnerItemsListMap.put(damage.getName(), damage.getId());
		}

		spinner = (Spinner) layout1.findViewById(R.id.rd_def_type);
		noteEdit = (EditText) layout1.findViewById(R.id.rd_notes);
		airEdit = (EditText) layout1.findViewById(R.id.rd_air_temp);
		groundEdit = (EditText) layout1.findViewById(R.id.rd_ground_temp);
				
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerItemsList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner.setFocusable(true);
				
		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
	}
	
	
	public OnClickListener deficiencyListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_add_deficiency:
				//mPoi = PointOfInterestManager.getInstance().getInsidePolygonPoi();
				createDialog();
				break;
			case R.id.rd_submit_btn:
				if(vaildate()){
					sendDeficiencyDetails();
					dialog.dismiss();
				}
			}
		}
	};
	
	private boolean vaildate(){
		boolean isValid = true;
		String errorMessage ="You must enter a valid ";
		if (airEdit.getText() == null || "".equals(airEdit.getText().toString()) ){
			isValid = false;
			errorMessage += "air temperature";
		}
		if (groundEdit.getText() == null || "".equals(groundEdit.getText().toString()) ){
			isValid = false;
			errorMessage += (errorMessage.equalsIgnoreCase("You must enter a valid "))? "ground temperature": " and ground temperature";
		}
		if (spinner.getSelectedItem() == null || "".equals(spinner.getSelectedItem()) ){
			isValid = false;
			errorMessage += (errorMessage.equalsIgnoreCase("You must enter a valid "))? "deficiency type": " and deficiency type";
		}		

		if(!isValid){
			AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
            builder1.setMessage(errorMessage);
            builder1.setCancelable(true);
            builder1.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                @Override
				public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            /*builder1.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                @Override
				public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });*/

            AlertDialog alert11 = builder1.create();
            alert11.show();
		}
		return isValid;
	}
	
	
	public void sendDeficiencyDetails() {
		Runnable sendDeficiency = new Runnable(){ 
			@Override
			public void run() {
				Deficiency dto = new Deficiency();
		
				dto.setCompanyId(Session.getCompanyId());
				dto.setUserId(Session.getDriver().getId());
				if (Session.route != null) {
					RouteSelectedDao routeSelectedDao = new RouteSelectedDao();
					dto.setRoute_selection_id(routeSelectedDao.getRouteSelectionId(Session.route.getId(), Session.getDriver().getId()));
				}
				dto.setVehicleId(Session.getVehicle().getId());
				dto.setDate(CommonUtils.UtcDateNow());
				dto.setAirT(airEdit.getText().toString());
				dto.setGroundT(groundEdit.getText().toString());
				if( noteEdit.getText() != null && !"".equals(noteEdit.getText().toString()) ){
					dto.setNotes(noteEdit.getText().toString());
				}
				dto.setDeficiencyTypeId(spinnerItemsListMap.get(spinner.getSelectedItem()));
		 
				if (Session.clocation != null) {
					dto.setGpsCoordinates(Session.clocation.getLatitude() + " " + Session.clocation.getLongitude());
				}
				dto.setRouteId(Session.route.getId());
		
				if(image_filename != null && !"".equals(image_filename)){
					Uploads upload = new Uploads();
					upload.setCompanyId(Session.getCompanyId());
					upload.setCreator_id(Session.getDriver().getId());
					upload.setImei_no(utils.getIMEI());
					upload.setFilename(filenameOnly);
					upload.setGps_cordinates(dto.getGpsCoordinates());
					upload.setModel(DeficiencyPicture.MODEL);
			
					UploadsPushSync.getInstance().pushData(mContext, upload);
			
					DeficiencyPicture picture = new DeficiencyPicture();
					picture.setCreatorId(Session.getDriver().getId());
					picture.setFilename(filenameOnly);
					picture.setGps_cordinates(dto.getGpsCoordinates());
					picture.setImeiNo(utils.getIMEI());
					picture.setUploadId(upload.getId());

					dto.add(picture);
				}
		
				DeficiencyPushSync.getInstance().pushData(mContext, dto);
			}
		};
		
		new Thread(sendDeficiency).start();
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
		filenameOnly = JPEG_FILE_PREFIX + utils.getIMEI() +"_"+ timeStamp + JPEG_FILE_SUFFIX;
		fullPathFilename = ImageUtils.IMAGE_FILEPATH+filenameOnly;
		Log.d("Full FilePath", fullPathFilename);		
		Uri uri = Uri.parse(fullPathFilename);
		return uri;
	}
	
	public void addImage(Context context, final String path){
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		//horizLayout.setVisibility(View.VISIBLE);
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
		pictureView.setTag(path);
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
		imageLayout.removeAllViews();
		imageLayout.addView(pictureView);	
		image_filename = path;
	}
	
	
	public void onCloseDeletePictures() throws Exception{
		if(image_filename != null && !"".equals(image_filename))
			deletePicture(image_filename);		
		image_filename = null;
	}
	
	public void deletePicture(String path) throws Exception{
		File deleteFile = new File(path);
		boolean isDeleted = deleteFile.delete();
		
		if(!isDeleted)
			throw new Exception("Couldn't delete the file: " + path);
	}
	
	/*
	 * Listeners for Camera
	 */
	public OnLongClickListener onLongClickDelete = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			if(v instanceof ImageView){	
				final ImageView vv = (ImageView)v;
				Activity host = (Activity)mContext;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(host);
				alertDialogBuilder.setMessage("Do you want to delete this picture?");
				 // set positive button: Yes message
				 alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,int id) {
							ImageView button = ((ImageView)vv);
							try {
								imageLayout.removeAllViews();
								onCloseDeletePictures();	
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
}
