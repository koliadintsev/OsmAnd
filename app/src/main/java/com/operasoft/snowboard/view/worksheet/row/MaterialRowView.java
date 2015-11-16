package com.operasoft.snowboard.view.worksheet.row;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.UnitOfMeasure;
import com.operasoft.snowboard.database.WorksheetMaterial;

/**
 * @author dounaka
 * TODO refactoring with LabourRowView
 */
public class MaterialRowView extends RowPageView<WorksheetMaterial> implements OnItemSelectedListener {

	private DtoSpinner<Products> mSpinnerProducts;
	private ImageView imgdelete;
	private EditText mEditQuantity;
	private TextView mTxtDate, mTxtUnitOfMeasure;

	private final HashMap<String, String> unitOfMeasures = new HashMap<String, String>();

	public MaterialRowView(Context context) {
		super(context);
	}

	@Override
	public int getViewResourceId() {
		return R.layout.worksheet_material_row;
	}

	public void initUnitOfMeasures(List<UnitOfMeasure> uoms) {
		unitOfMeasures.clear();
		for (UnitOfMeasure uom : uoms) {
			unitOfMeasures.put(uom.getId(), uom.getName());
		}
	}

	public void initProducts(List<Products> products) {
		mSpinnerProducts.setDtoAdapter(new DtoAdapter<Products>(getContext(), products) {
			@Override
			public String getTitle(Products product) {
				return product.getName();
			}
		});
	}

	@Override
	public void bindRowControls(Context ctx) {
		mSpinnerProducts = (DtoSpinner<Products>) findViewById(R.id.spinnermaterial);
		mSpinnerProducts.setOnItemSelectedListener(this);
		mTxtUnitOfMeasure = (TextView) findViewById(R.id.txtunitofmeasure);
		mTxtDate = (TextView) findViewById(R.id.editdate);
		mTxtDate.setOnClickListener(this.dateInputClickListener);
		mEditQuantity = (EditText) findViewById(R.id.editquantity);
		imgdelete = (ImageView) findViewById(R.id.imgdelete);
		mEditQuantity.setOnFocusChangeListener(selectTextListener);
		mEditQuantity.setOnTouchListener(selectTextListener);

	}

	@Override
	protected void display(WorksheetMaterial material) {
		mSpinnerProducts.setValueById(material.getProductId());
		if (mSpinnerProducts.getSelectedView() != null) {
			Products product = (Products) mSpinnerProducts.getSelectedView().getTag();
			try {
				mTxtUnitOfMeasure.setText(unitOfMeasures.get(product.getUom_id()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setDate(mTxtDate, material.getMaterialDate());
		mEditQuantity.setText("" + material.getQuantity());
	}

	@Override
	protected WorksheetMaterial update(WorksheetMaterial material) {
		material.setMaterialDate(mTxtDate.getText().toString());
		material.setProductId(mSpinnerProducts.getValueId());
		Products product = mSpinnerProducts.getSelectedDto();
		if (product != null) {
			material.setUnitOfMesureId(product.getUom_id());
		}
		final String quantity = mEditQuantity.getText().toString();
		material.setQuantity(Float.parseFloat(quantity));

		return material;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
		if (view == null)
			return;
		Object tag = view.getTag();
		if (tag != null && tag instanceof Products) {
			Products product = (Products) view.getTag();
			try {
				mTxtUnitOfMeasure.setText(unitOfMeasures.get(product.getUom_id()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	protected View getDeleteRowView() {
		return imgdelete;
	}

}
