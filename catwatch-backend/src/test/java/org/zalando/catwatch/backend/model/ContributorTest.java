package org.zalando.catwatch.backend.model;

import org.junit.Test;

import java.util.Date;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;

public class ContributorTest {

	@Test
	public void testToString_containsKey() throws Exception {

		// given
		Date date = new Date();
		Contributor contributor = new Contributor(123456789, 987654321, date);

		// when
		String str = contributor.toString();

		// then
		assertThat(str, stringContainsInOrder(asList("id", ":", "123456789")));
		assertThat(str, stringContainsInOrder(asList("organizationId", ":", "987654321")));
		assertThat(str, stringContainsInOrder(asList("snapshotDate", ":", "" + date)));
	}

}
