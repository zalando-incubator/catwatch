package org.zalando.catwatch.backend.repo;

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
		List<Contributor> contributors = repository.findContributorsTimeSeries(null, ca4.getSnapshotDate(), ca2.getSnapshotDate(), null);

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

	private void assertContributors(List<Contributor> expectedContributors, Contributor... contributors) {
		assertEquals(expectedContributors.size(), contributors.length);

		for (int index = 0; index < expectedContributors.size(); index++) {
			assertEquals(expectedContributors.get(index).getId(), contributors[index].getId());
		}
	}


}
