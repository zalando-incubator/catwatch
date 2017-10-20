package org.zalando.catwatch.backend.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AdminController {

    private final ContributorRepository contributorRepository;
    private final StatisticsRepository statisticsRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public AdminController(ContributorRepository contributorRepository,
                           StatisticsRepository statisticsRepository,
                           ProjectRepository projectRepository) {
        this.contributorRepository = contributorRepository;
        this.statisticsRepository = statisticsRepository;
        this.projectRepository = projectRepository;
    }

    @RequestMapping(value = "/config/scoring.project", method = POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public List<String> configScoringProjects(@RequestBody(required=false) String scoringProject,
                                              @RequestHeader(value="X-Organizations", required=false) String organizations) {
        //This endpoint enabled execution of arbitrary JS code passed in through the body, deactivated for now
        throw new UnsupportedOperationException("This endpoint is deactivated.");
    }

    @RequestMapping(value = "/init", method = GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String init() {
        //Unsecured writable endpoint deactivated
        throw new UnsupportedOperationException("This endpoint is deactivated.");
    }

    @RequestMapping(value = "/delete", method = GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String deleteAll() {
        //Unsecured writable endpoint deactivated
        throw new UnsupportedOperationException("This endpoint is deactivated.");
    }

    @RequestMapping(value = "/import", method = POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String importJson(@RequestBody DatabaseDto dto) {
        //Unsecured writable endpoint deactivated
        throw new UnsupportedOperationException("This endpoint is deactivated.");
    }

    @RequestMapping(value = "/export", method = GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public DatabaseDto exportJson() {
        DatabaseDto dto = new DatabaseDto();
        dto.contributors.addAll(newArrayList(contributorRepository.findAll()));
        dto.projects.addAll(newArrayList(projectRepository.findAll()));
        dto.statistics.addAll(newArrayList(statisticsRepository.findAll()));
        return dto;
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public String handleException(Exception e) {
        return e.getMessage();
    }
}
