package org.zalando.catwatch.backend.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.ContributorKey;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ContributorRepository
		extends CrudRepository<Contributor, ContributorKey>, ContributorRepositoryCustom {

    @Query("select c from Contributor c where c.key.snapshotDate between ?2 and ?3 and c.organizationName in ?1"
            + " order by c.key.snapshotDate")
    List<Contributor> findContributorsByOrganizationAndDate(Collection<String> orgs, Date startDate, Date endDate);
}
