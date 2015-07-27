package org.zalando.catwatch.backend.util;

import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class StringParserTest {
	
//	@Test (expected = ParseException.class)
//	public void testGetWrongDate(){
//		
//		String date = new Date().toString();
//		
//		Date d = StringParser.getDate(date);
//		
//		
//	}
	
	@Test
	public void testGetDate() throws ParseException{
		
		Date now = new Date();
		
		String date = StringParser.getISO8601StringForDate(now);
		
		Date d = StringParser.parseIso8601Date(date);
		
		Assert.assertEquals(now.toString(),  d.toString());
		
		//assert parsing without exception
//		d = StringParser.parseIso8601Date("2015-05-28T14:09:17+02:00");
//		
//		d = StringParser.parseIso8601Date("1996-12-19T16:39:57-08:00");
//		
//		d = StringParser.parseIso8601Date("1985-04-12T23:20:50.52Z");
//		
//		d = StringParser.parseIso8601Date("1990-12-31T23:59:60Z");
//		
//		d = StringParser.parseIso8601Date("1990-12-31T15:59:60-08:00");
//		
//		
//		d = StringParser.parseIso8601Date("1937-01-01T12:00:27.87+00:20");
		
		
	}

}
