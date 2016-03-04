package org.zalando.catwatch.backend.util;

import com.google.common.collect.Lists;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.zalando.catwatch.backend.model.Project;

import java.util.Date;
import java.util.List;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class LanguageStatsTest {

    private static final String JAVA = "java";
    private static final String PYTHON = "python";

    @Test
    public void thatNullLanguageNameResultsInUnknownLanguage() {
        LanguageStats languageStats = new LanguageStats(null, null, null);
        assertThat(languageStats.getLanguageName(), is("unknown"));
    }

    @Test
    public void thatStatsAreBuiltForEmptyProjects() {
        assertThat(LanguageStats.buildStats(Lists.newArrayList()), empty());
    }

    @Test
    public void thatStatsAreBuildForSingleProjectWithoutLanguageName() {
        Project project = new Project();
        project.setSnapshotDate(new Date());

        List<LanguageStats> listOfLanguageStats = LanguageStats.buildStats(Lists.newArrayList(project));

        assertThat(listOfLanguageStats.size(), is(1));

        LanguageStats languageStats = listOfLanguageStats.get(0);
        assertThat(languageStats.getLanguageName(), is("unknown"));

        List<Integer> projectCounts = languageStats.getProjectCounts();
        assertThat(projectCounts.size(), is(1));
        assertThat(projectCounts.get(0), is(1));
    }

    @Test
    public void thatStatsAreBuildForTwoProjectsOfDifferentLanguageAndSameSnapshotDate() {
        Date snapshotDate = new Date();

        Project javaProject = new Project();
        javaProject.setName("Project 1");
        javaProject.setPrimaryLanguage(JAVA);
        javaProject.setSnapshotDate(snapshotDate);

        Project pythonProject = new Project();
        pythonProject.setName("Project 2");
        pythonProject.setPrimaryLanguage(PYTHON);
        pythonProject.setSnapshotDate(snapshotDate);

        List<LanguageStats> listOfLanguageStats = LanguageStats.buildStats(Lists.newArrayList(javaProject, pythonProject));

        assertThat(listOfLanguageStats.size(), is(2));

        assertThat(listOfLanguageStats,
                hasItem(new LanguageStatsMatcher(JAVA, Lists.newArrayList(1), Lists.newArrayList(snapshotDate))));

        assertThat(listOfLanguageStats,
                hasItem(new LanguageStatsMatcher(PYTHON, Lists.newArrayList(1), Lists.newArrayList(snapshotDate))));
    }

    @Test
    public void thatDuplicateProjectsAreFiltered() {
        Date snapshotDate = new Date();

        Project javaProject = new Project();
        javaProject.setName("Project 1");
        javaProject.setPrimaryLanguage(JAVA);
        javaProject.setSnapshotDate(snapshotDate);

        Project duplicateProject = new Project();
        duplicateProject.setName("Project 1");
        duplicateProject.setPrimaryLanguage(JAVA);
        duplicateProject.setSnapshotDate(snapshotDate);

        List<LanguageStats> listOfLanguageStats = LanguageStats.buildStats(Lists.newArrayList(javaProject, duplicateProject));

        assertThat(listOfLanguageStats.size(), is(1));
    }

    private class LanguageStatsMatcher extends TypeSafeMatcher<LanguageStats> {

        private String languageName;
        private List<Integer> projectCounts;
        private List<Date> snapshotDates;

        LanguageStatsMatcher(String languageName, List<Integer> projectCounts, List<Date> snapshotDates) {
            this.languageName = languageName;
            this.projectCounts = projectCounts;
            this.snapshotDates = snapshotDates;
        }

        @Override
        protected boolean matchesSafely(LanguageStats languageStats) {
            return languageName.equals(languageStats.getLanguageName()) &&
                    projectCounts.equals(languageStats.getProjectCounts()) &&
                    snapshotDates.equals(languageStats.getSnapshotDates());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("projectCounts " + projectCounts +
                    " and snapshotDates " + snapshotDates +
                    " and languageName " + languageName);
        }
    }
}