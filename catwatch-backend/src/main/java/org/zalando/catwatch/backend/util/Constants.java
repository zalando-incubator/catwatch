package org.zalando.catwatch.backend.util;

public class Constants {
	
	public static final String 
		API_RESOURCE_CONTRIBUTORS = "/contributors",
		API_RESOURCE_STATISTICS = "/statistics",
		API_RESOURCE_PROJECTS = "/projects",
		API_RESOURCE_LANGUAGES = "/languages",
		
		API_REQUEST_PARAM_ENDDATE = "end_date",
		API_REQUEST_PARAM_STARTDATE = "start_date",
		API_REQUEST_PARAM_SORTBY = "sortBy",
		API_REQUEST_PARAM_ORGANIZATIONS = "organizations",
		API_REQUEST_PARAM_LIMIT = "limit",
		API_REQUEST_PARAM_OFFSET = "offset",
		API_REQUEST_PARAM_Q = "q",
	    API_REQUEST_PARAM_LANGUAGE = "language",

		CONFIG_ORGANIZATION_LIST = "organization.list",
		CONFIG_DEFAULT_LIMIT = "default.item.limit",
		
		ERR_MSG_WRONG_DATE_FORMAT = "Invalid date format";
}
