package org.zalando.catwatch.backend.repo;

import org.springframework.data.repository.CrudRepository;
import org.zalando.catwatch.backend.model.Statistics;

public interface StatisticsRepository extends CrudRepository<Statistics, Long> {

}
