package org.zalando.catwatch.backend.util;

import org.junit.Test;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.repo.builder.ProjectBuilder;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ProjectStatsTest {

    @Test
    public void checkProjectStats() {
        // Generate some date. Two projects, three snapshot timeslots

        LinkedList<Project> projects = new LinkedList<>();
        projects.add(new ProjectBuilder().name("zoo").organizationName("bob")
                .commitsCount(10).forksCount(3).contributorsCount(100).score(1)
                .snapshotDate(new Date(0)).create());
        projects.add(new ProjectBuilder().name("zoo").organizationName("bob")
                .commitsCount(20).forksCount(4).contributorsCount(200).score(2)
                .snapshotDate(new Date(1000L)).create());
        projects.add(new ProjectBuilder().name("zoo").organizationName("bob")
                .commitsCount(25).forksCount(5).contributorsCount(300).score(3)
                .snapshotDate(new Date(2000L)).create());

        projects.add(new ProjectBuilder().name("school").commitsCount(1).forksCount(1).snapshotDate(new Date(0)).create());
        projects.add(new ProjectBuilder().name("school").commitsCount(3).forksCount(2).snapshotDate(new Date(1000L)).create());
        projects.add(new ProjectBuilder().name("school").commitsCount(4).forksCount(3).snapshotDate(new Date(2000L)).create());

        List<ProjectStats> stats = ProjectStats.buildStats(projects);

        assertEquals(2, stats.size());

        int zooIndex = 0;
        int schoolIndex = 1;
        if (!stats.get(0).getName().equals("zoo")) {
            zooIndex = 1;
            schoolIndex = 0;
        }

        ProjectStats zoo = stats.get(zooIndex);
        assertEquals("zoo", zoo.getName());
        assertEquals("bob", zoo.getOrganizationName());
        assertArrayEquals(new Integer[] {10, 20, 25}, zoo.getCommitCounts().toArray());
        assertArrayEquals(new Integer[] {3, 4, 5}, zoo.getForkCounts().toArray());
        assertArrayEquals(new Integer[] {100, 200, 300}, zoo.getContributorsCounts().toArray());
        assertArrayEquals(new Integer[] {1, 2, 3}, zoo.getScores().toArray());
        assertArrayEquals(new Date[] {new Date(0L), new Date(1000L), new Date(2000L)}, zoo.getSnapshotDates().toArray());

        ProjectStats school = stats.get(schoolIndex);
        assertEquals("school", school.getName());
        assertArrayEquals(new Integer[]{1, 3, 4}, school.getCommitCounts().toArray());
        assertArrayEquals(new Integer[] {1, 2, 3}, school.getForkCounts().toArray());
        assertArrayEquals(new Date[] {new Date(0L), new Date(1000L), new Date(2000L)}, school.getSnapshotDates().toArray());
    }
}
