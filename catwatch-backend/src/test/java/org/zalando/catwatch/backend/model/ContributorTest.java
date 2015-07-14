package org.zalando.catwatch.backend.model;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ContributorTest {
    
	@Test
	public void dummytest() throws Exception {
		
        // given
		Contributor contributor = new Contributor("Jack");
		
		// when
		contributor.setName("David");
		
		// then
		assertThat(contributor.getName(), equalTo("David"));
    }

}
