package cn.kykys.index.utils;

import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static final String SIMPLE_FORMAT = "yyyy-MM-dd";
	public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String NUMBER_FORMAT = "yyyyMMdd";
	public static String WEEKS[] = { "日", "一", "二", "三", "四", "五", "六" };
	
	/**
	 * 短租最大天数(不包括)
	 */
	public static final int MAX_SHORT_DAYS = 28;

	public static long getDayDifference(String s, String e, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date sd = sdf.parse(s);
			Date ed = sdf.parse(e);
			long day = Math.abs((ed.getTime() - sd.getTime()) / (24 * 60 * 60 * 1000));
			return day == 0 ? 1 : day;
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return -1;
	}

	public static SimpleDateFormat getSimpleDateFormat(String pattern) {
		return new SimpleDateFormat(pattern);
	}

	/**
	 * 获取系统时间
	 * 
	 * @return Date
	 */
	public static Date getNewDate() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 获取系统时间
	 * 
	 * @return Calendar
	 */
	public static Calendar getCalendar() {
		return Calendar.getInstance();
	}

	/**
	 * 获取系统时间
	 * 
	 * @return Calendar
	 */
	public static Calendar getCalendar(Date date) {
		Calendar c = getCalendar();
		c.setTime(date);
		return c;
	}

	/**
	 * 时间格式化
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern);
	}

	/**
	 * 时间格式化
	 * 
	 * @param calendar
	 * @param pattern
	 * @return
	 */
	public static String format(Calendar calendar, String pattern) {
		return DateFormatUtils.format(calendar, pattern);
	}

	/**
	 * 
	 * 时间格式化
	 * 
	 * @param millis
	 * @param pattern
	 * @return
	 */
	public static String format(long millis, String pattern) {
		return DateFormatUtils.format(millis, pattern);
	}

	/**
	 * 时间格式化
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date format(String date, String pattern) {
		return DateTimeFormat.forPattern(pattern).parseDateTime(date).toDate();
	}

	/**
	 * 两时间相隔的自然年
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getIntervalYears(Date start, Date end) {
		Period period = new Period(start.getTime(), end.getTime(), PeriodType.years());
		return period.getYears();
	}

	/**
	 * 两时间相隔的自然月
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getIntervalMonths(Date start, Date end) {
		Period period = new Period(start.getTime(), end.getTime(), PeriodType.months());
		return period.getMonths();
	}

	/**
	 * 两时间相隔的自然日
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getIntervalDays(Date start, Date end) {
		Period period = new Period(start.getTime(), end.getTime(), PeriodType.days());
		return period.getDays();
	}

	/**
	 * 两时间相隔的自然日
	 * 
	 * @param start
	 * @param end
	 * @return [years,months,days]
	 */
	public static int[] getInterval(Date start, Date end) {
		Period period = new Period(start.getTime(), end.getTime(), PeriodType.yearMonthDay());
		int years = period.getYears();
		int months = period.getMonths();
		int days = period.getDays();
		int[] result = { years, months, days };
		return result;
	}

	/**
	 * 返回友好的租期格式
	 * 
	 * @param startTime
	 * @param endTime
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static String niceTenancy(String startTime, String endTime, String format) throws ParseException {
		String tenancy = null;
		long days = getDayDifference(startTime, endTime, format);
		if (days < MAX_SHORT_DAYS) {
			tenancy = days + "晚";
		} else {
			SimpleDateFormat sdf = DateUtils.getSimpleDateFormat(format);
			Period period = new Period(sdf.parse(startTime.toString()).getTime(), sdf.parse(endTime.toString()).getTime(), PeriodType.yearMonthDayTime());
			int extra = period.getDays();
			int year = period.getYears();
			int month = period.getMonths() + year * 12;
			tenancy = month + "月" + (extra > 0 ? extra + "天" : "");
		}
		return tenancy;
	}

	/**
	 * 根据时间返回周几
	 * @param date
	 * @return "日", "一", "二", "三", "四", "五", "六"
	 */
	public static String getWeek(Date date) {
		if(date == null){
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return WEEKS[dayOfWeek];
	}

}
