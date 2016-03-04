package org.zalando.catwatch.backend.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.model.StatisticsKey;

import java.util.Date;
import java.util.List;

public interface StatisticsRepository extends CrudRepository<Statistics, StatisticsKey>, StatisticsRepositoryCustom{
	
	List<Statistics> findByOrganizationName(String name);
	
	/**
	 * Get the most recent statistics of a particular organization.
	 * 
	 * @param name name of the organization
	 * @param pageable 
	 * @return List of statistics.
	 */
	List<Statistics> findByOrganizationNameOrderByKeySnapshotDateDesc(String name, Pageable pageable);
	
	@Query("select s from Statistics s where s.organizationName = ?1 and s.key.snapshotDate between ?2 and ?3 order by s.key.snapshotDate desc")
	List<Statistics> findStatisticsByOrganizationAndDate(String name, Date startDate, Date endDate);

}
