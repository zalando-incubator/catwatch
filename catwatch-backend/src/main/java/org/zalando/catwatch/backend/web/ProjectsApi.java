package org.zalando.catwatch.backend.web;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.service.ProjectService;
import org.zalando.catwatch.backend.util.Constants;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping(value = Constants.API_RESOURCE_PROJECTS, produces = {APPLICATION_JSON_VALUE})
@Api(value = Constants.API_RESOURCE_PROJECTS, description = "the projects API")
public class ProjectsApi {

    private final ProjectService projectService;

    @Autowired
    public ProjectsApi(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ApiOperation(
        value = "Project",
        notes =
            "The Projects endpoint returns all information like name,description, url, stars count, commits count, forks count, contributors count, score, languages used, last pushed of all the projects for the selected filter.",
        response = Project.class, responseContainer = "List"
    )
    @ApiResponses(
        value = {
            @ApiResponse(code = 200, message = "An array of Projects of selected Github organization"),
            @ApiResponse(code = 0, message = "Unexpected error")
        }
    )
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Collection<Project>> projectsGet(
            @ApiParam(value = "List of github.com organizations to scan(comma seperated)", required = false)
            @RequestParam(value = Constants.API_REQUEST_PARAM_ORGANIZATIONS, required = false)
            final String organizations,
            @ApiParam(value = "Number of items to retrieve. Default is 5.")
            @RequestParam(value = Constants.API_REQUEST_PARAM_LIMIT, required = false)
            final Integer limit,
            @ApiParam(value = "Offset the list of returned results by this amount. Default is zero.")
            @RequestParam(value = Constants.API_REQUEST_PARAM_OFFSET, required = false)
            final Integer offset,
            @ApiParam(value = "Date from which to start fetching records from database(default = current_date)")
            @RequestParam(value = Constants.API_REQUEST_PARAM_STARTDATE, required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            final Date startDate,
            @ApiParam(value = "Date till which records will be fetched from database(default = current_date)")
            @RequestParam(value = Constants.API_REQUEST_PARAM_ENDDATE, required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            final Date endDate,
            @ApiParam(
                value =
                    "parameter by which result should be sorted. '-' means descending order (count of star,count of commit, count of forks, count of contributors, score). Default is descending order of score."
            )
            @RequestParam(value = Constants.API_REQUEST_PARAM_SORTBY, required = false)
            final String sortBy,
            @ApiParam(value = "query paramater for search query (this will be project names prefix)")
            @RequestParam(value = Constants.API_REQUEST_PARAM_Q, required = false)
            final String q,
            @ApiParam(value = "query paramater for filtering by primary programming language")
            @RequestParam(value = Constants.API_REQUEST_PARAM_LANGUAGE, required = false)
            final String language
            ) {

        Optional<Date> optionalStartDate = Optional.ofNullable(startDate);
        Optional<Date> optionalEndDate = Optional.ofNullable(endDate);

        Iterable<Project> projects = projectService.findProjects(organizations, Optional.ofNullable(limit),
                Optional.ofNullable(offset), optionalStartDate, optionalEndDate, Optional.ofNullable(sortBy),
                Optional.ofNullable(q),Optional.ofNullable(language));

        return new ResponseEntity<>(Lists.newArrayList(projects), HttpStatus.OK);
    }
}
