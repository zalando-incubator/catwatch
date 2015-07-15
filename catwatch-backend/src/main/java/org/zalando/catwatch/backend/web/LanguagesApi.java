package org.zalando.catwatch.backend.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.util.Constants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(value = Constants.API_RESOURCE_LANGUAGES, produces = { APPLICATION_JSON_VALUE })
@Api(value = Constants.API_RESOURCE_LANGUAGES, description = "the languages API")
public class LanguagesApi {

	@ApiOperation(value = "Project programming language", notes = "The languages endpoint returns information about the languages used for projects by selected Github Organizations order by the number of projects using the programming language.", response = Language.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "An array of programming language used and count of projects using it."),
			@ApiResponse(code = 0, message = "Unexpected error") })
	@RequestMapping(value = "",

	method = RequestMethod.GET)
	public ResponseEntity<Language> languagesGet(
			@ApiParam(value = "List of github.com organizations to scan(comma seperated)", required = true) 
			@RequestParam(value = Constants.API_REQUEST_PARAM_ORGANIZATIONS, required = true) 
			String organizations, 
			
			@ApiParam(value = "Number of items to retrieve. Default is 5.") 
			@RequestParam(value = Constants.API_REQUEST_PARAM_LIMIT, required = false) 
			Integer limit, 
			
			@ApiParam(value = "Offset the list of returned results by this amount. Default is zero.") 
			@RequestParam(value = Constants.API_REQUEST_PARAM_OFFSET, required = false) 
			Integer offset, 
			
			@ApiParam(value = "query paramater for search query (this can be language name prefix)") 
			@RequestParam(value = Constants.API_REQUEST_PARAM_Q, required = false) 
			String q

	) throws NotFoundException {
		//TODO do some magic!
		
		return new ResponseEntity<Language>(HttpStatus.OK);
	}

}
