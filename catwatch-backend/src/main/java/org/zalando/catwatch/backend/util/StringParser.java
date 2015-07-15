package org.zalando.catwatch.backend.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StringParser {
	
	
	/**
	 * Parses a list of strings given as String and separated with a delemiter, e.g. \"foo, bar, lock\"
	 * @param stringList
	 * @param delimiter
	 * @return
	 */
	public static Collection<String> parseStringList(String stringList, String delimiter){
		
		if(stringList == null) return Collections.emptyList();
		
		String[] array = stringList.split(delimiter);
		
		
		List<String> list = new ArrayList<String>(array.length);
		
		for (int i=0; i<array.length; i++){
			list.add(array[i].trim());
		}
		
		return list;
	}

}
