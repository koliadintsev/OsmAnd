package com.operasoft.snowboard.view.worksheet;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.view.AppView;

/**
 * @author dounaka
 * Window used to input text in a larger window
 */
public abstract class GeneralPageInputView extends AppView {
	private Button mBtnConfirm;
	private TextView mTxtToEdit;
	private EditText mInputTxt;

	public GeneralPageInputView(Context ctx) {
		super(ctx);
	}

	@Override
	protected int getViewResId() {
		return R.layout.worksheet_general_input;
	}

	@Override
	protected void bindControls(Context ctx) {
		mBtnConfirm = (Button) findViewById(R.id.btnconfirm);
		mInputTxt = (EditText) findViewById(R.id.inputtxt);
		mBtnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTxtToEdit.setText(mInputTxt.getText());
				onConfirm();
			}
		});
	}

	public void show(TextView textView) {
		mTxtToEdit = textView;
		final CharSequence txt = mTxtToEdit.getText();
		mInputTxt.setText(txt);

		if (txt != null || txt.length() > 0)
			mInputTxt.post(new Runnable() {
				@Override
				public void run() {
					mInputTxt.setSelection(txt.length());
				}
			});
		mInputTxt.requestFocus();
	}

	public abstract void onConfirm();

}
