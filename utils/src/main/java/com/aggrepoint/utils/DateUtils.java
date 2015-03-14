package com.aggrepoint.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
	public static Date dayStart(Date dt) {
		if (dt == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date dayEnd(Date dt) {
		if (dt == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	public static Date prevDate(Date dt) {
		if (dt == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		return cal.getTime();
	}

	public static Date toGmt(Date dt, TimeZone tz) {
		if (tz == null)
			tz = TimeZone.getDefault();

		long time = dt.getTime();

		if (tz.inDaylightTime(dt))
			time -= tz.getDSTSavings();
		time -= tz.getRawOffset();

		return new Date(time);
	}

	public static Date toGmt(Date dt) {
		return toGmt(dt, null);
	}
}
