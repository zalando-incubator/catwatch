package org.zalando.catwatch.backend.web.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.zalando.catwatch.backend.model.Contributor;
import org.zalando.catwatch.backend.model.Statistics;
import org.zalando.catwatch.backend.repo.ContributorRepository;
import org.zalando.catwatch.backend.repo.ProjectRepository;
import org.zalando.catwatch.backend.repo.StatisticsRepository;
import org.zalando.catwatch.backend.repo.populate.ContributorBuilder;
import org.zalando.catwatch.backend.repo.populate.StatisticsBuilder;
import org.zalando.catwatch.backend.web.AbstractCatwatchIT;

public class ExportImportControllerIT extends AbstractCatwatchIT {

    @Autowired
    private ContributorRepository contributorRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;
    
    @Autowired
    private ProjectRepository projectRepository;

    private ContributorBuilder newContributor() {
        return new ContributorBuilder(contributorRepository);
    }

    private StatisticsBuilder newStatistic() {
        return new StatisticsBuilder(statisticsRepository);
    }

    @Test
    public void testExportAndImport() throws Exception {
        
        // given
        contributorRepository.deleteAll();
        statisticsRepository.deleteAll();
        Contributor c = contributorRepository.findOne(newContributor().save().getKey());
        Statistics s = statisticsRepository.findOne(newStatistic().save().getKey());
        
        // when
        DatabaseDto dto = template.getForEntity(exportUrl(), DatabaseDto.class).getBody();
        
        contributorRepository.deleteAll();
        statisticsRepository.deleteAll();
        projectRepository.deleteAll();
        
        template.postForEntity(importUrl(), dto, String.class);
        
        // then
        assertThat(contributorRepository.findAll(), iterableWithSize(1));
        Contributor c_ = contributorRepository.findAll().iterator().next();
        assertThat(c_.getId(), equalTo(c.getId()));
        // TODO problem!!!! the milliseconds are truncated so that the date differs after the export/import :-(
        //assertThat(c_.getSnapshotDate().getTime(), equalTo(c.getSnapshotDate().getTime()));
        
        assertThat(statisticsRepository.findAll(), iterableWithSize(1));
        Statistics s_ = statisticsRepository.findAll().iterator().next();
        assertThat(s_.getId(), equalTo(s.getId()));
        // TODO problem!!!! the milliseconds are truncated so that the date differs after the export/import :-(
        //assertThat(s_.getSnapshotDate().getTime(), equalTo(s.getSnapshotDate().getTime()));
    }

    private String exportUrl() {
        return fromHttpUrl(base.toString() + "export").toUriString();
    }

    private String importUrl() {
        return fromHttpUrl(base.toString() + "import").toUriString();
    }

}
