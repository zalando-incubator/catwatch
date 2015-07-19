package org.zalando.catwatch.backend.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.util.Constants;
import org.zalando.catwatch.backend.util.StringParser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(value = Constants.API_RESOURCE_STATISTICS, produces = { APPLICATION_JSON_VALUE })
@Api(value = Constants.API_RESOURCE_STATISTICS, description = "the statistics API")
public class StatisticsApi {
	
    @Autowired
    private StatisticsRepository repository;
    
	

	@ApiOperation(
			value = "General Statistics of list of Github.com Organizations", 
			notes = "The Statistics endpoint returns snapshot of statistics over a given period of time of the organization \nGithub account. Statistics contains information of the count of all private projects,  public projects,              members, teams, contributors, stars, forks, size, programming languages, tags of the list of Github.com              Organizations.\n", response = Statistics.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "An array of Statistics over selected period of time."), @ApiResponse(code = 0, message = "Unexpected error") })
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Collection<Statistics>> statisticsGet(
			@ApiParam(value = "List of github.com organizations to scan(comma seperated)", required = true) 
			@RequestParam(value = Constants.API_REQUEST_PARAM_ORGANIZATIONS, required = true) 
			String organizations, 
			
			@ApiParam(value = "Date from which to start fetching statistics records from database(default = current date)") 
			@RequestParam(value = Constants.API_REQUEST_PARAM_STARTDATE, required = false) 
			Date startDate, 
			
			@ApiParam(value = "Date till which statistics records will be fetched from database(default = current date)") 
			@RequestParam(value = Constants.API_REQUEST_PARAM_ENDDATE, required = false) 
			Date endDate

	) throws NotFoundException {
		
		Collection<String> orgs = StringParser.parseStringList(organizations, ",");
		
		List<Statistics> statistics = new ArrayList<>(orgs.size());
		
		for (String orgName : orgs){
			statistics.addAll(repository.findByOrganizationName(orgName));
		}
		
		//TODO filter by start and end date
		
		ResponseEntity<Collection<Statistics>> res = new ResponseEntity<>(statistics, HttpStatus.OK);
		
		//TODO  do some magic!
		return res;
	}

}
