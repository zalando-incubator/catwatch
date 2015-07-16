package org.zalando.catwatch.backend.repo;

import org.springframework.data.repository.CrudRepository;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.model.StatisticsKey;

public interface StatisticsRepository extends CrudRepository<Statistics, StatisticsKey> {

}
