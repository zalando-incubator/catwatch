package org.zalando.catwatch.backend.util;

import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.google.common.collect.Lists;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

		List<String> list = new ArrayList<>(array.length);

		for (String s : array) {
			list.add(s.trim());
		}

		return list;
	}

	public static String getISO8601StringForDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMANY);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	public static Date parseIso8601Date(String iso8601Date) throws ParseException {
		return ISO8601Utils.parse(iso8601Date, new ParsePosition(0));
	}

}
