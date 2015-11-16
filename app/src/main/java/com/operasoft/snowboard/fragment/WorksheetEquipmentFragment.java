package com.operasoft.snowboard.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.operasoft.snowboard.database.Contract;
import com.operasoft.snowboard.database.ContractServices;
import com.operasoft.snowboard.database.EquipmentTypes;
import com.operasoft.snowboard.database.EquipmentTypesDao;
import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.ProductsDao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.VehiclesDao;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.view.worksheet.EquipmentListPageView;
import com.operasoft.snowboard.view.worksheet.WorksheetView;

public class WorksheetEquipmentFragment extends WorksheetPageFragment implements ListPage {

	private final ArrayList<User> users = new ArrayList<User>();
	private final ArrayList<EquipmentTypes> equipments = new ArrayList<EquipmentTypes>();
	private final ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
	private List<Products> products = new ArrayList<Products>();
	EquipmentListPageView equipmentView = null;

	@Override
	protected WorksheetView<Worksheets> getWorkSheetPageView() {
		equipmentView = new EquipmentListPageView(getActivity());
		equipmentView.users = users;
		equipmentView.equipments = equipments;
		equipmentView.vehicles = vehicles;
		equipmentView.products = products;
		return equipmentView;
	}

	@Override
	protected void initPage() {
		users.clear();
		equipments.clear();
		vehicles.clear();
		//
		EquipmentTypes empty = new EquipmentTypes();
		empty.setId(null);
		empty.setEquipmentName("- Select -");
		equipments.add(empty);
		users.addAll((new UsersDao()).getTopActiveEmployees(worksheet.getContractId(), worksheet.getStartDate()));
		equipments.addAll((new EquipmentTypesDao()).listAll());
		vehicles.addAll(new VehiclesDao().listSorted());

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
	}

	@Override
	public void addNewRow() {
		equipmentView.addNewRow();
	}
}
