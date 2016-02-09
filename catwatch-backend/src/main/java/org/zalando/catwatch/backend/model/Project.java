package org.zalando.catwatch.backend.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.zalando.catwatch.backend.model.util.JsonDateDeserializer;
import org.zalando.catwatch.backend.model.util.JsonDateSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "project")
@ApiModel(description = "Represents a GitHub repository. See https://developer.github.com/v3/repos/")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ElementCollection
    @CollectionTable(name = "language_list", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "language")
    private List<String> languageList = new ArrayList<>();

    @Column(name = "git_hub_project_id")
    private long gitHubProjectId;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Column(name = "snapshot_date")
    private Date snapshotDate;

    @Column(name = "name")
    private String name;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    @Column(name = "stars_count")
    private Integer starsCount;

    @Column(name = "commits_count")
    private Integer commitsCount;

    @Column(name = "forks_count")
    private Integer forksCount;

    @Column(name = "contributors_count")
    private Integer contributorsCount;

    @Column(name = "score")
    private Integer score;

    @Column(name = "last_pushed")
    private String lastPushed;

    @Column(name = "primary_language")
    private String primaryLanguage;

    @ElementCollection
    @CollectionTable(name = "maintainers", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "maintainers")
    private List<String> maintainers = new ArrayList<>();

    @JsonIgnore
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    @ApiModelProperty(value = "List of programming languages of project")
    public List<String> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(final List<String> languageList) {
        this.languageList = languageList;
    }

    @ApiModelProperty(
        value = "the GitHub ID of the repository. Part of the primary key. See official GitHub REST API guide."
    )
    public long getGitHubProjectId() {
        return gitHubProjectId;
    }

    public void setGitHubProjectId(final long gitHubProjectId) {
        this.gitHubProjectId = gitHubProjectId;
    }

    @ApiModelProperty(value = "Project snapshot date. Part of the primary key.")
    public Date getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(final Date snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    @ApiModelProperty(value = "Name of project")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "Organization of the Project.")
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
    }

    @ApiModelProperty(value = "URL of project")
    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @ApiModelProperty(value = "Description of project")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @ApiModelProperty(value = "Count of stars for the project.")
    public Integer getStarsCount() {
        return starsCount;
    }

    public void setStarsCount(final Integer starsCount) {
        this.starsCount = starsCount;
    }

    @ApiModelProperty(value = "Count of commits for the project.")
    public Integer getCommitsCount() {
        return commitsCount;
    }

    public void setCommitsCount(final Integer commitsCount) {
        this.commitsCount = commitsCount;
    }

    @ApiModelProperty(value = "Count of forks of project.")
    public Integer getForksCount() {
        return forksCount;
    }

    public void setForksCount(final Integer forksCount) {
        this.forksCount = forksCount;
    }

    @ApiModelProperty(value = "Count of contributors of project.")
    public Integer getContributorsCount() {
        return contributorsCount;
    }

    public void setContributorsCount(final Integer contributorsCount) {
        this.contributorsCount = contributorsCount;
    }

    @ApiModelProperty(value = "Score of project.")
    public Integer getScore() {
        return score;
    }

    public void setScore(final Integer score) {
        this.score = score;
    }

    @ApiModelProperty(value = "Last pushed data of project.")
    public String getLastPushed() {
        return lastPushed;
    }

    public void setLastPushed(final String lastPushed) {
        this.lastPushed = lastPushed;
    }

    @ApiModelProperty(value = "Primary programming language of project")
    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(final String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    @ApiModelProperty(value = "List of maintainers of project")
    public List<String> getMaintainers() {
        return maintainers;
    }

    public void setMaintainers(final List<String> maintainers) {
        this.maintainers = maintainers;
    }

    @Override
    public String toString() {
        return "Project{" + "id=" + id + ", languageList=" + languageList + ", gitHubProjectId=" + gitHubProjectId
                + ", snapshotDate=" + snapshotDate + ", name='" + name + '\'' + ", organizationName='"
                + organizationName + '\'' + ", url='" + url + '\'' + ", description='" + description + '\''
                + ", starsCount=" + starsCount + ", commitsCount=" + commitsCount + ", forksCount=" + forksCount
                + ", contributorsCount=" + contributorsCount + ", score=" + score + ", lastPushed='" + lastPushed + '\''
                + ", primaryLanguage='" + primaryLanguage + '\'' + '}';
    }
}
