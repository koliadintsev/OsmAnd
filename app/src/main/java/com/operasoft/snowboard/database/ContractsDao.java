package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class ContractsDao extends Dao<Contract> {

	public ContractsDao() {
		super("sb_contracts");
	}

	public String getServiceLocationIdForContract(String contractId) {
		String sql = "SELECT service_location_id FROM " + table + " WHERE id = '" + contractId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			String value = cursor.getString(0);
			cursor.close();
			return value;
		}

		return null;
	}

	/**
	 * Retrieves the contract associated with a service location ID for a given season
	 * TODO Not used anymore
	 */
	private Contract getContractForServiceLocationId(String serviceLocationId, String seasonId) {
		String sql = "SELECT * FROM " + table + " WHERE service_location_id = '" + serviceLocationId + "' AND season_id = '" + seasonId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			Contract dto = buildDto(cursor);
			cursor.close();
			return dto;
		}

		return null;
	}

	/**
	 * Retrieves the contract associated with a service location ID for a given season
	 */
	public List<Contract> getActiveContractForServiceLocation(String serviceLocationId, String seasonId, String contractType) {
		String sql = null;
		if (contractType != null) {
			sql = "SELECT * FROM " + table
					// filter on service location and season
							+ " contract WHERE contract.service_location_id = '" + serviceLocationId + "' AND contract.season_id = '" + seasonId
							// filter on status code to keep only activate contract
							+ "' and contract.id in (select c.id from sb_divisions d, sb_contracts c where contract.id=c.id and d.id=c.division_id "
							// filter contract type 
							+ " and d.type='" + contractType + "' "
							// activate contract
							+ "  and c.status_code_id in (SELECT id FROM sb_status_codes where model='Contract' and name='Activate')) "
							// sorting...
							+ " order by contract.contract_number, contract.job_number ";			
		} else {
			sql = "SELECT * FROM " + table
					// filter on service location and season
							+ " contract WHERE contract.service_location_id = '" + serviceLocationId + "' AND contract.season_id = '" + seasonId + "'"
							// filter on status code to keep only activate contract
							+ "  and contract.status_code_id in (SELECT id FROM sb_status_codes where model='Contract' and name='Activate') "
							// sorting...
							+ " order by contract.contract_number, contract.job_number ";			
		}
		
		return listDtos(sql);
	}

	@Override
	public void insert(Contract dto) {
		insertDto(dto);
	}

	@Override
	public void replace(Contract dto) {
		replaceDto(dto);
	}

	@Override
	protected Contract buildDto(Cursor cursor) {
		Contract contract = new Contract();

		contract.setCompany_id(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		contract.setService_location_id(cursor.getString(cursor.getColumnIndexOrThrow("service_location_id")));
		contract.setDate_to_date(cursor.getString(cursor.getColumnIndexOrThrow("date_to")));
		contract.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		contract.setStatus_code_id(cursor.getString((cursor.getColumnIndexOrThrow("status_code_id"))));
		contract.setSeason_id(cursor.getString((cursor.getColumnIndexOrThrow("season_id"))));
		contract.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		contract.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
		contract.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
		contract.setContract_number(cursor.getString(cursor.getColumnIndexOrThrow("contract_number")));
		contract.setContract_name(cursor.getString(cursor.getColumnIndexOrThrow("contract_name")));
		contract.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		contract.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));
		contract.setJobNumber(cursor.getString(cursor.getColumnIndexOrThrow("job_number")));
		contract.setDivisionId(cursor.getString(cursor.getColumnIndexOrThrow("division_id")));

		return contract;
	}

	@Override
	public Contract buildDto(JSONObject json) throws JSONException {
		Contract dto = new Contract();

		dto.setCompany_id(jsonParser.parseString(json, "company_id"));
		dto.setService_location_id(jsonParser.parseString(json, "service_location_id"));
		dto.setDate_to_date(jsonParser.parseString(json, "date_to"));
		dto.setStatus_code_id(jsonParser.parseString(json, "status_code_id"));
		dto.setSeason_id(jsonParser.parseString(json, "season_id"));
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setLatitude(jsonParser.parseDouble(json, "latitude", 0));
		dto.setLongitude(jsonParser.parseDouble(json, "longitude", 0));
		dto.setContract_number(jsonParser.parseString(json, "contract_number"));
		dto.setContract_name(jsonParser.parseString(json, "contract_name"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		dto.setJobNumber(jsonParser.parseString(json, "job_number"));
		dto.setDivisionId(jsonParser.parseString(json, "division_id"));

		return dto;
	}

	/**
	 * Retrieves All contract associated with a service location ID
	 */
	public ArrayList<Contract> getAllContractForServiceLocationId(String serviceLocationId) {
		String sql = "SELECT * FROM " + table + " WHERE service_location_id = '" + serviceLocationId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		ArrayList<Contract> contractList = new ArrayList<Contract>();
		while (cursor.moveToNext()) {
			Contract dto = buildDto(cursor);
			contractList.add(dto);
		}
		cursor.close();

		return contractList;
	}

	public Contract getContractforDivision(String id) {
		Contract con = null;
		String sql = "SELECT * FROM " + table + " WHERE division_id = '" + id + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			Contract dto = buildDto(cursor);
			con = dto;
		}
		cursor.close();

		return con;
	}
}
