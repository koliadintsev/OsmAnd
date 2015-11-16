package com.operasoft.snowboard.util;

import com.operasoft.snowboard.R;
import net.osmand.plus.activities.MapActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.operasoft.snowboard.Sw_LoginScreenActivity;

public class OfflineDialog extends AsyncTask<Void, Void, Void> {

	private Context mContext;
	MapActivity mMapActivity;
	SharedPreferences mSP;
	boolean dialogOnceCreated = true;

	public OfflineDialog(Context context, MapActivity mapActivity) {
		this.mContext = context;
		this.mMapActivity = mapActivity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		mMapActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (dialogOnceCreated) {
					alertMsg("Offline Map Error");
					dialogOnceCreated = false;
				}
			}
		});
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	}

	protected void alertMsg(String msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mMapActivity);
		alertDialogBuilder.setTitle(msg);
		alertDialogBuilder.setMessage(mContext.getResources().getString(R.string.offline_dialog_message)).setCancelable(true)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						// mMapActivity.finish();
						SharedPreferences mSP = PreferenceManager.getDefaultSharedPreferences(mContext);
						SharedPreferences.Editor prefEditor = mSP.edit();
						prefEditor.putString(Config.USER_PIN_KEY, "");
						prefEditor.commit();
						Session.route = null;
						Session.FirstLogin = false;
						Session.routeSequences = null;
						Intent intent = new Intent(mContext, Sw_LoginScreenActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(intent);
						mMapActivity.finish();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(false);
		alertDialog.show();
	}
}