package com.operasoft.snowboard.connection;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class NMEAParser {
	/**
	 * Type not supported.
	 */
	public static final int TYPE_NA = 0;

	/**
	 * Type GPRMC.
	 */
	public static final int TYPE_GPRMC = 1;

	public static final int TYPE_GPGGA = 2;

	public static final int TYPE_GPVTG = 3;

	/**
	 * Character that indicates a warning.
	 */
	private static final String NMEA_WARNING = "V";

	private static final boolean debug = true;

	private StringTokenizer tokenizer = new StringTokenizer("", ",");

	/**
	 * Parses a string sent by GPS receiver.
	 * 
	 * @param s
	 *            String to be parsed
	 * @param record
	 *            Record to store data
	 * @return Type of record
	 * 
	 *         public int parse(String s, NMEARecord record) { int length =
	 *         s.length(); if ((length < 6) || (s.charAt(0) != '$')) { return
	 *         TYPE_NA; }
	 * 
	 *         if (!s.startsWith("$GPRMC")) { return TYPE_NA; }
	 * 
	 *         if (!verifyChecksum(s, length)) { if (debug) {
	 *         Logger.debug("Checksum error", s); } return TYPE_NA; } //
	 *         Tokenizer to separate tokens tokenizer.restart(s, 1, length - 3);
	 * 
	 *         // Type of record int type; try { String token =
	 *         tokenizer.nextToken();
	 * 
	 *         if (token.equals("GPRMC")) { type = TYPE_GPRMC; GPRMC(tokenizer,
	 *         record); } else if (token.equals("GPGGA")) { type = TYPE_GPGGA;
	 *         GPGGA(tokenizer, record); } else if (token.equals("GPVTG")) {
	 *         type = TYPE_GPVTG; GPVTG(tokenizer, record); } else { if (debug)
	 *         { Logger.debug("Type is not supported", token); } return TYPE_NA;
	 *         } } catch (NoSuchElementException e) { return TYPE_NA; } return
	 *         type; }
	 */
	boolean verifyChecksum(String str, int length) {
		// An NMEA checksum is calculated as the XOR of bytes between (but not
		// including) the dollar sign
		// and asterisk.
		if (str.charAt(length - 3) != '*') {
			return false;
		}

		// Loop through all chars to get a checksum
		int checksum = 0;
		for (int i = 1; i < length - 3; i++) {
			checksum ^= str.charAt(i);
		}
		// System.out.println("cs1:" + Integer.toString(checksum, 16));
		// System.out.println("cs2:" + str.substring(length -2));
		return Integer.toString(checksum, 16).equalsIgnoreCase(
				str.substring(length - 2));
	}

	static void GPRMC(StringTokenizer tokenizer, NMEARecord record) {
		// $GPRMC - Recommended Minimum Specific GPS/TRANSIT Data
		// $GPRMC,031955.000,A,4342.7185,N,07919.5022,W,0.00,38.79,231106,,,A*4C
		// GLatLng.create(43.71204506091482, -79.32519435882568)

		record.timeOfFix = tokenizer.nextToken();

		record.warning = tokenizer.nextToken().equals(NMEA_WARNING);

		record.latitudeStr = tokenizer.nextToken();
		record.latitudeDirection = tokenizer.nextToken();

		record.longitudeStr = tokenizer.nextToken();
		record.longitudeDirection = tokenizer.nextToken();

		record.groundSpeed = tokenizer.nextToken();
		record.courseMadeGood = tokenizer.nextToken();

		record.dateOfFix = tokenizer.nextToken();
		// record.magneticVariation = tokenizer.nextToken();
	}

	// Global Positioning System Fix Data
	static void GPGGA(StringTokenizer tokenizer, NMEARecord record) {
		// $GPGGA,031956.000,4342.7185,N,07919.5023,W,1,05,2.7,122.2,M,-35.1,M,,0000*67
		// Time of fix
		tokenizer.nextToken();
		// Lattitude
		tokenizer.nextToken();
		// Lattitude direction
		tokenizer.nextToken();
		// Longitude
		tokenizer.nextToken();
		// Longitude direction
		tokenizer.nextToken();

		// Fix Quality
		// - 0 = Invalid
		// - 1 = GPS fix
		// - 2 = DGPS fix
		record.quality = tokenizer.nextToken();
		record.satelliteCount = tokenizer.nextToken();
		// Ignore rest
		// Horizontal Dilution of Precision (HDOP) {1.5} Relative accuracy of
		// horizontal position
		// Altitude {280.2}, M 280.2 meters above mean sea level

	}

	static void GPVTG(StringTokenizer tokenizer, NMEARecord record) {
		// Track Made Good and Ground Speed.
		//
		// eg1. $GPVTG,360.0,T,348.7,M,000.0,N,000.0,K*43
		// eg2. $GPVTG,054.7,T,034.4,M,005.5,N,010.2,K*41
		//
		// 054.7,T True course made good over ground, degrees
		// 034.4,M Magnetic course made good over ground, degrees
		// 005.5,N Ground speed, N=Knots
		// 010.2,K Ground speed, K=Kilometers per hour
	}

	// public static double parseLatLng(String s, int degreeLen, String
	// direction, String directionNegative)
	// throws NumberFormatException {
	// //$GPRMC, 4342.7185,N, 07919.5022, W
	// //GLatLng.create(43.71204506091482, -79.32519435882568)
	//
	// double d = Double.valueOf(s.substring(0, degreeLen)).doubleValue()
	// + Double.valueOf(s.substring(degreeLen)).doubleValue() / 60D;
	// if (direction.equals(directionNegative)) {
	// d = -d;
	// }
	// return d;
	// }

	/**
	 * Convert latitude or longitude from NMEA format to Google's decimal degree
	 * format.
	 */
	public static double parseLatLng(String valueString, boolean isLatitude,
			String direction, String directionNegative) {
		int degreeInteger = 0;
		double minutes = 0.0;
		if (isLatitude) {
			degreeInteger = Integer.parseInt(valueString.substring(0, 2));
			minutes = Double.parseDouble(valueString.substring(2));
		} else {
			degreeInteger = Integer.parseInt(valueString.substring(0, 3));
			minutes = Double.parseDouble(valueString.substring(3));
		}
		double degreeDecimals = minutes / 60.0;
		double degrees = degreeInteger + degreeDecimals;
		if (direction.equals(directionNegative)) {
			return -degrees;
		} else {
			return degrees;
		}
	}

	public static double parseLatitude(String valueString, String direction) {
		return 0;
	}

	public static double parseLongitude(String valueString, String direction) {
		return 0;
	}

	/*
	 * public static void parsCoordinate(NMEARecord record, LocationCoordinate
	 * coord) { //coord.latitude = parseLatLng(record.latitudeStr, 3,
	 * record.latitudeDirection, "S"); //coord.longitude =
	 * parseLatLng(record.longitudeStr, 3, record.longitudeDirection, "W");
	 * 
	 * coord.latitude = parseLatLng(record.latitudeStr, true,
	 * record.latitudeDirection, "S"); coord.longitude =
	 * parseLatLng(record.longitudeStr, false, record.longitudeDirection, "W");
	 * 
	 * coord.timeOfFix = parsTimeOfFix(record.dateOfFix, record.timeOfFix); }
	 */
	public static String timeOfFix(String timeOfFix) {
		return timeOfFix.substring(0, 2) + ":" + timeOfFix.substring(2, 4)
				+ ":" + timeOfFix.substring(4, 6);
	}

	public static long parsTimeOfFix(String dateOfFix, String timeOfFix) {
		try {
			Calendar calendar = Calendar.getInstance(TimeZone
					.getTimeZone("GMT"));
			// ddmmyy
			calendar.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(dateOfFix.substring(0, 2)));
			calendar.set(Calendar.MONTH,
					Integer.parseInt(dateOfFix.substring(2, 4)) - 1);
			calendar.set(Calendar.YEAR,
					2000 + Integer.parseInt(dateOfFix.substring(4, 6)));
			// hhmmss
			calendar.set(Calendar.HOUR_OF_DAY,
					Integer.parseInt(timeOfFix.substring(0, 2)));
			calendar.set(Calendar.MINUTE,
					Integer.parseInt(timeOfFix.substring(2, 4)));
			calendar.set(Calendar.SECOND,
					Integer.parseInt(timeOfFix.substring(4, 6)));
			return calendar.getTime().getTime();
		} catch (Throwable e) {
			// Logger.error("GPS time", e);
			return 0;
		}
	}
}
