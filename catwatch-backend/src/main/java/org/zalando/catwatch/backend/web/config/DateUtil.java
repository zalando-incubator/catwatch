package org.zalando.catwatch.backend.web.config;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	public static Date iso8601(String date) {
		try {
			return newDateFormat().parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String iso8601(Date date) {
		return newDateFormat().format(date);
	}

	private static DateFormat newDateFormat() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat;
	}
}
