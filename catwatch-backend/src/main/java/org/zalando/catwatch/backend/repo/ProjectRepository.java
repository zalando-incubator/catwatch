package org.zalando.catwatch.backend.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.Statistics;

import java.util.Date;
import java.util.List;

public interface ProjectRepository extends CrudRepository<Project,Integer>, ProjectRepositoryCustom {

    @Query("select p from Project p where p.snapshotDate between ?1 and ?2 order by p.snapshotDate desc")
    List<Project> findProjectsByDateRange(Date startDate, Date endDate);
}
