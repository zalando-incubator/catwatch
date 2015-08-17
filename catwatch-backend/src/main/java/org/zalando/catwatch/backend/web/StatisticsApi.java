package org.zalando.catwatch.backend.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.time.temporal.ChronoUnit;
import java.util.*;

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
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.service.StatisticsService;
import org.zalando.catwatch.backend.util.Constants;
import org.zalando.catwatch.backend.util.ProjectStats;
import org.zalando.catwatch.backend.util.ContributorStats;
import org.zalando.catwatch.backend.util.StringParser;

@Controller
@RequestMapping(value = Constants.API_RESOURCE_STATISTICS, produces = { APPLICATION_JSON_VALUE })
@Api(value = Constants.API_RESOURCE_STATISTICS, description = "the statistics API")
public class StatisticsApi {

	@Autowired
	private StatisticsRepository repository;

	@Autowired
	private ProjectRepository projectRepository;

    @Autowired
    private ContributorRepository contributorRepository;

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

	private Date parseDate(String dateString, Date defaultValue) {
		if (dateString == null)
			return defaultValue;
		else {
			try {
				return StringParser.parseIso8601Date(dateString);
			} catch(java.text.ParseException e) {
				throw new IllegalArgumentException("Couldn't parse date string " + dateString + ".");
			}
		}
	}

	@RequestMapping(value = "/projects", method = RequestMethod.GET)
	public ResponseEntity<Collection<ProjectStats>> statisticsProjectGet(
			@ApiParam(value = "List of github.com organizations to scan(comma seperated)", required = false)
			@RequestParam(value = Constants.API_REQUEST_PARAM_ORGANIZATIONS, required = false)
			String organizations,
			@ApiParam(value = "Date from which to start fetching statistics records from database(default = current date)")
			@RequestParam(value = Constants.API_REQUEST_PARAM_STARTDATE, required = false)
			String startDateString,
			@ApiParam(value = "Date till which statistics records will be fetched from database(default = current date)")
			@RequestParam(value = Constants.API_REQUEST_PARAM_ENDDATE, required = false)
			String endDateString
	) throws java.text.ParseException {

		Date now = new Date();

		Date startDate = parseDate(startDateString, Date.from(now.toInstant().minus(30, ChronoUnit.DAYS)));
		Date endDate = parseDate(endDateString, now);

		List<Project> projects = null;

		if (organizations == null) {
			projects = projectRepository.findProjectsByDateRange(startDate, endDate);
		} else {
			Collection<String> orgs = StringParser.parseStringList(organizations, ",");
			projects = projectRepository.findProjectsByOrganizationNameAndDateRange(orgs, startDate, endDate);
		}
		assert (projects != null);

		List<ProjectStats> result = ProjectStats.buildStats(projects);

		// only top 10 by last score
		result.sort((ps1, ps2) -> -ps1.getScores().get(ps1.getScores().size() - 1)
			.compareTo(ps2.getScores().get(ps2.getScores().size() - 1)));

		ResponseEntity<Collection<ProjectStats>> res = new ResponseEntity<>(result.subList(0, 3), HttpStatus.OK);

		return res;
	}

    @RequestMapping(value = "/contributors", method = RequestMethod.GET)
	public ResponseEntity<Collection<ContributorStats>> statisticsContributorGet(
            @ApiParam(value = "List of github.com organizations to scan(comma seperated)", required = false)
            @RequestParam(value = Constants.API_REQUEST_PARAM_ORGANIZATIONS, required = false)
			String organizations,
            @ApiParam(value = "Date from which to start fetching statistics records from database(default = current date)")
            @RequestParam(value = Constants.API_REQUEST_PARAM_STARTDATE, required = false)
			String startDateString,
            @ApiParam(value = "Date till which statistics records will be fetched from database(default = current date)")
            @RequestParam(value = Constants.API_REQUEST_PARAM_ENDDATE, required = false)
			String endDateString
	) throws java.text.ParseException {
		Date now = new Date();
        Date startDate = parseDate(startDateString, Date.from(now.toInstant().minus(30, ChronoUnit.DAYS)));
        Date endDate = parseDate(endDateString, now);
        List<Contributor> contributors = null;
        if (organizations == null) {
            contributors = contributorRepository.findContributorsTimeSeries(null, startDate, endDate, null);
        } else {
			Collection<String> orgs = StringParser.parseStringList(organizations, ",");
			contributors = contributorRepository.findContributorsByOrganizationAndDate(orgs, startDate, endDate);
        }
        assert (contributors != null);
        List<ContributorStats> result = ContributorStats.buildStats(contributors);

        result.sort((cs1, cs2) -> -cs1.getOrganizationalCommitsCounts().get(cs1.getOrganizationalCommitsCounts().size()-1)
                .compareTo(cs2.getOrganizationalCommitsCounts().get(cs2.getOrganizationalCommitsCounts().size()-1)));

        return new ResponseEntity<>(result.subList(0, 3), HttpStatus.OK);
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