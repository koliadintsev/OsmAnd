package com.operasoft.snowboard.fragment;

import java.util.ArrayList;

import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.ProductsDao;
import com.operasoft.snowboard.database.UnitOfMeasure;
import com.operasoft.snowboard.database.UnitOfMeasureDao;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.worksheet.MaterialListPageView;
import com.operasoft.snowboard.view.worksheet.WorksheetView;

public class WorksheetMaterialFragment extends WorksheetPageFragment implements ListPage {

	private ArrayList<Products> products = new ArrayList<Products>();
	public ArrayList<UnitOfMeasure> uoms = new ArrayList<UnitOfMeasure>();
	MaterialListPageView materialView = null;

	@Override
	protected WorksheetView<Worksheets> getWorkSheetPageView() {
		materialView = new MaterialListPageView(getActivity());
		materialView.products = products;
		materialView.unitOfMeasures = uoms;
		return materialView;
	}

	@Override
	protected void initPage() {
		products.clear();
		uoms.clear();
		//
		products.addAll((new ProductsDao()).listActiveProducts());
		uoms.addAll((new UnitOfMeasureDao()).listAll());
	}

	@Override
	public void addNewRow() {
		materialView.addNewRow();
	}
}
