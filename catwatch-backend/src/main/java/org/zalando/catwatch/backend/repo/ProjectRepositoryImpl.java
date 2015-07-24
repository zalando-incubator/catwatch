package org.zalando.catwatch.backend.repo;

import java.util.Date;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Sort;

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
    public Iterable<Project> findProjects(final String organization, final Optional<Integer> limit,
            final Optional<Integer> offset, final Optional<Date> snapshotDate, final Optional<String> sortBy,
            final Optional<String> query) {
        JPAQuery jpaQuery = new JPAQuery(entityManager);
        QProject project = QProject.project;
        QProject projectSubselect = QProject.project;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(project.organizationName.eq(organization));

        if (snapshotDate.isPresent()) {
            booleanBuilder.and(project.snapshotDate.eq(snapshotDate.get()));
        } else {
            booleanBuilder.and(project.snapshotDate.eq(new JPASubQuery().from(projectSubselect).unique(
                        projectSubselect.snapshotDate.max())));
        }

        JPAQuery base = jpaQuery.from(project).where(booleanBuilder);

        base = getJpaQueryWithOffsetAndLimit(limit, offset, base);

        base = getJpaQueryWithOrderBy(sortBy, project, base);

        return base.list(project);
    }

    private JPAQuery getJpaQueryWithOrderBy(final Optional<String> sortBy, final QProject project, JPAQuery base) {
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (sortBy.isPresent()) {
            // TODO add implementation for the default sort direction
        } else {
            base = base.orderBy(project.commitsCount.desc());
        }

        return base;
    }

    private JPAQuery getJpaQueryWithOffsetAndLimit(final Optional<Integer> limit, final Optional<Integer> offset,
            JPAQuery base) {
        if (limit.isPresent()) {
            base = base.limit(limit.get());
        } else {
            base = base.limit(5);
        }

        if (offset.isPresent()) {
            base = base.offset(offset.get());
        }

        return base;
    }

    private Sort.Direction getSortDirection(final String sortBy) {
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (sortBy.startsWith("-")) {
            sortDirection = Sort.Direction.DESC;
        }

        return sortDirection;
    }

}
