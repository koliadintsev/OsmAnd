package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class ProductsDao extends Dao<Products> {

	public ProductsDao() {
		super("sb_products");
		this.orderByFields = " name, product_code ";
	}

	private static final String TAG = "ProductsDao";

	public List<Products> listProducts(String... productIds) {

		final String apos = "'";
		final String comma = ",";

		final List<Products> products = new ArrayList<Products>();
		if (productIds == null || productIds.length == 0)
			return products;
		Cursor cursor = null;
		Products dto = null;
		try {
			final StringBuilder sql = new StringBuilder("SELECT * FROM " + table + " where id in (");
			sql.append(apos);
			sql.append(productIds[0]);
			sql.append(apos);
			for (int i = 1; i < productIds.length; i++) {
				sql.append(comma);
				sql.append(apos);
				sql.append(productIds[i]);
				sql.append(apos);
			}
			sql.append(")");
			cursor = DataBaseHelper.getDataBase().rawQuery(sql.toString(), null);
			while (cursor.moveToNext()) {
				dto = buildDto(cursor);
				if (dto != null) {
					products.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "ERROR " + e.getMessage(), e);
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return products;
	}

	/**
	 * return the list of valid Products for dto
	 * 
	 * @return
	 */
	public List<Products> listProducts() {
		List<Products> list = new ArrayList<Products>();

		String sql = "SELECT * FROM " + table + " where foreman_daily_worksheet = 1 and deleted <= 0 ORDER BY name COLLATE NOCASE";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		while (cursor.moveToNext()) {
			try {
				Products dto = buildDto(cursor);
				if (dto != null) {
					list.add(dto);
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();

		return list;
	}

	@Override
	public void insert(Products dto) {
		insertDto(dto);
	}

	@Override
	public void replace(Products dto) {
		replaceDto(dto);
	}

	@Override
	protected Products buildDto(Cursor cursor) {
		Products dto = new Products();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setCreator_id(cursor.getString(cursor.getColumnIndexOrThrow("creator_id")));
		dto.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")));
		dto.setDeleted_date(cursor.getString(cursor.getColumnIndexOrThrow("deleted_date")));
		dto.setDescription_long(cursor.getString(cursor.getColumnIndexOrThrow("description_long")));
		dto.setDescription_short(cursor.getString(cursor.getColumnIndexOrThrow("description_short")));
		dto.setForeman_daily_worksheet(cursor.getInt(cursor.getColumnIndexOrThrow("foreman_daily_worksheet")));
		dto.setImport_id(cursor.getString(cursor.getColumnIndexOrThrow("import_id")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setNon_taxable(cursor.getInt(cursor.getColumnIndexOrThrow("non_taxable")));
		dto.setProduct_code(cursor.getString(cursor.getColumnIndexOrThrow("product_code")));
		dto.setProduct_type(cursor.getString(cursor.getColumnIndexOrThrow("product_type")));
		dto.setRoute_group_id(cursor.getString(cursor.getColumnIndexOrThrow("route_group_id")));
		dto.setStatus_code_id(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setUom_id(cursor.getString(cursor.getColumnIndexOrThrow("uom_id")));
		dto.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public Products buildDto(JSONObject json) throws JSONException {
		Products dto = new Products();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setCreator_id(jsonParser.parseString(json, "creator_id"));
		dto.setDeleted(jsonParser.parseInt(json, "deleted"));
		dto.setDeleted_date(jsonParser.parseString(json, "deleted_date"));
		dto.setDescription_long(jsonParser.parseString(json, "description_long"));
		dto.setDescription_short(jsonParser.parseString(json, "description_short"));
		dto.setForeman_daily_worksheet(jsonParser.parseInt(json, "foreman_daily_worksheet"));
		dto.setImport_id(jsonParser.parseString(json, "import_id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setNon_taxable(jsonParser.parseInt(json, "non_taxable"));
		dto.setProduct_code(jsonParser.parseString(json, "product_code"));
		dto.setProduct_type(jsonParser.parseString(json, "product_type"));
		dto.setRoute_group_id(jsonParser.parseString(json, "route_group_id"));
		dto.setStatus_code_id(jsonParser.parseString(json, "status_code_id"));
		dto.setUom_id(jsonParser.parseString(json, "uom_id"));
		dto.setType(jsonParser.parseString(json, "type"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	public static void sortBySeasonEvent(final List<ContractServices> contractServices) {
		final String[] productids = new String[contractServices.size()];
		int index = 0;
		for (ContractServices service : contractServices) {
			productids[index++] = service.getProductId();
		}
		final HashMap<String, Products> productsById = new HashMap<String, Products>();
		for (Products product : (new ProductsDao()).listProducts(productids)) {
			productsById.put(product.getId(), product);
		}
		Comparator<ContractServices> seasonEventComparator = new Comparator<ContractServices>() {
			@Override
			public int compare(ContractServices lhs, ContractServices rhs) {

				if (lhs.getProductId() == null || lhs.getProductName() == null)
					return 1;

				if (rhs.getProductId() == null || rhs.getProductName() == null)
					return -1;

				if (lhs.getProductId() == null && rhs.getProductId() == null)
					return 0;

				if (lhs.getProductName() == null && rhs.getProductName() == null)
					return 0;

				if (lhs.getProductId().equals(rhs.getProductId())) {
					return lhs.getProductName().compareTo(rhs.getProductName());
				}
				Products lproduct = productsById.get(lhs.getProductId());
				if (lproduct == null) {
					return 1;
				}

				Products rproduct = productsById.get(rhs.getProductId());
				if (rproduct == null) {
					return -1;
				}

				if (lproduct.isSeason() && rproduct.isEvent())
					return -1;
				if (rproduct.isSeason() && lproduct.isEvent())
					return 1;
				return lhs.getProductName().compareTo(rhs.getProductName());
			}
		};
		Collections.sort(contractServices, seasonEventComparator);
	}

	/**
	 * Returns the list of active products from the database.
	 */
	public List<Products> listActiveProducts() {
		String sql = "SELECT * FROM " + table + " WHERE status_code_id = '" + Products.ACTIVE_STATUS_CODE + "' AND type = '" + Products.PRODUCT_TYPE + "' ORDER BY name COLLATE NOCASE";
		return listDtos(sql);
	}
		
	/**
	 * Returns the list of active services from the database.
	 */
	public List<Products> listActiveServices() {
		String sql = "SELECT * FROM " + table + " WHERE status_code_id = '" + Products.ACTIVE_STATUS_CODE + "' AND type = '" + Products.SERVICE_TYPE + "' ORDER BY name COLLATE NOCASE";
		return listDtos(sql);
	}

}
