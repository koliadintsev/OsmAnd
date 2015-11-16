package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class ContactDao extends Dao<Contact> {

	public ContactDao() {
		super("sb_contacts");
	}

	@Override
	public void insert(Contact dto) {
		insertDto(dto);
	}

	@Override
	public void replace(Contact dto) {
		replaceDto(dto);
	}

	@Override
	protected Contact buildDto(Cursor cursor) {
		Contact dto = new Contact();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompany_id(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setCompanyName(cursor.getString(cursor.getColumnIndexOrThrow("CompanyName")));
		dto.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("Notes")));
		dto.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow("FirstName")));
		dto.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("FullName")));
		dto.setLastName(cursor.getString(cursor.getColumnIndexOrThrow("LastName")));
		dto.setTelephone1(cursor.getString(cursor.getColumnIndexOrThrow("Telephone1")));
		dto.setTelephone2(cursor.getString(cursor.getColumnIndexOrThrow("Telephone2")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));

		return dto;
	}

	@Override
	public Contact buildDto(JSONObject json) throws JSONException {
		Contact dto = new Contact();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompany_id(jsonParser.parseString(json, "company_id"));
		dto.setCompanyName(jsonParser.parseString(json, "CompanyName"));
		dto.setNotes(jsonParser.parseString(json, "Notes"));
		dto.setFirstName(jsonParser.parseString(json, "FirstName"));
		dto.setFullName(jsonParser.parseString(json, "FullName"));
		dto.setLastName(jsonParser.parseString(json, "LastName"));
		dto.setTelephone1(jsonParser.parseString(json, "Telephone1"));
		dto.setTelephone2(jsonParser.parseString(json, "Telephone2"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}
