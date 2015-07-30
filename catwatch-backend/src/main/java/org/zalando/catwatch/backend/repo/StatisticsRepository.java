package org.zalando.catwatch.backend.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.model.StatisticsKey;

public interface StatisticsRepository extends CrudRepository<Statistics, StatisticsKey>, StatisticsRepositoryCustom{
	
	List<Statistics> findByOrganizationName(String name);
	
	/**get the most recent statistics of a particular organization
	 * 
	 * @param name name of the organization
	 * @param pageable 
	 * @return List of statistics
	 */
	List<Statistics> findByOrganizationNameOrderByKeySnapshotDateDesc(String name, Pageable pageable);
	
	//get the statistics objects of an organization of a given time frame
	//List<Statistics> findByOrganizationNameAndSnapshotDateAfterAndSnapshotDateBeforeOrderBySnapshotDateDesc(String name, Date startDate, Date endDate);
	//List<Statistics> findByOrganizationNameAndKeySnapshotDateAfterAndKeySnapshotDateBeforeOrderByKeySnapshotDateDesc(String name, Date startDate, Date endDate);
	
	@Query("select s from Statistics s where s.organizationName = ?1 and s.key.snapshotDate between ?2 and ?3 order by s.key.snapshotDate desc")
	List<Statistics> findStatisticsByOrganizationAndDate(String name, Date startDate, Date endDate);
	
}
