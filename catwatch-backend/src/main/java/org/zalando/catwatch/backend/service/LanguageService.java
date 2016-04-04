package org.zalando.catwatch.backend.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.util.StringParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LanguageService {

    public static final Logger logger = LoggerFactory.getLogger(LanguageService.class);

	private final ProjectRepository repository;

    @Autowired
    public LanguageService(ProjectRepository repository) {
        this.repository = repository;
    }

    public List<Language> filterLanguages(List<Language> languages, int limit,  int offset){
         return  languages.stream().skip(offset).limit(limit).collect(Collectors.toList());
    }
    
    public List<Language> getMainLanguages(final String organizations, final Comparator<Language> c, Optional<String> filterLanguage) {

        Collection<String> organizationList = StringParser.parseStringList(organizations, ",");
        List<Project> projectList = new ArrayList<>();

        // get the projects
        for (String org : organizationList) {

            Iterable<Project> projects = repository.findProjects(org, Optional.empty(), filterLanguage);

            for (Project project : projects) {
                projectList.add(project);
            }
        }

        // count the languages

        List<String> languageList = new ArrayList<>();

        for (Project p : projectList) {
            if (StringUtils.isEmpty(p.getPrimaryLanguage())) {
                logger.info(String.format("No primary programming language set for project [%s].", p.getName()));
                continue;
            }

            languageList.add(p.getPrimaryLanguage());
        }

        List<Language> languages = new ArrayList<>();

        Set<String> languageSet = new HashSet<>(languageList);

        int frequency;

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
	
}
