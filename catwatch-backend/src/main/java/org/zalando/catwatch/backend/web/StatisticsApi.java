package org.zalando.catwatch.backend.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import org.zalando.catwatch.backend.util.DataAggregator;
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
			String startDate, 
			
			@ApiParam(value = "Date till which statistics records will be fetched from database(default = current date)") 
			@RequestParam(value = Constants.API_REQUEST_PARAM_ENDDATE, required = false) 
			String endDate

	) throws NotFoundException {
		
		String organisationList = organizations;
		
		if(organisationList==null){
			//TODO read organizations from config file
		}
		
		Collection<String> orgs = StringParser.parseStringList(organisationList, ",");
		
		Collection<Statistics> statistics = new ArrayList<>(orgs.size());
		
		
		List<Statistics> unaggregatedStatistics = new ArrayList<>();
		
		if(startDate == null && endDate ==null){
			for (String orgName : orgs){

				List<Statistics> s = repository.findByOrganizationNameOrderByKeySnapshotDateDesc(orgName, new PageRequest(0, 1));
				
				unaggregatedStatistics.addAll(s);
			}
			
			Statistics aggregatedStatistics = DataAggregator.aggregateStatistics(unaggregatedStatistics);
			
			statistics.add(aggregatedStatistics);
		}
		else{
			//filter by start and end date
			
			statistics = getStatisticsByDate(orgs, startDate, endDate);
		}
		
		ResponseEntity<Collection<Statistics>> res = new ResponseEntity<>(statistics, HttpStatus.OK);
		
		return res;
	}
	
	
	private Collection<Statistics> getStatisticsByDate(Collection<String> orgs, String startDate, String endDate){
		
		if(endDate!=null && startDate==null){
			throw new IllegalArgumentException("No start date specified");
		}
		
		Date start, end;
		try {
			start = StringParser.getDate(startDate);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid date format for stardDate");
		}
		
		
		try {
			end = endDate == null ? new Date() : StringParser.getDate(endDate);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid date format for endDate");
		}
		
		List<List<Statistics>> statisticsLists = new ArrayList<>();
		
		//get statistics for each organization
		for (String orgName : orgs){

			List<Statistics> s = repository.findStatisticsByOrganizationAndDate(orgName, start, end);
			statisticsLists.add(s);
		}
		
		return DataAggregator.aggregateHistoricalStatistics(statisticsLists);
	}

}
