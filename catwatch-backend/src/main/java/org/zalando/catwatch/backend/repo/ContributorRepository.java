package org.zalando.catwatch.backend.repo;

import org.springframework.data.repository.CrudRepository;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.ContributorKey;

public interface ContributorRepository
		extends CrudRepository<Contributor, ContributorKey>, ContributorRepositoryCustom {

}
