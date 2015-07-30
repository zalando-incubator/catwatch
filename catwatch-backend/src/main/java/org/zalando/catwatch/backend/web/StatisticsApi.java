package org.zalando.catwatch.backend.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.service.StatisticsService;
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
    
    @Autowired
    private Environment env;

	@ApiOperation(
			value = "General Statistics of list of Github.com Organizations", 
			notes = "The Statistics endpoint returns snapshot of statistics over a given period of time of the organization \nGithub account. Statistics contains information of the count of all private projects,  public projects,              members, teams, contributors, stars, forks, size, programming languages, tags of the list of Github.com              Organizations.\n", response = Statistics.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "An array of Statistics over selected period of time."), @ApiResponse(code = 0, message = "Unexpected error") })
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Collection<Statistics>> statisticsGet(
			@ApiParam(value = "List of github.com organizations to scan(comma seperated)", required = false) 
			@RequestParam(value = Constants.API_REQUEST_PARAM_ORGANIZATIONS, required = false) 
			String organizations, 
			
			@ApiParam(value = "Date from which to start fetching statistics records from database(default = current date)") 
			@RequestParam(value = Constants.API_REQUEST_PARAM_STARTDATE, required = false) 
			String startDate, 
			
			@ApiParam(value = "Date till which statistics records will be fetched from database(default = current date)") 
			@RequestParam(value = Constants.API_REQUEST_PARAM_ENDDATE, required = false) 
			String endDate

	) {
		
		String organisationList = organizations;
		
		if(organisationList==null){
			organisationList = getOrganizationConfig();
		}
		
		Collection<String> orgs = StringParser.parseStringList(organisationList, ",");
		
		Collection<Statistics> statistics = new ArrayList<>(orgs.size());
		
		
		List<Statistics> unaggregatedStatistics = new ArrayList<>();
		
		
		if(startDate == null && endDate ==null){
			for (String orgName : orgs){

				List<Statistics> s = repository.findByOrganizationNameOrderByKeySnapshotDateDesc(orgName, new PageRequest(0, 1));
				
				unaggregatedStatistics.addAll(s);
			}
			
			if(unaggregatedStatistics!=null && unaggregatedStatistics.size()>0){
				Statistics aggregatedStatistics = StatisticsService.aggregateStatistics(unaggregatedStatistics);
				
				statistics.add(aggregatedStatistics);
			}
			
		}
		else{
			//filter by start and end date
			
			
			statistics = getStatisticsByDate(orgs, startDate, endDate);
		}
		
		ResponseEntity<Collection<Statistics>> res = new ResponseEntity<>(statistics, HttpStatus.OK);
		
		return res;
	}
	
	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public String handleException(Exception e) {
	    return e.getMessage();
	}
	
	
	
	private String getOrganizationConfig() {
		
		if(!env.containsProperty(Constants.CONFIG_ORGANIZATION_LIST)){
			//TODO throw new exception
			return "";
		}
		
		return env.getProperty(Constants.CONFIG_ORGANIZATION_LIST);
	}


	private Collection<Statistics> getStatisticsByDate(Collection<String> orgs, String startDate, String endDate){
		
		if(endDate!=null && startDate==null){
			throw new IllegalArgumentException("No start date specified");
		}
		
		if(startDate==null && endDate!=null){
			throw new IllegalArgumentException("Start date parameter missing");
		}
		
		Date start, end;
		try {
			start = StringParser.parseIso8601Date(startDate);
		} catch (ParseException e) {
			throw new IllegalArgumentException(Constants.ERR_MSG_WRONG_DATE_FORMAT+" for stardDate");
		}
		
		
		try {
			end = endDate == null ? new Date() : StringParser.parseIso8601Date(endDate);
		} catch (ParseException e) {
			throw new IllegalArgumentException(Constants.ERR_MSG_WRONG_DATE_FORMAT+" for endDate");
		}
		
		if(start.after(end)){
			throw new IllegalArgumentException("Start date is after end date");
		}
		
		List<List<Statistics>> statisticsLists = new ArrayList<>();
		
		//get statistics for each organization
		for (String orgName : orgs){

			List<Statistics> s = repository.findStatisticsByOrganizationAndDate(orgName, start, end);
			statisticsLists.add(s);
		}
		
		return StatisticsService.aggregateHistoricalStatistics(statisticsLists);
	}

}
