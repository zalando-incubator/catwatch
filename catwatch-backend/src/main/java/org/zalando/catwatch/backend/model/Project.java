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

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@ApiModel(description = "Represents a GitHub repository. See https://developer.github.com/v3/repos/")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name = null;
	private String url = null;
	private String description = null;
	private Integer starsCount = null;
	private Integer commitsCount = null;
	private Integer forksCount = null;
	private Integer contributorsCount = null;
	private String score = null;
	private String lastPushed = null;
	private String primaryLanguage = null;
	private Date snapshotDate = null;
	  
	@ElementCollection
	@CollectionTable(name = "LANG_LIST", joinColumns = @JoinColumn(name = "PROJECT_ID") )
	@Column(name = "LANGUAGE")
	private List<String> languageList = new ArrayList<String>();

	private String organizationName = null;

	public long getId() {
		return id;
	}

	/**
	 * Name of project
	 **/
	@ApiModelProperty(value = "Name of project")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * URL of project
	 **/
	@ApiModelProperty(value = "URL of project")
	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Description of project
	 **/
	@ApiModelProperty(value = "Description of project")
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Count of stars for the project.
	 **/
	@ApiModelProperty(value = "Count of stars for the project.")
	@JsonProperty("starsCount")
	public Integer getStarsCount() {
		return starsCount;
	}

	public void setStarsCount(Integer starsCount) {
		this.starsCount = starsCount;
	}

	/**
	 * Count of commits for the project.
	 **/
	@ApiModelProperty(value = "Count of commits for the project.")
	@JsonProperty("commitsCount")
	public Integer getCommitsCount() {
		return commitsCount;
	}

	public void setCommitsCount(Integer commitsCount) {
		this.commitsCount = commitsCount;
	}

	/**
	 * Count of forks of project.
	 **/
	@ApiModelProperty(value = "Count of forks of project.")
	@JsonProperty("forksCount")
	public Integer getForksCount() {
		return forksCount;
	}

	public void setForksCount(Integer forksCount) {
		this.forksCount = forksCount;
	}

	/**
	 * Count of contributors of project.
	 **/
	@ApiModelProperty(value = "Count of contributors of project.")
	@JsonProperty("contributorsCount")
	public Integer getContributorsCount() {
		return contributorsCount;
	}

	public void setContributorsCount(Integer contributorsCount) {
		this.contributorsCount = contributorsCount;
	}

	/**
	 * Score of project.
	 **/
	@ApiModelProperty(value = "Score of project.")
	@JsonProperty("score")
	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	/**
	 * Last pushed data of project.
	 **/
	@ApiModelProperty(value = "Last pushed data of project.")
	@JsonProperty("lastPushed")
	public String getLastPushed() {
		return lastPushed;
	}

	public void setLastPushed(String lastPushed) {
		this.lastPushed = lastPushed;
	}

	/**
	 * Primary programming language of project
	 **/
	@ApiModelProperty(value = "Primary programming language of project")
	@JsonProperty("primaryLanguage")
	public String getPrimaryLanguage() {
		return primaryLanguage;
	}

	public void setPrimaryLanguage(String primaryLanguage) {
		this.primaryLanguage = primaryLanguage;
	}

	/**
	 * List of programming languages of project
	 **/
	@ApiModelProperty(value = "List of programming languages of project")
	@JsonProperty("languageList")
	public List<String> getLanguageList() {
		return languageList;
	}

	public void setLanguageList(List<String> languageList) {
		this.languageList = languageList;
	}

	/**
	 * Organization of the Project.
	 **/
	@ApiModelProperty(value = "Organization of the Project.")
	@JsonProperty("organizationName")
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	/**
	 * Project snapshot date.
	 **/
	@ApiModelProperty(value = "Project snapshot date.")
	@JsonProperty("snapshotDate")
	public Date getSnapshotDate() {
		return snapshotDate;
	}

	public void setSnapshotDate(Date snapshotDate) {
		this.snapshotDate = snapshotDate;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Project {\n");

		sb.append("  id: ").append(id).append("\n");
		sb.append("  name: ").append(name).append("\n");
		sb.append("  url: ").append(url).append("\n");
		sb.append("  description: ").append(description).append("\n");
		sb.append("  starsCount: ").append(starsCount).append("\n");
		sb.append("  commitsCount: ").append(commitsCount).append("\n");
		sb.append("  forksCount: ").append(forksCount).append("\n");
		sb.append("  contributorsCount: ").append(contributorsCount).append("\n");
		sb.append("  score: ").append(score).append("\n");
		sb.append("  lastPushed: ").append(lastPushed).append("\n");
		sb.append("  primaryLanguage: ").append(primaryLanguage).append("\n");
		sb.append("  languageList: ").append(languageList).append("\n");
		sb.append("  organizationName: ").append(organizationName).append("\n");
		sb.append("  snapshotDate: ").append(snapshotDate).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
