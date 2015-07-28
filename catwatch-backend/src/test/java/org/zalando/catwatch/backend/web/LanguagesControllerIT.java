package org.zalando.catwatch.backend.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.populate.BuilderUtil;
import org.zalando.catwatch.backend.repo.populate.ProjectBuilder;

public class LanguagesControllerIT extends AbstractCatwatchIT {

    private final String LANG_JAVA = "Java", LANG_JS = "JS", LANG_HTML = "HTML5", LANG_CSS = "CSS", LANG_PY = "Python",
        LANG_CPP = "C++", LANG_GO = "Go", LANG_SCALA = "Scala", LANG_GROOVY = "Groovy", LANG_CSH = "C#",
        LANG_CLO = "Clojure", LANG_VB = "VB", LANG_OC = "OjectiveC";

    @Autowired
    ProjectRepository repository;

    @Test
    public void testgetLanguages() {

        // when
        this.repository.deleteAll();

        List<String> languages = fillRepositoryRandom(10);

// Collections.sort TODO

    }

    @Test
    public void testGetLanguagesWithOffsetAndLimit() { }

    private List<String> fillRepositoryRandom(final int nrOfProjects) {

        List<String> languages = new ArrayList<>();

        String lang;

        for (int i = 0; i < nrOfProjects; i++) {
            lang = BuilderUtil.randomLanguage();
            new ProjectBuilder(repository, new Date(), null, null, null, null, null, null, null, null).primaryLanguage(
                lang).save();
            languages.add(lang);
        }

        return languages;
    }

    private class LanguageComparator implements Comparator<String> {

        Collection<String> stringCollection;

        public LanguageComparator(final Collection<String> strings) {
            this.stringCollection = strings;
        }

        @Override
        public int compare(final String o1, final String o2) {

            int f1 = Collections.frequency(stringCollection, o1);

            int f2 = Collections.frequency(stringCollection, o2);

            if (f1 >= f2) {
                return 1;
            }

            return -1;
        }

    }
}
