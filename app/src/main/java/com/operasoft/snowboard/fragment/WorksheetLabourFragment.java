package com.operasoft.snowboard.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.operasoft.snowboard.database.Contract;
import com.operasoft.snowboard.database.ContractServices;
import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.ProductsDao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.worksheet.LabourListPageView;
import com.operasoft.snowboard.view.worksheet.WorksheetView;

public class WorksheetLabourFragment extends WorksheetPageFragment implements ListPage {

	private List<User> users = new ArrayList<User>();
	private List<Products> products = new ArrayList<Products>();
	private LabourListPageView labourView = null;

	@Override
	protected WorksheetView<Worksheets> getWorkSheetPageView() {
		labourView = new LabourListPageView(getActivity());
		labourView.users = users;
		labourView.products = products;
		return labourView;
	}

	@Override
	protected void initPage() {
		users.clear();
		products.clear();
		
		// Add the services associated with the contract
		Contract contract = worksheet.getContract();
		if (contract != null) {
			List<ContractServices> services = contract.listServices();
			for (ContractServices service : services) {
				Products product = service.getProduct();
				if ( (product != null) && product.isService() ) {
					products.add(product);
				} else {
					Log.w("Worksheet", "Product " + service.getProductId() + " not found in local DB");
				}
			}
		} else {
			Log.w("Worksheet", "No contract found for worksheet... Adding all services for company");
			products.addAll((new ProductsDao()).listActiveServices());
		}
		// Sort the services based on their names
		Collections.sort(products);
		
		users.addAll((new UsersDao()).getTopActiveEmployees(worksheet.getContractId(), worksheet.getStartDate()));
	}

	@Override
	public void addNewRow() {
		labourView.addNewRow();
	}
}
