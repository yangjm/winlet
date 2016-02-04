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

	public static Date addDay(Date dt, int n) {
		if (dt == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.add(Calendar.DAY_OF_YEAR, n);
		return cal.getTime();
	}

	public static Date addMonth(Date dt, int n) {
		if (dt == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.add(Calendar.MONTH, n);
		return cal.getTime();
	}

	public static Date prevDate(Date dt) {
		return addDay(dt, -1);
	}

	public static Date nextDate(Date dt) {
		return addDay(dt, 1);
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

	static int GRADE_YEAR = 0;
	static long GRADE_YEAR_VALID_TO = 0;

	public static synchronized int getCurrentGradeYear() {
		if (System.currentTimeMillis() > GRADE_YEAR_VALID_TO) {
			Calendar cal = Calendar.getInstance();
			GRADE_YEAR = cal.get(Calendar.YEAR);
			if (cal.get(Calendar.MONTH) <= Calendar.JUNE)
				GRADE_YEAR--;
			GRADE_YEAR_VALID_TO = System.currentTimeMillis() + 60 * 1000l;
		}
		return GRADE_YEAR;
	}

	public static int getGradeYear(Date date) {
		if (date == null)
			return getCurrentGradeYear();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int gradeYear = cal.get(Calendar.YEAR);
		if (cal.get(Calendar.MONTH) <= Calendar.JUNE)
			gradeYear--;
		return gradeYear;
	}

	/**
	 * 学生在year年的9月份是grade年级，计算他在onDate是几年级
	 */
	public static int getGrade(int year, int grade, Date onDate) {
		Calendar calOnDate = Calendar.getInstance();
		calOnDate.setTime(onDate);
		int onDateYear = calOnDate.get(Calendar.YEAR);
		if (calOnDate.get(Calendar.MONTH) < Calendar.SEPTEMBER)
			onDateYear--;

		grade += (onDateYear - year);
		if (grade < 0)
			grade = 0;
		else if (grade > 13) // 0 = JK, 1 = SK, ... 13 = grade 12
			grade = 13;

		return grade;
	}

	/**
	 * 学生在year年的9月份是grade年级，计算他在onYear的9月份是几年级
	 */
	public static Integer getGrade(Integer year, Integer grade, Integer onYear) {
		if (year == null || grade == null || onYear == null)
			return null;

		grade += onYear - year;
		if (grade < 0)
			grade = 0;
		else if (grade > 13)
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
