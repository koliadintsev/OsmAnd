package com.operasoft.snowboard.view.worksheet;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.HeaderView;

/**
 * @author dounaka
 *
 */
public class GeneralPageView extends WorksheetView<Worksheets> implements OnClickListener {

	private TextView mEditWeather, mEditJobnotes, mEditContractWork, mEditAccident, mEditTemperature, mEditVisitor, mEditComment;
	private TextView mtxtStartDate;

	public GeneralPageView(Context context) {
		super(context);
	}

	public GeneralPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public int getViewResourceId() {
		return com.operasoft.snowboard.R.layout.worksheet_general;
	}

	@Override
	public void bindControls(Context ctx) {

		mEditWeather = (TextView) findViewById(R.id.editweather);
		mEditJobnotes = (TextView) findViewById(R.id.editjobnotes);
		mEditAccident = (TextView) findViewById(R.id.editaccident);
		mEditContractWork = (TextView) findViewById(R.id.editcontractorwork);
		mEditVisitor = (TextView) findViewById(R.id.editvisitor);
		mEditComment = (TextView) findViewById(R.id.editcomment);
		mEditTemperature = (TextView) findViewById(R.id.edittemperature);

		mEditWeather.setOnClickListener(this);
		mEditJobnotes.setOnClickListener(this);
		mEditAccident.setOnClickListener(this);
		mEditContractWork.setOnClickListener(this);
		mEditVisitor.setOnClickListener(this);
		mEditComment.setOnClickListener(this);
		mEditTemperature.setOnClickListener(this);

		mtxtStartDate = (TextView) findViewById(R.id.editstartdate);
		mtxtStartDate.setOnClickListener(this.dateInputClickListener);

	}

	@Override
	protected void display(final Worksheets worksheet) {
		mEditWeather.setText(worksheet.getWeather());
		mEditJobnotes.setText(worksheet.getNotes());
		mEditContractWork.setText(worksheet.getWorkPerformed());
		mEditAccident.setText(worksheet.getAccidentNotes());
		mEditVisitor.setText(worksheet.getVisitors());
		mEditComment.setText(worksheet.getComments());
		mEditTemperature.setText(worksheet.getTemperature());
		setDate(mtxtStartDate, worksheet.getStartDate());
	}

	@Override
	protected Worksheets update(final Worksheets worksheet) {
		worksheet.setWeather(mEditWeather.getText().toString());
		worksheet.setNotes(mEditJobnotes.getText().toString());
		worksheet.setWorkPerformed(mEditContractWork.getText().toString());
		worksheet.setAccidentNotes(mEditAccident.getText().toString());
		worksheet.setVisitors(mEditVisitor.getText().toString());
		worksheet.setComments(mEditComment.getText().toString());
		worksheet.setTemperature(mEditTemperature.getText().toString());

		worksheet.setStartDate(mtxtStartDate.getText().toString());
		return worksheet;
	}

	AlertDialog dialog = null;

	private String getDialogTitle(View v) {
		if (v == mEditWeather) {
			return "Weather";
		} else if (v == mEditJobnotes) {
			return "Job Notes";
		} else if (v == mEditAccident) {
			return "Accidents / Incidents";
		} else if (v == mEditContractWork) {
			return "Sub Contractor and Work Performed";
		} else if (v == mEditVisitor) {
			return "Visitors";
		} else if (v == mEditComment) {
			return "Comments";
		} else {
			return "Temperature";
		}
	}

	private synchronized void closeDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	final OnClickListener closeDialogListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			closeDialog();
		}
	};

	@Override
	public void onClick(View v) {
		final GeneralPageInputView inputView = new GeneralPageInputView(getContext()) {
			@Override
			public void onConfirm() {
				closeDialog();
			}
		};

		final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setView(inputView);

		HeaderView header = new HeaderView(getContext(), -1, getDialogTitle(v));
		header.setOnClickListener(closeDialogListener);
		builder.setCustomTitle(header);

		dialog = builder.create();
		/// dialog.getWindow().setFlags(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		dialog.setCancelable(true);
		dialog.show();

		inputView.show((TextView) v);

	}
}
