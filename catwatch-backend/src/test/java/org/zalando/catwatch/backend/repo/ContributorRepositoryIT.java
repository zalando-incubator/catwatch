package org.zalando.catwatch.backend.repo;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.catwatch.backend.CatWatchBackendApplication;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.repo.populate.ContributorBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CatWatchBackendApplication.class)
public class ContributorRepositoryIT {

	@Autowired
	ContributorRepository repository;

	public ContributorBuilder newContributor() {
		return new ContributorBuilder(repository);
	}

	@Test
	public void findAllTimeTopContributors_FilterBySnapshotDate() throws Exception {

		// given
		repository.deleteAll();
		Contributor ca1 = newContributor().days(1).orgCommits(23).save();
		Contributor ca2 = newContributor().days(2).orgCommits(20).id(ca1.getId()).save();
		Contributor cb1 = newContributor().days(1).orgCommits(22).save();
		Contributor cb2 = newContributor().days(2).orgCommits(21).id(cb1.getId()).save();

		// when
		List<Contributor> contributors = repository.findAllTimeTopContributors(null, ca1.getSnapshotDate(), null, null,
				null);

		// then
		assertContributors(contributors, ca1, cb1);

		// when
		contributors = repository.findAllTimeTopContributors(null, ca2.getSnapshotDate(), null, null, null);

		// then
		assertContributors(contributors, cb2, ca2);

		// when
		try {
			contributors = repository.findAllTimeTopContributors(null, null, null, null, null);
		} catch (NullPointerException e) {
			// then
			assertThat(e.getMessage(), containsString("snapshot date must not be null but was"));
		}
	}

	@Test
	public void findAllTimeTopContributors_FilterByOrganizationId() throws Exception {

		// given
		repository.deleteAll();
		Contributor ca1 = newContributor().days(1).orgCommits(23).save();
		Contributor cb1 = newContributor().days(1).orgCommits(20).save();
		/* no var ass. */ newContributor().days(2).orgCommits(20).organizationId(ca1.getOrganizationId()).save();

		// when
		List<Contributor> contributors = repository.findAllTimeTopContributors(ca1.getOrganizationId(),
				ca1.getSnapshotDate(), null, null, null);

		// then
		assertContributors(contributors, ca1);

		// when
		contributors = repository.findAllTimeTopContributors(null, ca1.getSnapshotDate(), null, null, null);

		// then
		assertContributors(contributors, ca1, cb1);
	}

	@Test
	public void findAllTimeTopContributors_FilterByNamePrefix() throws Exception {

		// given
		repository.deleteAll();
		Contributor c = newContributor().name("John").save();

		// when
		List<Contributor> contributors = repository.findAllTimeTopContributors(null, c.getSnapshotDate(), "Joh", null,
				null);

		// then
		assertContributors(contributors, c);

		// when
		contributors = repository.findAllTimeTopContributors(null, c.getSnapshotDate(), "John", null, null);

		// then
		assertContributors(contributors, c);

		// when
		contributors = repository.findAllTimeTopContributors(null, c.getSnapshotDate(), "Jah", null, null);

		// then
		assertContributors(contributors);

		// when
		contributors = repository.findAllTimeTopContributors(null, c.getSnapshotDate(), "Johnny", null, null);

		// then
		assertContributors(contributors);
	}

	@Test
	public void findAllTimeTopContributors_FilterByNamePrefix_EdgeCases() throws Exception {

		// given
		repository.deleteAll();
		Contributor c = newContributor().name("abcxyz").save();

		// when
		List<Contributor> contributors = repository.findAllTimeTopContributors(null, c.getSnapshotDate(), "%xyz", null,
				null);

		// then
		assertContributors(contributors);
	}

	@Test
	public void findAllTimeTopContributors_Pagination() throws Exception {

		// given
		repository.deleteAll();
		Contributor ca = newContributor().days(1).orgCommits(23).save();
		Contributor cb = newContributor().days(1).orgCommits(22).save();
		Contributor cc = newContributor().days(1).orgCommits(21).save();
		Contributor cd = newContributor().days(1).orgCommits(20).save();

		// when
		List<Contributor> contributors = repository.findAllTimeTopContributors(null, ca.getSnapshotDate(), null, 0, 2);

		// then
		assertContributors(contributors, ca, cb);

		// when
		contributors = repository.findAllTimeTopContributors(null, ca.getSnapshotDate(), null, 1, 2);

		// then
		assertContributors(contributors, cb, cc);

		// when
		contributors = repository.findAllTimeTopContributors(null, ca.getSnapshotDate(), null, 3, 2);

		// then
		assertContributors(contributors, cd);

		// when
		contributors = repository.findAllTimeTopContributors(null, ca.getSnapshotDate(), null, 4, 2);

		// then
		assertContributors(contributors);

		// when
		contributors = repository.findAllTimeTopContributors(null, ca.getSnapshotDate(), null, null, 2);

		// then
		assertContributors(contributors, ca, cb);

		// when
		contributors = repository.findAllTimeTopContributors(null, ca.getSnapshotDate(), null, 1, null);

		// then
		assertContributors(contributors, cb, cc, cd);
	}

	@Test
	public void findAllTimeTopContributors_SortByOrganizationalCommitsCountDesc() throws Exception {

		// given
		repository.deleteAll();
		Contributor ca = newContributor().days(1).orgCommits(22).save();
		Contributor cb = newContributor().days(1).orgCommits(23).save();

		// when
		List<Contributor> contributors = repository.findAllTimeTopContributors(null, ca.getSnapshotDate(), null, null,
				null);

		// then
		assertContributors(contributors, cb, ca);
	}

	@Test
	public void findContributorsTimeSeries_FilterByOrganization() throws Exception {

		// given
		repository.deleteAll();
		Contributor c1 = newContributor().organizationId(345).days(1).save();
		Contributor c2 = newContributor().organizationId(345).days(2).save();

		// when
		List<Contributor> contributors = repository.findContributorsTimeSeries(c1.getOrganizationId(), null, null,
				null);

		// then
		assertContributors(contributors, c1, c2);

		// when
		contributors = repository.findContributorsTimeSeries(c1.getOrganizationId() + 1, null, null, null);

		// then
		assertContributors(contributors);

	}

	@Test
	public void findContributorsTimeSeries_FilterByNamePrefix() throws Exception {

		// given
		repository.deleteAll();
		Contributor c1 = newContributor().name("John").save();

		// when
		List<Contributor> contributors = repository.findContributorsTimeSeries(null, null, null, "Joh");

		// then
		assertContributors(contributors, c1);

		// when
		contributors = repository.findContributorsTimeSeries(null, null, null, "John");

		// then
		assertContributors(contributors, c1);

		// when
		contributors = repository.findContributorsTimeSeries(null, null, null, null);

		// then
		assertContributors(contributors, c1);

		// when
		contributors = repository.findContributorsTimeSeries(null, null, null, "Jah");

		// then
		assertContributors(contributors); // prefix does not match

		// when
		contributors = repository.findContributorsTimeSeries(null, null, null, "joh");

		// then
		assertContributors(contributors); // case sensitive!

		// when
		contributors = repository.findContributorsTimeSeries(null, null, null, "Johnny");

		// then
		assertContributors(contributors); // not a prefix at all
	}

	@Test
	public void findContributorsTimeSeries_FilterByTimeSpan() throws Exception {

		// given
		repository.deleteAll();
		Contributor ca1 = newContributor().days(1).name("ca1").save();
		Contributor ca2 = newContributor().days(2).name("ca2").save();
		Contributor ca3 = newContributor().days(3).name("ca3").save();
		Contributor ca4 = newContributor().days(4).name("ca4").save();
		Contributor ca5 = newContributor().days(5).name("ca5").save();

		// when
		List<Contributor> contributors = repository.findContributorsTimeSeries(null, ca4.getSnapshotDate(),
				ca2.getSnapshotDate(), null);

		// then
		assertContributors(contributors, ca2, ca3, ca4);

		// when
		contributors = repository.findContributorsTimeSeries(null, null, ca4.getSnapshotDate(), null);

		// then
		assertContributors(contributors, ca4, ca5);

		// when
		contributors = repository.findContributorsTimeSeries(null, ca2.getSnapshotDate(), null, null);

		// then
		assertContributors(contributors, ca1, ca2);
	}

	@Test
	public void findContributorsTimeSeries_SortByShapshotDateAndContributorId() throws Exception {

		// given
		repository.deleteAll();
		Contributor ca1 = newContributor().days(1).name("ca1").save();
		Contributor cb1 = newContributor().days(1).name("cb1").save();
		Contributor ca2 = newContributor().days(2).name("ca2").id(ca1.getId()).save();
		Contributor cb2 = newContributor().days(2).name("cb2").id(cb1.getId()).save();

		// when
		List<Contributor> contributors = repository.findContributorsTimeSeries(null, null, null, null);

		// then
		assertContributors(contributors, cb1, ca1, cb2, ca2);
	}

	@Test
	public void testSaveAndFindOne() throws Exception {

		// given
		Contributor kim = newContributor().name("Kim").save();

		// when
		Contributor loadedContributor = repository.findOne(kim.getKey());

		// then
		assertThat(loadedContributor.getName(), equalTo("Kim"));
	}

	private void assertContributors(List<Contributor> foundContributors, Contributor... expectedContributors) {
		assertEquals(expectedContributors.length, foundContributors.size());

		for (int index = 0; index < foundContributors.size(); index++) {
			assertEquals(foundContributors.get(index).getId(), expectedContributors[index].getId());
		}
	}

}
