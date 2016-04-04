package org.zalando.catwatch.backend.repo;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.query.DateTimeSubQuery;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.QProject;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final QProject project = QProject.project;

    @Override
    public List<Project> findProjects(String organization, Optional<String> query, Optional<String> language) {
        DateTimeSubQuery<Date> lastSnapshot = new JPASubQuery().from(project)
            .where(project.organizationName.eq(organization))
            .unique(project.snapshotDate.max());

        BooleanBuilder q = new BooleanBuilder()
            .and(project.organizationName.eq(organization))
            .and(project.snapshotDate.eq(lastSnapshot));

        if (query.isPresent()) {
            q.and(project.name.startsWith(query.get()));
        }

        if (language.isPresent()) {
            q.and(project.primaryLanguage.eq(language.get()));
        }

        return queryProject().where(q).list(project);
    }

    @Override
    public List<Project> findProjects(String organization, Date snapshotDate, Optional<String> query, Optional<String> language) {
        BooleanBuilder q = new BooleanBuilder(project.organizationName.eq(organization));

        Optional<Date> snapshotDateMatch = getSnapshotDateMatch(snapshotDate, organization);
        if (!snapshotDateMatch.isPresent()) {
            return Collections.emptyList();
        }

        q.and(project.snapshotDate.eq(snapshotDateMatch.get()));
        if (query.isPresent()) {
            q.and(project.name.startsWith(query.get()));
        }

        if (language.isPresent()) {
            q.and(project.primaryLanguage.eq(language.get()));
        }

        return queryProject().where(q).list(project);
    }

    /**
     * @param snapshot
     *
     * @return date that is closest in the past. If there is no earlier date in
     * the past, nothing is returned
     */
    private Optional<Date> getSnapshotDateMatch(Date snapshot, String organization) {
        return queryProject()
            .where(
                project.organizationName.eq(organization)
                .and(project.snapshotDate.loe(snapshot))
            )
            .orderBy(project.snapshotDate.desc())
            .limit(1)
            .list(project)
            .stream()
            .findFirst()
            .map(Project::getSnapshotDate);
    }

    private JPAQuery queryProject() {
        return new JPAQuery(entityManager).from(project);
    }
}
