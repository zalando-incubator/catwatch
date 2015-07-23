package org.zalando.catwatch.backend.repo;

import java.util.Date;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.QProject;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;

/**
 * Created by mkunz on 7/22/15.
 */
class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Iterable<Project> findProjects(final String organization, final Optional<Integer> limit,
            final Optional<Integer> offset, final Optional<Date> startDate, final Optional<Date> endDate,
            final Optional<String> sortBy, final Optional<String> query) {
        JPAQuery jpaQuery = new JPAQuery(entityManager);
        QProject project = QProject.project;
        QProject projectSubselect = QProject.project;

        // sample implementation using query dsl
        return getProjectsWithoutDate(organization, jpaQuery, project, projectSubselect);
    }

    private Iterable<Project> getProjectsWithoutDate(final String organization, final JPAQuery jpaQuery,
            final QProject project, final QProject projectSubselect) {
        return jpaQuery.from(project)
                       .where(project.snapshotDate.eq(new JPASubQuery().from(projectSubselect).unique(
                                   projectSubselect.snapshotDate.max())), project.organizationName.eq(organization))
                       .list(project);
    }

}
