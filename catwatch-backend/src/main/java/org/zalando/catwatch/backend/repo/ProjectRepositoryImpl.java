package org.zalando.catwatch.backend.repo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.QProject;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;

/**
 * Created by mkunz on 7/22/15.
 */
class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Project> findProjects(final String organization, final Optional<String> query,
            final Optional<String> language) {
        JPAQuery jpaQuery = new JPAQuery(entityManager);
        QProject project = QProject.project;

        BooleanBuilder booleanBuilder = new BooleanBuilder().and(project.organizationName.eq(organization));

        QProject projectSubquery = QProject.project;

        booleanBuilder = booleanBuilder.and(project.snapshotDate.eq(new JPASubQuery().from(projectSubquery).unique(
                        projectSubquery.snapshotDate.max())));
        if (query.isPresent()) {
            booleanBuilder = booleanBuilder.and(project.name.startsWith(query.get()));
        }

        if (language.isPresent()) {
            booleanBuilder = booleanBuilder.and(project.primaryLanguage.eq(language.get()));
        }

        return jpaQuery.from(project).where(booleanBuilder).list(project);
    }

    @Override
    public List<Project> findProjects(final String organization, final Date snapshotDate, final Optional<String> query,
            final Optional<String> language) {
        JPAQuery jpaQuery = new JPAQuery(entityManager);
        QProject project = QProject.project;
        BooleanBuilder booleanBuilder = new BooleanBuilder().and(project.organizationName.eq(organization));
        Optional<Date> snapshotDateMatch = getSnapshotDateMatch(snapshotDate, organization);
        if (snapshotDateMatch.isPresent()) {
            booleanBuilder.and(project.snapshotDate.eq(snapshotDateMatch.get()));
            if (query.isPresent()) {
                booleanBuilder = booleanBuilder.and(project.name.startsWith(query.get()));
            }

            if (language.isPresent()) {
                booleanBuilder = booleanBuilder.and(project.primaryLanguage.eq(language.get()));
            }

            return jpaQuery.from(project).where(booleanBuilder).list(project);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @param   snapshot
     *
     * @return  date that is closest in the past. If there is no earlier date in the past, nothing is returned
     */
    private Optional<Date> getSnapshotDateMatch(final Date snapshot, final String organization) {
        QProject project = QProject.project;
        List<Project> projectList = new JPAQuery(entityManager).from(project)
                                                               .where(project.organizationName.eq(organization).and(
                                                                       project.snapshotDate.eq(snapshot).or(
                                                                           project.snapshotDate.before(snapshot))))
                                                               .orderBy(project.snapshotDate.desc()).limit(1).list(
                                                                   project);
        return projectList.isEmpty() ? Optional.ofNullable(null)
                                     : Optional.ofNullable(projectList.get(0).getSnapshotDate());
    }

}
