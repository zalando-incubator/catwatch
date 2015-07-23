package org.zalando.catwatch.backend.repo;

import java.util.Date;
import java.util.List;

import org.zalando.catwatch.backend.model.Contributor;

public interface ContributorRepositoryCustom {

	/**
	 * Used to find the top contributors (always sorted by all-time number of
	 * commits).
	 * <p>
	 * It is possible to fetch more data (for infinite scrolling).
	 * 
	 * @param organizationId
	 * @param snapshotDate Must not be null.
	 * @param offset
	 *            the rows to skip
	 * @param limit
	 *            the maximum number of returned results
	 * @return Returns the contributors.
	 */
	List<Contributor> findAllTimeTopContributors(Long organizationId, Date snapshotDate, Integer offset, Integer limit);



	/**
	 * Returns all contributor data that are found for the given time span. The
	 * returned data is "two-dimensional", i.e. for each contributor and each
	 * snapshot date a row is returned.
	 * <p>
	 * The returned data is sorted by time descendingly, then by contributor key ID.
	 * 
	 * @param organizationId EXAMPLE: 123
	 * @param startDate this date is used to exclude rows that have an earlier snapshot date (including this date)
	 * @param endDate this date is used to exclude rows that have a later snapshot date (including this date)
	 * @param namePrefix the prefix is used to filter by contributor name. EXAMPLE: prefix "joh" will match the name "John".
	 * @return Returns the contributors.
	 */
	List<Contributor> findContributorsTimeSeries(Long organizationId, Date startDate, Date endDate, String namePrefix);

}
