package org.zalando.catwatch.backend.web;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.util.Constants;

public class StatisticsControllerIT extends AbstractCatwatchIT {

    @Autowired
    private StatisticsRepository repository;
    
    private Statistics s1, s2;

	@Test
	public void getAllStatistics() throws Exception {
		
		fillRepository();
		
		
		//check if data has been initialized correctly
		Assert.assertNotNull(repository);
		Assert.assertNotNull(s1);
		Assert.assertNotNull(s2);
		Assert.assertNotNull("Statistics not found in repository", repository.findByOrganizationName(s1.getOrganizationName()));
		Assert.assertNotNull("Statistics not found in repository", repository.findByOrganizationName(s2.getOrganizationName()));
		
		String organisations = s1.getOrganizationName()+", "+s2.getOrganizationName();
		ResponseEntity<String> response = template.getForEntity(base.toString()+"?"+Constants.API_REQUEST_PARAM_ORGANIZATIONS+"="+organisations, String.class);
		
//		System.out.println(response.getBody());
//		assertThat(response.getBody(), containsString("<h1>CatWatch Backend</h1>"));
	}
	
	@Test
	public void getStatisticsFromOneOrganization() throws Exception {
//		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
//		assertThat(response.getBody(), containsString("<h1>CatWatch Backend</h1>"));
	}
	
	
	private void fillRepository() {
		this.repository.deleteAll();
		
		s1 = new Statistics();
		s1.setAllContributorsCount(10);
		s1.setAllForksCount(12);
		s1.setAllSizeCount(100);
		s1.setAllStarsCount(23);
		s1.setMembersCount(7);
		s1.setPrivateProjectCount(11);
		s1.setProgramLanguagesCount(3);
		s1.setPublicProjectCount(2);
		s1.setSnapshotDate(new Date());
		s1.setTagsCount(6);
		s1.setTeamsCount(0);
		s1.setOrganizationName("organization1");
		
		repository.save(s1);
		
		s2 = new Statistics();
		s2.setAllContributorsCount(9);
		s2.setAllForksCount(11);
		s2.setAllSizeCount(99);
		s2.setAllStarsCount(22);
		s2.setMembersCount(6);
		s2.setPrivateProjectCount(10);
		s2.setProgramLanguagesCount(2);
		s2.setPublicProjectCount(1);
		s2.setSnapshotDate(new Date(System.currentTimeMillis()-1000*60*60*24)); //a day ago
		s2.setTagsCount(5);
		s2.setTeamsCount(1);
		s2.setOrganizationName("organization2");
		
		repository.save(s2);
	}
}
