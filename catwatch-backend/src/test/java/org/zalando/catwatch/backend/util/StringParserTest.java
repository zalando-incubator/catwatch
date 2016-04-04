package org.zalando.catwatch.backend.util;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class StringParserTest {
	
	@Test
	public void testGetDate() throws ParseException{
		
		Date now = new Date();
		
		String date = StringParser.getISO8601StringForDate(now);
		
		Date d = StringParser.parseIso8601Date(date);
		
		Assert.assertEquals(now.toString(),  d.toString());
		
		//assert parsing without exception
		d = StringParser.parseIso8601Date("2015-05-28T14:09:17+02:00");
		
		
		d = StringParser.parseIso8601Date("1990-12-31T15:59:59-08:00");
		d = StringParser.parseIso8601Date("1996-12-19T16:39:57-08:00");
	}

}
