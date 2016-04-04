package org.zalando.catwatch.backend.repo;

import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.ContributorKey;

import java.util.Date;
import java.util.List;

public interface ContributorRepositoryCustom {

	/**
	 * @param organizationName
	 *            must not be null EXAMPLE: "zalando"
	 * @return Returns the {@link ContributorKey#getOrganizationId() ID} of the
	 *         organization.
	 */
	Long findOrganizationId(String organizationName);

	/**
	 * @param date
	 *            must not be null
	 * @return Returns the snapshot data is that before the given date. If there
	 *         are more than one, the latest is chosen. Returns null if there is
	 *         none.
	 */
	Date findPreviousSnapShotDate(Date date);

	/**
	 * Used to find the top contributors (always sorted by all-time number of
	 * commits).
	 * <p>
	 * It is possible to fetch more data (for infinite scrolling).
	 * 
	 * @param organizationId
	 * @param snapshotDate
	 *            Must not be null.
	 * @param namePrefix
	 *            the prefix is used to filter by contributor name.
	 *            Case-sensitive. EXAMPLE: prefix "joh" will match the name
	 *            "John".
	 * @param offset
	 *            the rows to skip
	 * @param limit
	 *            the maximum number of returned results
	 * @param namePrefix
	 *            the prefix is used to filter by contributor name.
	 *            Case-sensitive. EXAMPLE: prefix "joh" will match the name
	 *            "John".
	 * @return Returns the contributors.
	 */
	List<Contributor> findAllTimeTopContributors(Long organizationId, Date snapshotDate, String namePrefix,
			Integer offset, Integer limit);

	/**
	 * Returns all contributor data that are found for the given time span. The
	 * returned data is "two-dimensional", i.e. for each contributor and each
	 * snapshot date a row is returned.
	 * <p>
	 * The returned data is sorted by time descendingly, then by contributor key
	 * ID.
	 * 
	 * @param organizationId
	 *            EXAMPLE: 123
	 * @param startDate
	 *            this date is used to exclude rows that have an earlier
	 *            snapshot date (including this date)
	 * @param endDate
	 *            this date is used to exclude rows that have a later snapshot
	 *            date (including this date)
	 * @param namePrefix
	 *            the prefix is used to filter by contributor name.
	 *            Case-sensitive. EXAMPLE: prefix "joh" will match the name
	 *            "John".
	 * @return Returns the contributors.
	 */
	List<Contributor> findContributorsTimeSeries(Long organizationId, Date startDate, Date endDate, String namePrefix);

}
