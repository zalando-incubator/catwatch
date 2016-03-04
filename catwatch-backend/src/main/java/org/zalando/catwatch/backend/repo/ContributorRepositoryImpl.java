package org.zalando.catwatch.backend.repo;

import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.ContributorKey;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.Instant.now;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.util.Date.from;

public class ContributorRepositoryImpl implements ContributorRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Long findOrganizationId(String organizationName) {

		checkNotNull(organizationName, "organizationName must not be null but was");

		@SuppressWarnings("unchecked")
		List<Long> results = em
				.createQuery("select c.key.organizationId from Contributor c " //
						+ " where c.organizationName = :organizationName") //
				.setParameter("organizationName", organizationName).setMaxResults(1) //
				.getResultList();
		return results.size() > 0 ? results.get(0) : -1L;
	}

	@Override
	public Date findPreviousSnapShotDate(Date snapshotDate) {

		if (snapshotDate == null) {
			// choose some date far away in the future
			snapshotDate = from(ofInstant(now(), systemDefault()).plus(10, YEARS).toInstant(UTC));
		}

		@SuppressWarnings("unchecked")
		List<Date> results = em
				.createQuery("select c.key.snapshotDate from Contributor c " //
						+ " where c.key.snapshotDate <= :date " //
						+ " order by c.key.snapshotDate desc") //
				.setParameter("date", snapshotDate).setMaxResults(1) //
				.getResultList();

		return results.size() > 0 ? results.get(0) : null;
	}

	@Override
	public List<Contributor> findAllTimeTopContributors(Long organizationId, Date snapshotDate, String namePrefix,
			Integer offset, Integer limit) {

		checkNotNull(snapshotDate, "snapshot date must not be null but was");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contributor> cq = cb.createQuery(Contributor.class);

		// define "from" and joins
		Root<Contributor> contributor = cq.from(Contributor.class);
		Path<ContributorKey> key = contributor.get("key");

		// define constraints
		List<Predicate> andPredicates = new ArrayList<>();
		{
			if (organizationId != null) {
				andPredicates.add(cb.equal(key.get("organizationId"), organizationId));
			}

			andPredicates.add(cb.equal(key.<Date> get("snapshotDate"), snapshotDate));

			if (namePrefix != null) {
				andPredicates.add(cb.like(contributor.get("name"), namePrefix.replace("%", "[%]") + "%"));
			}
		}

		return em
				.createQuery(cq //
						.select(contributor) //
						.where(andPredicates.toArray(new Predicate[andPredicates.size()])) //
						.orderBy(cb.desc(contributor.get("organizationalCommitsCount"))))
				.setFirstResult(offset == null ? 0 : offset) //
				.setMaxResults(limit == null ? 10000000 : limit) //
				.getResultList();
	}

	@Override
	public List<Contributor> findContributorsTimeSeries(Long organizationId, Date startDate, Date endDate,
			String namePrefix) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contributor> cq = cb.createQuery(Contributor.class);

		// define "from" and joins
		Root<Contributor> contributor = cq.from(Contributor.class);
		Path<ContributorKey> key = contributor.get("key");

		// define constraints
		List<Predicate> andPredicates = new ArrayList<>();
		{
			if (organizationId != null) {
				andPredicates.add(cb.equal(key.get("organizationId"), organizationId));
			}
			if (startDate != null && endDate != null) {
				andPredicates.add(cb.between(key.<Date> get("snapshotDate"), startDate, endDate));
			} else if (startDate != null) {
				andPredicates.add(cb.greaterThanOrEqualTo(key.<Date> get("snapshotDate"), startDate));
			} else if (endDate != null) {
				andPredicates.add(cb.lessThanOrEqualTo(key.<Date> get("snapshotDate"), endDate));
			}
			if (namePrefix != null) {
				andPredicates.add(cb.like(contributor.get("name"), namePrefix.replace("%", "[%]") + "%"));
			}
		}

		return em.createQuery(cq //
				.select(contributor) //
				.where(andPredicates.toArray(new Predicate[andPredicates.size()])) //
				.orderBy(cb.desc(key.get("snapshotDate")), cb.desc(key.get("id")))).getResultList();
	}
}
