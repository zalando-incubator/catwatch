package org.zalando.catwatch.backend.model;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

public class ContributorTest {
    
	@Test
	public void dummytest() throws Exception {
		
        // given
		Contributor contributor = new Contributor(70,23,new Date());
		contributor.setName("Jack");
		
		// when
		contributor.setName("David");
		
		// then
		assertThat(contributor.getName(), equalTo("David"));
    }

}
