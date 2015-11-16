package com.operasoft.snowboard.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HandlerUtils {

	public static String getCurrentDate() {
		return new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss").format(new Date());
	}

	public static String getCurrentTime() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	public static String getDate(Date curDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		//		Date curDate = new Date(timeMilliSeconds);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.format(curDate);
	}

	public static String getTime(Date curDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		//		Date curTime = new Date(timeMilliSeconds);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.format(curDate);
	}

}
