package org.zalando.catwatch.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.zalando.catwatch.backend.model.util.JsonDateDeserializer;
import org.zalando.catwatch.backend.model.util.JsonDateSerializer;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Column(name = "title")
    private String title;

    @Column(name = "image")
    private String image;

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

    @Column(name = "external_contributors_count")
    private Integer externalContributorsCount;

    @Column(name = "score")
    private Integer score;

    @Column(name = "last_pushed")
    private String lastPushed;

    @Column(name = "primary_language")
    private String primaryLanguage;

    @ElementCollection
    @CollectionTable(name = "maintainers", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "maintainer")
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

    @ApiModelProperty(value = "Title of project")
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @ApiModelProperty(value = "Image url of project")
    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
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

    @ApiModelProperty(value = "Count of contributors for project.")
    public Integer getContributorsCount() {
        return contributorsCount;
    }

    public void setContributorsCount(final Integer contributorsCount) {
        this.contributorsCount = contributorsCount;
    }

    @ApiModelProperty(value = "Count of external contributors for project.")
    public Integer getExternalContributorsCount() { return externalContributorsCount; }

    public void setExternalContributorsCount(Integer externalContributorsCount) {
        this.externalContributorsCount = externalContributorsCount;
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
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("languageList", languageList)
                .add("gitHubProjectId", gitHubProjectId)
                .add("snapshotDate", snapshotDate)
                .add("name", name)
                .add("title", title)
                .add("image", image)
                .add("organizationName", organizationName)
                .add("url", url)
                .add("description", description)
                .add("starsCount", starsCount)
                .add("commitsCount", commitsCount)
                .add("forksCount", forksCount)
                .add("contributorsCount", contributorsCount)
                .add("externalContributorsCount", externalContributorsCount)
                .add("score", score)
                .add("lastPushed", lastPushed)
                .add("primaryLanguage", primaryLanguage)
                .add("maintainers", maintainers)
                .toString();
    }
}
