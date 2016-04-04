package org.zalando.catwatch.backend.util;

import org.junit.Test;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.repo.builder.ContributorBuilder;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.zalando.catwatch.backend.repo.builder.BuilderUtil.freshId;

public class ContributorStatsTest{
    @Test
    public void checkContributorStats(){
        // Generate a list of contributors
        LinkedList<Contributor> contributors = new LinkedList<>();
        String gitHub = "https://github.com/";

        contributors.add(new ContributorBuilder().name("elephant").url(gitHub+"elephant").organizationId(freshId())
                .organizationName("bob").snapshotDate(new Date(0))
                .orgCommits(40).persCommits(30).orgProjects(20).persProjects(10).create());
        contributors.add(new ContributorBuilder().name("elephant").url(gitHub+"elephant").organizationId(freshId())
                .organizationName("bob").snapshotDate(new Date(1))
                .orgCommits(40).persCommits(30).orgProjects(20).persProjects(10).create());
        contributors.add(new ContributorBuilder().name("elephant").url(gitHub + "elephant").organizationId(freshId())
                .organizationName("bob").snapshotDate(new Date(2))
                .orgCommits(40).persCommits(30).orgProjects(20).persProjects(10).create());

        contributors.add(new ContributorBuilder().name("snake").url(gitHub + "snake").organizationId(freshId())
                .organizationName("bob").snapshotDate(new Date(0))
                .orgCommits(40).persCommits(30).orgProjects(20).persProjects(10).create());
        contributors.add(new ContributorBuilder().name("snake").url(gitHub + "snake").organizationId(freshId())
                .organizationName("bob").snapshotDate(new Date(2))
                .orgCommits(40).persCommits(30).orgProjects(20).persProjects(10).create());
        contributors.add(new ContributorBuilder().name("snake").url(gitHub + "snake").organizationId(freshId())
                .organizationName("bob").snapshotDate(new Date(3))
                .orgCommits(40).persCommits(30).orgProjects(20).persProjects(10).create());

        // Construct a ContributorStats object
        List<ContributorStats> stats = ContributorStats.buildStats(contributors);
        assertEquals(2, stats.size());

        assertEquals("snake", stats.get(0).getName());
        assertEquals("bob", stats.get(0).getOrganizationName().get(0));
        assertArrayEquals(new Integer[] {40, 40, 40}, stats.get(0).getOrganizationalCommitsCounts().toArray());
        assertArrayEquals(new Integer[] {30, 30, 30}, stats.get(0).getPersonalCommitsCounts().toArray());
        assertArrayEquals(new Integer[] {20, 20, 20}, stats.get(0).getOrganizationalProjectsCounts().toArray());
        assertArrayEquals(new Integer[] {10, 10, 10}, stats.get(0).getPersonalProjectsCounts().toArray());
        stats.get(0).getSnapshotDates().toArray();
    }

    @Test
    public void checkLoginId() {
        Contributor c = new ContributorBuilder().name("elephant").organizationId(freshId())
                .organizationName("bob").snapshotDate(new Date(0))
                .orgCommits(40).persCommits(30).orgProjects(20).persProjects(10).create();
        c.setUrl(null);
        assertEquals("", c.getLoginId());

        c.setUrl("notGoodStart");
        assertEquals("", c.getLoginId());

        c.setUrl("https://github.com/zalando");
        assertEquals("zalando", c.getLoginId());
    }
}
