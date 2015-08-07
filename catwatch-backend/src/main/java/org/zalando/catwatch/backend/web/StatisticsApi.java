package org.zalando.catwatch.backend.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

@Controller
@RequestMapping(value = Constants.API_RESOURCE_STATISTICS, produces = { APPLICATION_JSON_VALUE })
@Api(value = Constants.API_RESOURCE_STATISTICS, description = "the statistics API")
public class StatisticsApi {

	@Autowired
	private StatisticsRepository repository;

	@Autowired
	private Environment env;

	@ApiOperation(value = "General Statistics of list of Github.com Organizations", notes = "The Statistics endpoint returns snapshot of statistics over a given period of time of the organization \nGithub account. Statistics contains information of the count of all private projects,  public projects,              members, teams, contributors, stars, forks, size, programming languages, tags of the list of Github.com              Organizations.\n", response = Statistics.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "An array of Statistics over selected period of time."),
			@ApiResponse(code = 0, message = "Unexpected error") })
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Collection<Statistics>> statisticsGet(
			@ApiParam(value = "List of github.com organizations to scan(comma seperated)", required = false) @RequestParam(value = Constants.API_REQUEST_PARAM_ORGANIZATIONS, required = false) String organizations,

	@ApiParam(value = "Date from which to start fetching statistics records from database(default = current date)") @RequestParam(value = Constants.API_REQUEST_PARAM_STARTDATE, required = false) String startDate,

	@ApiParam(value = "Date till which statistics records will be fetched from database(default = current date)") @RequestParam(value = Constants.API_REQUEST_PARAM_ENDDATE, required = false) String endDate

	) {

		String organisationList = organizations;

		if (organisationList == null) {
			organisationList = getOrganizationConfig();
		}

		Collection<String> orgs = StringParser.parseStringList(organisationList, ",");

		Collection<Statistics> statistics = StatisticsService.getStatistics(repository, orgs, startDate, endDate);

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

		if (!env.containsProperty(Constants.CONFIG_ORGANIZATION_LIST)) {
			// TODO throw new exception
			return "";
		}

		return env.getProperty(Constants.CONFIG_ORGANIZATION_LIST);
	}

	


}