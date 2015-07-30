package org.zalando.catwatch.backend.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.zalando.catwatch.backend.model.QStatistics;
import org.zalando.catwatch.backend.model.Statistics;

import com.mysema.query.jpa.impl.JPAQuery;

public class StatisticsRepositoryImpl implements StatisticsRepositoryCustom{

	
    @PersistenceContext
    private EntityManager entityManager;
	
	
    public Optional<Date> getLatestSnaphotDateBefore(final String organization, final Date snapshot) {
        QStatistics statistics = QStatistics.statistics;
        List<Statistics> statisticList = new JPAQuery(entityManager).from(statistics)
                                                               .where((statistics.key.snapshotDate.before(snapshot)
                                                            		   .or(statistics.key.snapshotDate.eq(snapshot))
                                                            		   .and(statistics.organizationName.eq(organization))))
                                                              // .where(statistics.organizationName.eq(organization))
                                                               .orderBy(statistics.key.snapshotDate.desc()).limit(1).list(statistics);
        
        return statisticList.isEmpty() ? Optional.ofNullable(null)
                                     : Optional.ofNullable(statisticList.get(0).getSnapshotDate());
    }
    
    
    public Optional<Date> getEarliestSnaphotDate(final String organization) {
        QStatistics statistics = QStatistics.statistics;
        List<Statistics> statisticList = new JPAQuery(entityManager).from(statistics)
                                                               .where(statistics.organizationName.eq(organization))
                                                               .orderBy(statistics.key.snapshotDate.asc()).limit(1).list(
                                                            		   statistics);
        return statisticList.isEmpty() ? Optional.ofNullable(null)
                                     : Optional.ofNullable(statisticList.get(0).getSnapshotDate());
    }
}
