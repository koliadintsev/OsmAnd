package com.operasoft.snowboard.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.database.Cursor;

/**
 * WORK IN PROGRESS
 * @author Christian
 *
 */
public class DtoBuilder {

	private Map<Class, List<DtoFieldDetails>> map = new HashMap<Class, List<DtoFieldDetails>>();
	
	
	public void fillDto(Dto dto, Cursor cursor) throws Exception {
		List<DtoFieldDetails> fields = map.get(dto.getClass());
		if (fields == null) {
			fields = dto.getDtoFieldDetails();
			map.put(dto.getClass(), fields);
		}
		
		for (DtoFieldDetails field: fields) {
			
		}
	}

	public void fillDto(Dto dto, JSONObject json) throws Exception {
		List<DtoFieldDetails> fields = map.get(dto.getClass());
		if (fields == null) {
			fields = dto.getDtoFieldDetails();
			map.put(dto.getClass(), fields);
		}
		
		for (DtoFieldDetails field: fields) {
			
		}
	}
}
