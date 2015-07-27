package org.zalando.catwatch.backend.repo;

import java.util.Date;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.QProject;
import org.zalando.catwatch.backend.model.sort.ProjectSortColumn;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.ComparableExpressionBase;

/**
 * Created by mkunz on 7/22/15.
 */
class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String SORT_ORDER_DESC = "-";

    private static final long DEFAULT_LIMIT = 5;

    @Override
    public Iterable<Project> findProjects(final String organization, final Optional<Integer> limit,
            final Optional<Integer> offset, final Optional<Date> snapshotDate, final Optional<String> sortBy,
            final Optional<String> query) {
        JPAQuery jpaQuery = new JPAQuery(entityManager);
        QProject project = QProject.project;

        BooleanBuilder booleanBuilder = new BooleanBuilder().and(project.organizationName.eq(organization));

        if (snapshotDate.isPresent()) {
            booleanBuilder = booleanBuilder.and(project.snapshotDate.eq(snapshotDate.get()));
        } else {
            QProject projectSubquery = QProject.project;
            booleanBuilder = booleanBuilder.and(project.snapshotDate.eq(new JPASubQuery().from(projectSubquery).unique(
                            projectSubquery.snapshotDate.max())));
        }

        JPAQuery base = jpaQuery.from(project).where(booleanBuilder);

        base = getJpaQueryWithOffsetAndLimit(limit, offset, base);

        base = getJpaQueryWithOrderBy(sortBy, project, base);

        return base.list(project);
    }

    private JPAQuery getJpaQueryWithOrderBy(final Optional<String> sortBy, final QProject project, JPAQuery base) {
        if (sortBy.isPresent()) {
            String sortColumn = sortBy.get();
            if (sortColumn.startsWith(SORT_ORDER_DESC)) {
                ComparableExpressionBase<? extends Comparable> comparableExpressionBase = getSortPath(project,
                        sortColumn.substring(1));
                base = base.orderBy(comparableExpressionBase.desc());
            } else {
                ComparableExpressionBase<?> comparableExpressionBase = getSortPath(project, sortColumn);
                base = base.orderBy(comparableExpressionBase.asc());
            }
        } else {
            base = base.orderBy(project.commitsCount.desc());
        }

        return base;
    }

    private ComparableExpressionBase<?> getSortPath(final QProject project, final String sortBy) {
        switch (sortBy) {

            case ProjectSortColumn.STARS_COUNT :
                return project.starsCount;

            case ProjectSortColumn.SCORE :
                return project.score;

            case ProjectSortColumn.COMMITS_COUNT :
                return project.commitsCount;

            case ProjectSortColumn.FORKS_COUNT :
                return project.forksCount;

            case ProjectSortColumn.CONTRIBUTION_COUNT :
                return project.contributorsCount;

            default :
        }

        throw new IllegalArgumentException("no sorting defined for sort attribute: " + sortBy);
    }

    private JPAQuery getJpaQueryWithOffsetAndLimit(final Optional<Integer> limit, final Optional<Integer> offset,
            JPAQuery base) {
        if (limit.isPresent()) {
            base = base.limit(limit.get());
        } else {
            base = base.limit(DEFAULT_LIMIT);
        }

        if (offset.isPresent()) {
            base = base.offset(offset.get());
        }

        return base;
    }

}
