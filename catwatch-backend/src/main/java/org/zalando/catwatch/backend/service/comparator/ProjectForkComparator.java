package org.zalando.catwatch.backend.service.comparator;

import org.zalando.catwatch.backend.model.Project;

import java.util.Comparator;

public class ProjectForkComparator implements Comparator<Project> {

    @Override
    public int compare(Project p1, Project p2) {
        return p1.getForksCount()-p2.getForksCount();
    }
}
