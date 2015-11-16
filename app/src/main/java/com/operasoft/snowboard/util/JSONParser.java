package com.operasoft.snowboard.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {
	static final private String NULL = "null";

	static private SimpleDateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static private SimpleDateFormat longFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
	static private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
	static private SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("HH:mm:ss");

	/**
	 * This method is used to parse a date and format it in the proper format
	 */
	public String parseDate(JSONObject jsonObject, String field) {
		String value = jsonObject.optString(field);

		if ((value == null) || (value.equals(NULL))) {
			return null;
		}

		String input = value.trim();

		Date date = null;
		try {
			date = longFormat.parse(input);
		} catch (ParseException e) {
			try {
				date = shortFormat.parse(input);
			} catch (ParseException e1) {
				try {
					date = dateOnlyFormat.parse(input);
				} catch (ParseException e2) {
					Log.e("JsonDtoParser", "Failed to parse date " + input + " for field " + field);
					return input;
				}
			}
		}

		return shortFormat.format(date);
	}

	/**
	 * Parses a JSON field as a String
	 */
	public String parseString(JSONObject jsonObject, String field) {
		String value = jsonObject.optString(field);

		if ((value == null) || (value.equals(NULL))) {
			return null;
		}

		return value;
	}

	public int parseInt(JSONObject jsonObject, String field) throws JSONException {
		if (!jsonObject.isNull(field)) {
			String value = jsonObject.getString(field);
			if (value.equals("true")) {
				return 1;
			} else if (value.equals("false")) {
				return 0;
			}
			return jsonObject.getInt(field);
		}
		
		return -1;
	}

	public int parseInt(JSONObject jsonObject, String field, int fallback) {
		return jsonObject.optInt(field, fallback);
	}
	
	public float parseFloat(JSONObject jsonObject, String field) throws JSONException {
		if (!jsonObject.isNull(field)) {
			return (float) jsonObject.getDouble(field);
		}
		
		return -1f;
	}

	public float parseFloat(JSONObject jsonObject, String field, float fallback) throws JSONException {
		return (float) jsonObject.optDouble(field, fallback);
	}

	public double parseDouble(JSONObject jsonObject, String field) throws JSONException {
		if (!jsonObject.isNull(field)) {
			return jsonObject.getDouble(field);
		}
		
		return -1d;
	}

	public double parseDouble(JSONObject jsonObject, String field, double fallback) throws JSONException {
		return jsonObject.optDouble(field, fallback);
	}
}
