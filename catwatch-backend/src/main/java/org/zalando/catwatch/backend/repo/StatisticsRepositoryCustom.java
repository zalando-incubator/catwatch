package org.zalando.catwatch.backend.repo;

import java.util.Date;
import java.util.Optional;

public interface StatisticsRepositoryCustom {

	/**
     * @param   date
     *
     * @return  Date of the snapshot that is closest in the past of the given date. If there is no earlier date in the past, nothing is returned
     */
	Optional<Date> getLatestSnaphotDateBefore(String organization, Date date);
	
	Optional<Date> getEarliestSnaphotDate(String organization);
}
