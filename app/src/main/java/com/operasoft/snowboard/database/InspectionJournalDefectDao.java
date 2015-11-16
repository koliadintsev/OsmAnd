package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class InspectionJournalDefectDao extends Dao<InspectionJournalDefect> {

	public InspectionJournalDefectDao() {
		super("sb_inspection_journal_defects");
	}
	
	@Override
	public void insert(InspectionJournalDefect dto) {
		insertDto(dto);
	}

	@Override
	public void replace(InspectionJournalDefect dto) {
		replaceDto(dto);
	}

	@Override
	protected InspectionJournalDefect buildDto(Cursor cursor) {
		InspectionJournalDefect dto = new InspectionJournalDefect();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setJournalId(cursor.getString(cursor.getColumnIndexOrThrow("inspection_journal_id")));
		dto.setItemId(cursor.getString(cursor.getColumnIndexOrThrow("inspection_checklist_item_id")));
		dto.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public InspectionJournalDefect buildDto(JSONObject json) throws JSONException {
		InspectionJournalDefect dto = new InspectionJournalDefect();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setJournalId(jsonParser.parseString(json, "inspection_journal_id"));
		dto.setItemId(jsonParser.parseString(json, "inspection_checklist_item_id"));
		dto.setNotes(jsonParser.parseString(json, "notes"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	public List<InspectionJournalDefect> listAllForJournal(String journalId) {
		List<InspectionJournalDefect> list = new ArrayList<InspectionJournalDefect>();

		String sql = "SELECT * FROM " + table + " WHERE inspection_journal_id = '" + journalId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				InspectionJournalDefect dto = buildDto(cursor);
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

}
