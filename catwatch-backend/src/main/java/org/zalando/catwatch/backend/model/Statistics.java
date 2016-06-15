package org.zalando.catwatch.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.zalando.catwatch.backend.model.util.JsonDateDeserializer;
import org.zalando.catwatch.backend.model.util.JsonDateSerializer;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@ApiModel(description = "Represents the statistics of a GitHub organization. See https://developer.github.com/v3/orgs/")
public class Statistics {

	@Id
	private StatisticsKey key;

	private Integer publicProjectCount = null;
	private Integer membersCount = null;
	private Integer teamsCount = null;
	private Integer allContributorsCount = null;
	private Integer allStarsCount = null;
	private Integer allForksCount = null;
	private Integer allSizeCount = null;
	private Integer programLanguagesCount = null;
	private Integer tagsCount = null;
	private String organizationName = null;

	public Statistics() {
		super();
	}

	public Statistics(long id, Date snapshotDate) {
		super();
		this.key = new StatisticsKey(id, snapshotDate);
	}

	@JsonIgnore
	public StatisticsKey getKey() {
		return key;
	}

	@ApiModelProperty(value = "the GitHub ID of the organization. Part of the primary key. See official GitHub REST API guide.")
	@JsonIgnore
	public long getId() {
		return key == null ? 0: key.getId();
	}

	/**
	 * Count of public projects.
	 **/
	@ApiModelProperty(value = "Count of public projects.")
	@JsonProperty("public_project_count")
	public Integer getPublicProjectCount() {
		return publicProjectCount;
	}

	public void setPublicProjectCount(Integer publicProjectCount) {
		this.publicProjectCount = publicProjectCount;
	}

	/**
	 * Count of memebers.
	 **/
	@ApiModelProperty(value = "Count of memebers.")
	@JsonProperty("members_count")
	public Integer getMembersCount() {
		return membersCount;
	}

	public void setMembersCount(Integer membersCount) {
		this.membersCount = membersCount;
	}

	/**
	 * Count of teams.
	 **/
	@ApiModelProperty(value = "Count of teams.")
	@JsonProperty("teams_count")
	public Integer getTeamsCount() {
		return teamsCount;
	}

	public void setTeamsCount(Integer teamsCount) {
		this.teamsCount = teamsCount;
	}

	/**
	 * Count of contributors.
	 **/
	@ApiModelProperty(value = "Count of contributors.")
	@JsonProperty("all_contributors_count")
	public Integer getAllContributorsCount() {
		return allContributorsCount;
	}

	public void setAllContributorsCount(Integer allContributorsCount) {
		this.allContributorsCount = allContributorsCount;
	}

	/**
	 * Count of stars.
	 **/
	@ApiModelProperty(value = "Count of stars.")
	@JsonProperty("all_stars_count")
	public Integer getAllStarsCount() {
		return allStarsCount;
	}

	public void setAllStarsCount(Integer allStarsCount) {
		this.allStarsCount = allStarsCount;
	}

	/**
	 * Count of forks.
	 **/
	@ApiModelProperty(value = "Count of forks.")
	@JsonProperty("all_forks_count")
	public Integer getAllForksCount() {
		return allForksCount;
	}

	public void setAllForksCount(Integer allForksCount) {
		this.allForksCount = allForksCount;
	}

	/**
	 * Count of projects.
	 **/
	@ApiModelProperty(value = "Count of projects.")
	@JsonProperty("all_size_count")
	public Integer getAllSizeCount() {
		return allSizeCount;
	}

	public void setAllSizeCount(Integer allSizeCount) {
		this.allSizeCount = allSizeCount;
	}

	/**
	 * Count of programming languages used.
	 **/
	@ApiModelProperty(value = "Count of programming languages used.")
	@JsonProperty("program_languages_count")
	public Integer getProgramLanguagesCount() {
		return programLanguagesCount;
	}

	public void setProgramLanguagesCount(Integer programLanguagesCount) {
		this.programLanguagesCount = programLanguagesCount;
	}

	/**
	 * Count of tags.
	 **/
	@ApiModelProperty(value = "Count of tags.")
	@JsonProperty("tags_count")
	public Integer getTagsCount() {
		return tagsCount;
	}

	public void setTagsCount(Integer tagsCount) {
		this.tagsCount = tagsCount;
	}

	/**
	 * Organization name.
	 **/
	@ApiModelProperty(value = "Organization name.")
	@JsonProperty("organization_name")
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	/**
	 * Statistics snapshot date.
	 **/
	@ApiModelProperty(value = "Statistics snapshot date. Part of the primary key.")
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	@JsonProperty("snapshot_date")
	public Date getSnapshotDate() {
		return key == null ? null : key.getSnapshotDate();
	}
	
	public void setSnapshotDate(Date snapshotDate){
		
		if(this.key==null) this.key = new StatisticsKey();
		
		this.key.setSnapshotDate(snapshotDate);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Statistics {\n");

		sb.append("  id: ").append(getId()).append("\n");
		sb.append("  publicProjectCount: ").append(publicProjectCount).append("\n");
		sb.append("  membersCount: ").append(membersCount).append("\n");
		sb.append("  teamsCount: ").append(teamsCount).append("\n");
		sb.append("  allContributorsCount: ").append(allContributorsCount).append("\n");
		sb.append("  allStarsCount: ").append(allStarsCount).append("\n");
		sb.append("  allForksCount: ").append(allForksCount).append("\n");
		sb.append("  allSizeCount: ").append(allSizeCount).append("\n");
		sb.append("  programLanguagesCount: ").append(programLanguagesCount).append("\n");
		sb.append("  tagsCount: ").append(tagsCount).append("\n");
		sb.append("  organizationName: ").append(organizationName).append("\n");
		sb.append("  snapshotDate: ").append(getSnapshotDate()).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
