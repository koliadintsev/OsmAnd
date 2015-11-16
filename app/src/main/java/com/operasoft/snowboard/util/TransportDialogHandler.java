package com.operasoft.snowboard.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.util.Session.SessionType;

public class TransportDialogHandler {

	private Context context;
	private View view;
	private Dialog dialog;
	private LayoutInflater inflater;
	private View layout1;
	AlertDialog.Builder builder;

	public TransportDialogHandler(Context context, View view) {
		this.context = context;
		this.view = view;
	}

	private void createDialog() {

		if (dialog != null)
			if (dialog.isShowing())
				dialog.dismiss();

		dialog = new Dialog(context);
		builder = new AlertDialog.Builder(context);

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_header, (ViewGroup) view.findViewById(R.id.root));

		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.img_dh_cancel);
		ImageView iMg_title_dialog = (ImageView) layout.findViewById(R.id.img_dh_title);
		TextView closeText = (TextView) layout.findViewById(R.id.tv_dh_cancel);
		TextView titleText = (TextView) layout.findViewById(R.id.tv_dh_title);

		iMg_title_dialog.setImageResource(R.drawable.map_action_transport);
		if (Session.getType() == SessionType.SITE_SESSION)
			titleText.setText("Close Transport Activity");
		else
			titleText.setText("Create Transport Activity");

		// Configure the "close" button
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
		if (Session.getType() == SessionType.SITE_SESSION)
			layout1 = inflater.inflate(R.layout.dialog_close_transport, null);
		else
			layout1 = inflater.inflate(R.layout.dialog_create_transport, null);

		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
	}

	public OnClickListener makeTransportListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.button_transport:
				createDialog();
				break;
			default:
				break;
			}
		}
	};

}
