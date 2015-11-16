package com.operasoft.snowboard.view.worksheet.row;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.EquipmentTypes;
import com.operasoft.snowboard.database.EquipmentTypesDao;
import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.VehiclesDao;
import com.operasoft.snowboard.database.WorksheetEquipment;

/**
 * @author dounaka
 */
public class EquipmentRowView extends RowPageView<WorksheetEquipment> {

	private DtoSpinner<Vehicle> mSpinnerVehicle;
	private DtoSpinner<EquipmentTypes> mSpinnerEquipment;
	private DtoSpinner<Products> mSpinnerProduct;
	private DtoSpinner<User> mSpinnerEmployee;
	Spinner mSpinnerMinutes;
	private ImageView imgdelete;
	private EditText mEditHours;
	private TextView mTxtDate;
	private boolean disableEquipmentSpinner = false;
	private boolean disableVehicleSpinner = false;
	
	public EquipmentRowView(Context context) {
		super(context);
	}

	@Override
	public int getViewResourceId() {
		return R.layout.worksheet_equipment_row;
	}

	public void initUsers(List<User> employees) {
		mSpinnerEmployee.setDtoAdapter(new UserSpinnerDto(getContext(), employees));
	}

	public void initEquipments(List<EquipmentTypes> equipments) {
		mSpinnerEquipment.setDtoAdapter(new DtoAdapter<EquipmentTypes>(getContext(), equipments) {
			@Override
			public String getTitle(EquipmentTypes product) {
				return product.getEquipmentName();
			}
		});
	}

	public void initProducts(List<Products> products) {
		mSpinnerProduct.setDtoAdapter(new DtoAdapter<Products>(getContext(), products) {
			@Override
			public String getTitle(Products product) {
				return product.getName();
			}
		});
	}

	public void initVehicles(List<Vehicle> vehicles) {
		mSpinnerVehicle.setDtoAdapter(new DtoAdapter<Vehicle>(getContext(), vehicles) {
			@Override
			public String getTitle(Vehicle vehicle) {
				return vehicle.getName();
			}
		});
	}

	@Override
	public void bindRowControls(Context ctx) {
		mSpinnerEmployee = (DtoSpinner<User>) findViewById(R.id.spinneremployee);
		mSpinnerEquipment = (DtoSpinner<EquipmentTypes>) findViewById(R.id.spinnerequipment);
		mSpinnerProduct = (DtoSpinner<Products>) findViewById(R.id.spinnertask);
		mSpinnerVehicle = (DtoSpinner<Vehicle>) findViewById(R.id.spinnervehicle);
		mSpinnerMinutes = (Spinner) findViewById(R.id.spinnerminutes);
		mTxtDate = (TextView) findViewById(R.id.editdate);
		mEditHours = (EditText) findViewById(R.id.edithours);
		imgdelete = (ImageView) findViewById(R.id.imgdelete);
		mSpinnerMinutes.setAdapter(new MinutesAdapter(getContext()));
		mTxtDate.setOnClickListener(this.dateInputClickListener);
		mEditHours.setOnFocusChangeListener(selectTextListener);
		mEditHours.setOnTouchListener(selectTextListener);
		mSpinnerEquipment.setOnItemSelectedListener(new EquipmentTypeSelectedListener());
		mSpinnerVehicle.setOnItemSelectedListener(new VehicleSelectedListener());
	}

	@Override
	protected void display(WorksheetEquipment equipment) {
		disableEquipmentSpinner = true;
		disableVehicleSpinner = true;
		
		mSpinnerEmployee.setValueById(equipment.getUserId());
		mSpinnerEquipment.setValueById(equipment.getEquipmentId());
		mSpinnerProduct.setValueById(equipment.getProductId());
		mSpinnerVehicle.setValueById(equipment.getVehicleId());
		setDate(mTxtDate, equipment.getEquipmentDate());
		mEditHours.setText("" + (int) equipment.getHours());
		setMinutes(mSpinnerMinutes, equipment.getHours());
		if (!equipment.isNew()) {
			// Disable the delete button
			imgdelete.setVisibility(INVISIBLE);
		} else {
			// Enable the delete button
			imgdelete.setVisibility(VISIBLE);			
		}
	}

	@Override
	protected WorksheetEquipment update(WorksheetEquipment equipment) {		
		equipment.setEquipmentDate(mTxtDate.getText().toString());
		equipment.setUserId(mSpinnerEmployee.getValueId());
		equipment.setEquipmentId(mSpinnerEquipment.getValueId());
		equipment.setProductId(mSpinnerProduct.getValueId());
		equipment.setVehicleId(mSpinnerVehicle.getValueId());
		String nbHours = mEditHours.getText().toString();
		equipment.setHours(Integer.parseInt(nbHours) + getMinutes(mSpinnerMinutes));
		return equipment;
	}

	@Override
	protected View getDeleteRowView() {
		return imgdelete;
	}

	private class EquipmentTypeSelectedListener implements OnItemSelectedListener {

		private VehiclesDao dao = new VehiclesDao();
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (!disableEquipmentSpinner) {
				disableVehicleSpinner = true;
				
				String equipmentTypeId = mSpinnerEquipment.getValueId();
				if (equipmentTypeId != null) {
					List<Vehicle> vehicles = dao.findByEquipmentType(equipmentTypeId);
					initVehicles(vehicles);
				} else {
					initVehicles(dao.listSorted());
				}
			}
			disableEquipmentSpinner = false;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			initVehicles(dao.listSorted());
		}
		
	}
	
	private class VehicleSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (!disableVehicleSpinner) {
				Vehicle vehicle = mSpinnerVehicle.getSelectedDto();
				if (vehicle != null) {
					String selectedId = mSpinnerEquipment.getValueId();
					String newId = vehicle.getEquipmentTypeId();
					
					boolean update = false;
					
					if (selectedId == null) {
						if (newId != null) {
							update = true;
						}
					} else if (newId == null) {
						update = true;
					} else if (!selectedId.equalsIgnoreCase(vehicle.getEquipmentTypeId()) ) {
						update = true;
					}
					
					if ( update ) {
						disableEquipmentSpinner = true;				
						mSpinnerEquipment.setValueById(vehicle.getEquipmentTypeId());
					}
				}
			}
			disableVehicleSpinner = false;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}		
	}
}
