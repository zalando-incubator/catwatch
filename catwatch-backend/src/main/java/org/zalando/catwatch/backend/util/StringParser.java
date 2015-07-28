package org.zalando.catwatch.backend.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.common.collect.Lists;

public class StringParser {

	/**
	 * Parses a list of strings given as String and separated with a delemiter,
	 * e.g. \"foo, bar, lock\"
	 * 
	 * @param stringList
	 * @param delimiter
	 * @return
	 */
	public static Collection<String> parseStringList(String stringList, String delimiter) {

		if (stringList == null)
			return Collections.emptyList();

		if (!stringList.contains(delimiter))
			return Lists.newArrayList(stringList);

		String[] array = stringList.split(delimiter);

		List<String> list = new ArrayList<String>(array.length);

		for (int i = 0; i < array.length; i++) {
			list.add(array[i].trim());
		}

		return list;
	}

	public static String getISO8601StringForDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	public static Date parseIso8601Date(String iso8601Date) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		// this is zero time so we need to add that TZ indicator for
		if (iso8601Date.endsWith("Z")) {
			iso8601Date = iso8601Date.substring(0, iso8601Date.length() - 1) + "GMT-00:00";
		} else {
			int inset = 6;

			String s0 = iso8601Date.substring(0, iso8601Date.length() - inset);
			String s1 = iso8601Date.substring(iso8601Date.length() - inset, iso8601Date.length());

			iso8601Date = s0 + "GMT" + s1;
		}

		return df.parse(iso8601Date);
	}

	public static Date parseRFC3339Date(String datestring) throws ParseException {
		
		Date d = new Date();

		// if there is no time zone, we don't need to do any special parsing.
		if (datestring.endsWith("Z")) {
			try {
				// spec for RFC3339
				SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				d = s.parse(datestring);
			} catch (java.text.ParseException pe) {// try again with optional decimals
				// spec for  RFC3339  (with  fractional seconds)
				SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
				s.setLenient(true);
				d = s.parse(datestring);
			}
			return d;
		}

		// step one, split off the timezone.
		String firstpart = datestring.substring(0, datestring.lastIndexOf('-'));
		String secondpart = datestring.substring(datestring.lastIndexOf('-'));

		// step two, remove the colon from the timezone offset
		secondpart = secondpart.substring(0, secondpart.indexOf(':'))
				+ secondpart.substring(secondpart.indexOf(':') + 1);
		
		datestring = firstpart + secondpart;
		
		// spec for RFC3339
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		try {
			d = s.parse(datestring);
		} catch (java.text.ParseException pe) {// try again with optional decimals
			// spec  for  RFC3339  (with  fractional  seconds)
			s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
			s.setLenient(true);
			d = s.parse(datestring);
		}
		return d;
	}
}
