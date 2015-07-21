package org.zalando.catwatch.backend.util;

import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class StringParserTest {
	
//	@Test (expected = IllegalArgumentException.class)
//	public void testGetWrongDate(){
//		
//		String date = "";
//		
//		Date d = StringParser.getDate(date);
//		
//		
//	}
	
	@Test
	public void testGetDate() throws ParseException{
		
		Date now = new Date();
		
		String date = StringParser.getISO8601StringForDate(now);
		
		Date d = StringParser.getDate(date);
		
		
		Assert.assertEquals(now.toString(),  d.toString());
	}

}
