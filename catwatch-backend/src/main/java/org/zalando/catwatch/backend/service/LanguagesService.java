package org.zalando.catwatch.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.zalando.catwatch.backend.model.Language;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.util.StringParser;

public class LanguagesService {

    public static List<Language> getMainLanguages(final String organizations, final Comparator<Language> c, ProjectRepository repository, Optional<String> filterLanguage) {

        Collection<String> organizationList = StringParser.parseStringList(organizations, ",");
        List<Project> projectList = new ArrayList<>();

        // get the projects
        for (String org : organizationList) {

            Iterable<Project> projects = repository.findProjects(org, Optional.ofNullable(null), filterLanguage);

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
    
    public static List<Language> filterLanguages(List<Language> languages, int limit,  int offset){
    	

        return  languages.stream().skip(offset).limit(limit).collect(Collectors.toList());

   }
}
