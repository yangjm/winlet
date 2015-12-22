package com.aggrepoint.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
	public static Date get(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

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
		if (dt == null)
			return null;

		if (tz == null)
			tz = TimeZone.getDefault();

		long time = dt.getTime();

		if (tz.inDaylightTime(dt))
			time -= tz.getDSTSavings();
		time -= tz.getRawOffset();

		return new Date(time);
	}

	public static Date toGmt(Date dt) {
		if (dt == null)
			return null;

		return toGmt(dt, null);
	}

	public static int getYear(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		return cal.get(Calendar.YEAR);
	}

	public static int getAge(Date dateOfBirth, Date onDate) {
		Calendar now = Calendar.getInstance();
		Calendar dob = Calendar.getInstance();

		now.setTime(onDate);
		dob.setTime(dateOfBirth);
		if (dob.after(now))
			return -1;

		int year1 = now.get(Calendar.YEAR);
		int year2 = dob.get(Calendar.YEAR);
		int age = year1 - year2;
		int month1 = now.get(Calendar.MONTH);
		int month2 = dob.get(Calendar.MONTH);
		if (month2 > month1) {
			age--;
		} else if (month1 == month2) {
			int day1 = now.get(Calendar.DAY_OF_MONTH);
			int day2 = dob.get(Calendar.DAY_OF_MONTH);
			if (day2 > day1) {
				age--;
			}
		}

		return age;
	}

	public static int getMonth(Date dateOfBirth, Date onDate) {
		Calendar now = Calendar.getInstance();
		Calendar dob = Calendar.getInstance();

		now.setTime(onDate);
		dob.setTime(dateOfBirth);
		if (dob.after(now))
			return -1;

		int year1 = now.get(Calendar.YEAR);
		int year2 = dob.get(Calendar.YEAR);
		int month1 = now.get(Calendar.MONTH);
		int month2 = dob.get(Calendar.MONTH);
		int day1 = now.get(Calendar.DAY_OF_MONTH);
		int day2 = dob.get(Calendar.DAY_OF_MONTH);

		int months = (year1 - year2) * 12 + month1 - month2;
		if (day2 > day1)
			months--;

		return months;
	}

	public static int getGrade(Date gradeDate, int grade, Date onDate) {
		Calendar calGradeDate = Calendar.getInstance();
		Calendar calOnDate = Calendar.getInstance();

		calGradeDate.setTime(gradeDate);
		calOnDate.setTime(onDate);

		int gradeDateYear = calGradeDate.get(Calendar.YEAR);
		if (calGradeDate.get(Calendar.MONTH) >= 8)
			gradeDateYear++;
		int onDateYear = calOnDate.get(Calendar.YEAR);
		if (calOnDate.get(Calendar.MONTH) >= 8)
			onDateYear++;

		grade += (onDateYear - gradeDateYear);
		if (grade < 0)
			grade = 0;
		else if (grade > 13) // 0 = JK, 1 = SK, ... 13 = grade 12
			grade = 13;

		return grade;
	}

	public static String getAgeDisplay(Date dateOfBirth) {
		if (dateOfBirth == null)
			return "";

		Date now = new Date();

		int year = getAge(dateOfBirth, now);
		int month = getMonth(dateOfBirth, now) % 12;

		StringBuffer sb = new StringBuffer();
		sb.append(year);
		if (year > 1)
			sb.append(" yrs ");
		else
			sb.append(" yr ");

		if (month == 0)
			return sb.toString();

		if (month > 1)
			sb.append(month).append(" mos");
		else
			sb.append(month).append(" mo");

		return sb.toString();

	}
}
