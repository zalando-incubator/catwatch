package org.zalando.catwatch.backend.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.util.Constants;
import org.zalando.catwatch.backend.util.StringParser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(value = Constants.API_RESOURCE_LANGUAGES, produces = {APPLICATION_JSON_VALUE})
@Api(value = Constants.API_RESOURCE_LANGUAGES, description = "the languages API")
public class LanguagesApi {

    @Autowired
    ProjectRepository repository;

    @ApiOperation(
        value = "Project programming language",
        notes =
            "The languages endpoint returns information about the languages used for projects by selected Github Organizations order by the number of projects using the programming language.",
        response = Language.class, responseContainer = "List"
    )
    @ApiResponses(
        value = {
            @ApiResponse(code = 200, message = "An array of programming language used and count of projects using it."),
            @ApiResponse(code = 0, message = "Unexpected error")
        }
    )
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Collection<Language>> languagesGet(
            @ApiParam(value = "List of github.com organizations to scan(comma seperated)", required = true)
            @RequestParam(value = Constants.API_REQUEST_PARAM_ORGANIZATIONS, required = false)
            final String organizations,
            @ApiParam(value = "Number of items to retrieve. Default is 5.")
            @RequestParam(value = Constants.API_REQUEST_PARAM_LIMIT, required = false)
            final Integer limit,
            @ApiParam(value = "Offset the list of returned results by this amount. Default is zero.")
            @RequestParam(value = Constants.API_REQUEST_PARAM_OFFSET, required = false)
            final Integer offset,
            @ApiParam(value = "query paramater for search query (this can be language name prefix)")
            @RequestParam(value = Constants.API_REQUEST_PARAM_Q, required = false)
            final String q) throws NotFoundException {

        List<Language> languages = getMainLanguages(organizations, new LanguagePercentComparator());

        // TODO apply limit

        // TOOD apply q

        // TODO apply offset

        return new ResponseEntity<Collection<Language>>(languages, HttpStatus.OK);
    }

    
    private List<Language> getLanguages(final String organizations, final Comparator<Language> c) {

        Collection<String> organizationList = StringParser.parseStringList(organizations, ",");
        List<Project> projectList = new ArrayList<>();

        // get the projects
        for (String org : organizationList) {

            Iterable<Project> projects = repository.findProjects(org, null, null, null, null, null);

            Iterator<Project> iter = projects.iterator();
            while (iter.hasNext()) {
                projectList.add(iter.next());
            }
        }

        // count the languages

        List<String> languageList = new ArrayList<>();

        for (Project p : projectList) {
            languageList.addAll(p.getLanguageList());
        }

        List<Language> languages = new ArrayList<>();

        Set<String> languageSet = new HashSet<>(languageList);

        int frequency = 0;

        for (String language : languageSet) {
            Language l = new Language(language);
            frequency = Collections.frequency(languageList, language);

            l.setPercentage((int) Math.round(((double) frequency) / languageList.size() * 100));
            l.setProjectsCount(frequency);

            languages.add(l);
        }

        // sort
        if (languages.size() > 1) {
            Collections.sort(languages, c);
        }

        return languages;
    }
    
    
    private List<Language> getMainLanguages(final String organizations, final Comparator<Language> c) {

        Collection<String> organizationList = StringParser.parseStringList(organizations, ",");
        List<Project> projectList = new ArrayList<>();

        // get the projects
        for (String org : organizationList) {

            Iterable<Project> projects = repository.findProjects(org, Optional.ofNullable(null), Optional.ofNullable(null), Optional.ofNullable(null), Optional.ofNullable(null), Optional.ofNullable(null));

            Iterator<Project> iter = projects.iterator();
            while (iter.hasNext()) {
                projectList.add(iter.next());
            }
        }

        // count the languages

        List<String> languageList = new ArrayList<>();

        for (Project p : projectList) {
            languageList.add(p.getPrimaryLanguage());
        }

        List<Language> languages = new ArrayList<>();

        Set<String> languageSet = new HashSet<>(languageList);

        int frequency = 0;

        for (String language : languageSet) {
            Language l = new Language(language);
            frequency = Collections.frequency(languageList, language);

            l.setPercentage((int) Math.round(((double) frequency) / languageList.size() * 100));
            l.setProjectsCount(frequency);

            languages.add(l);
        }

        // sort
        if (languages.size() > 1) {
            Collections.sort(languages, c);
        }

        return languages;
    }

    
    private class LanguagePercentComparator implements Comparator<Language> {

        @Override
        public int compare(final Language l1, final Language l2) {

            if (l1.getPercentage() >= l2.getPercentage()) {
                return 1;
            } else {
                return -1;
            }
        }

    }

}
