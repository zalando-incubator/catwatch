package org.zalando.catwatch.backend.repo;

import org.springframework.data.repository.CrudRepository;
import org.zalando.catwatch.backend.model.Project;
import org.zalando.catwatch.backend.model.ProjectKey;

public interface ProjectRepository extends CrudRepository<Project, ProjectKey> {

}
