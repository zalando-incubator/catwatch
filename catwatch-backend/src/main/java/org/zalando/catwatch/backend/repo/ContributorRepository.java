package org.zalando.catwatch.backend.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.zalando.catwatch.backend.model.Contributor;

public interface ContributorRepository extends CrudRepository<Contributor, Long> {

    List<Contributor> findByName(String name);
}
