package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class ContractServicesDao extends Dao<ContractServices> {

	public ContractServicesDao() {
		super("sb_contract_services");
	}

	@Override
	public void insert(ContractServices dto) {
		insertDto(dto);
	}

	@Override
	public void replace(ContractServices dto) {
		replaceDto(dto);
	}

	/**
	 * Returns the list of all services available for a given contract.
	 */
	public List<ContractServices> listAllForContractId(String contractId) {
		String sql = "SELECT * FROM " + table + " WHERE contract_id = '" + contractId + "'";
		return listDtos(sql);
	}

	public void clearAllContractLines(String contractId) {
		String sql = "delete FROM " + table + " WHERE contract_id = '" + contractId + "'";
		DataBaseHelper.getDataBase().execSQL(sql);

	}

	@Override
	protected ContractServices buildDto(Cursor cursor) {
		ContractServices service = new ContractServices();
		service.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		service.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		service.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("product_id")));
		service.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
		service.setUnitOfMeasure(cursor.getString(cursor.getColumnIndexOrThrow("uom_name")));
		service.setQuantity(cursor.getFloat(cursor.getColumnIndexOrThrow("product_qty")));
		service.setRouteId(cursor.getString(cursor.getColumnIndexOrThrow("route_id")));
		service.setContractId(cursor.getString(cursor.getColumnIndexOrThrow("contract_id")));
		service.setDescription(cursor.getString((cursor.getColumnIndexOrThrow("description"))));
		service.setTriggerLevel(cursor.getInt((cursor.getColumnIndexOrThrow("trigger_level"))));
		service.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		service.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		service.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));

		return service;
	}

	@Override
	public ContractServices buildDto(JSONObject json) throws JSONException {
		ContractServices dto = new ContractServices();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setProductId(jsonParser.parseString(json, "product_id"));
		dto.setProductName(jsonParser.parseString(json, "product_name"));
		dto.setUnitOfMeasure(jsonParser.parseString(json, "uom_name"));
		dto.setQuantity(jsonParser.parseFloat(json, "product_qty"));
		dto.setRouteId(jsonParser.parseString(json, "route_id"));
		dto.setContractId(jsonParser.parseString(json, "contract_id"));
		dto.setDescription(jsonParser.parseString(json, "description"));
		dto.setTriggerLevel(jsonParser.parseInt(json, "trigger_level"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}
