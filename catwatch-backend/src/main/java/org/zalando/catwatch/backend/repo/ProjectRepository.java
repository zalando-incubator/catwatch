package org.zalando.catwatch.backend.repo;

import org.springframework.data.repository.CrudRepository;
import org.zalando.catwatch.backend.model.Project;

public interface ProjectRepository extends CrudRepository<Project, Long> {

}
