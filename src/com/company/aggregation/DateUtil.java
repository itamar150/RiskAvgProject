package com.company.aggregation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
	
	/**
	 * init few final date format
	 */
	final static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
	final static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	final static SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
	final static SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

	/**
	 * Convert string to Date.
	 * @param dateStr
	 * @return
	 */
    public static Date convertStringToDate(String dateStr) {

    	Date date = null;
		try {
			date = formatter.parse(dateStr);
		} catch (ParseException e) {
		}
    	
    	return date;
    }
    
    /**
     * 
     * @param dateStr
     * @return the YEAR on specific date
     */
    public static Integer getYear(Date dateStr) {
    	String currentYear = null;
		currentYear = yearFormat.format(dateStr);
    	return Integer.valueOf(currentYear);
    }
    
    /**
     * 
     * @param dateStr
     * @return the MONTH on specific date
     * 
     */
    public static Integer getMonth(Date dateStr) {
    	String currentMonth = null;
    	currentMonth = monthFormat.format(dateStr);
    	return Integer.valueOf(currentMonth);
    }
    
    /**
     * 
     * @param dateStr
     * @return the DAY on specific date
     */
    public static Integer getDay(Date dateStr) {
    	String currentDay = null;
		currentDay = dayFormat.format(dateStr);
    	return Integer.valueOf(currentDay);
    }

}
