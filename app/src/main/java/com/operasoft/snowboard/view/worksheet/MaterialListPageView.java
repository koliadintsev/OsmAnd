package com.operasoft.snowboard.view.worksheet;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.UnitOfMeasure;
import com.operasoft.snowboard.database.WorksheetMaterial;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.worksheet.row.MaterialRowView;
import com.operasoft.snowboard.view.worksheet.row.RowPageView;

/**
 * @author dounaka
 *
 */
public class MaterialListPageView extends ListRowPageView<Worksheets, WorksheetMaterial> {

	public MaterialListPageView(Context ctx) {
		super(ctx);
	}

	private LinearLayout mLstMaterials;
	public List<Products> products;
	public List<UnitOfMeasure> unitOfMeasures;

	@Override
	public int getViewResourceId() {
		return R.layout.worksheet_material;
	}

	@Override
	public void bindListControls(Context ctx) {
		mLstMaterials = (LinearLayout) findViewById(R.id.lstworkmaterials);
	}

	@Override
	protected RowPageView<WorksheetMaterial> getNewRowView() {
		MaterialRowView materialView = new MaterialRowView(getContext());
		materialView.listener = this;
		if (products != null)
			materialView.initProducts(products);
		if (unitOfMeasures != null)
			materialView.initUnitOfMeasures(unitOfMeasures);
		return materialView;
	}

	@Override
	protected List<WorksheetMaterial> getRowDtos() {
		return this.dto.getWorksheetMaterialList();
	}

	@Override
	protected WorksheetMaterial getNewRowDto() {
		return new WorksheetMaterial();
	}

	@Override
	protected ViewGroup getContainerView() {
		return mLstMaterials;
	}

}
