package org.zalando.catwatch.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@ApiModel(description = "A contributor is a (GitHub) user that may have contributed to projects. "
		+ "Equals to a GitHub acocunt. See https://developer.github.com/v3/users/#get-a-single-user")
public class Contributor {

	@EmbeddedId
	private ContributorKey key;

	private String name = null;
	private String url = null;
	private Integer organizationalCommitsCount = null;
	/**
	 * todo
	 */
	private Integer personalCommitsCount = null;
	private Integer personalProjectsCount = null;
	private Integer organizationalProjectsCount = null;
	private String organizationName = null;

	public Contributor() {
		super();
	}

	public Contributor(long id, long organizationId, Date snapshotDate) {
		super();
		this.key = new ContributorKey(id, organizationId, snapshotDate);
	}
	

	public ContributorKey getKey() {
		return key;
	}

	@ApiModelProperty(value = "the GitHub User ID of the Contributor. Part of the primary key. See official GitHub REST API guide.")
	@JsonProperty("id")
	public long getId() {
		return key == null ? 0: key.getId();
	}

	@ApiModelProperty(value = "the GitHub ID of the organization. Part of the primary key. See official GitHub REST API guide.")
	@JsonProperty("organizationId")
	public long getOrganizationId() {
		return key == null ? 0: key.getOrganizationId();
	}
	/**
	 * Name of contributor
	 **/
	@ApiModelProperty(value = "Name of contributor")
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * URL of contributor
	 **/
	@ApiModelProperty(value = "URL of contributor")
	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Count of organizational commits.
	 **/
	@ApiModelProperty(value = "Count of organizational commits.")
	@JsonProperty("organizationalCommitsCount")
	public Integer getOrganizationalCommitsCount() {
		return organizationalCommitsCount;
	}

	public void setOrganizationalCommitsCount(Integer organizationalCommitsCount) {
		this.organizationalCommitsCount = organizationalCommitsCount;
	}

	/**
	 * Count of personal commits.
	 **/
	@ApiModelProperty(value = "Count of personal commits.")
	@JsonProperty("personalCommitsCount")
	public Integer getPersonalCommitsCount() {
		return personalCommitsCount;
	}

	public void setPersonalCommitsCount(Integer personalCommitsCount) {
		this.personalCommitsCount = personalCommitsCount;
	}

	/**
	 * Count of personal projects of contributor.
	 **/
	@ApiModelProperty(value = "Count of personal projects of contributor.")
	@JsonProperty("personalProjectsCount")
	public Integer getPersonalProjectsCount() {
		return personalProjectsCount;
	}

	public void setPersonalProjectsCount(Integer personalProjectsCount) {
		this.personalProjectsCount = personalProjectsCount;
	}

	/**
	 * Count of organization projects of contributor.
	 **/
	@ApiModelProperty(value = "Count of organization projects of contributor.")
	@JsonProperty("organizationalProjectsCount")
	public Integer getOrganizationalProjectsCount() {
		return organizationalProjectsCount;
	}

	public void setOrganizationalProjectsCount(Integer organizationalProjectsCount) {
		this.organizationalProjectsCount = organizationalProjectsCount;
	}
	
	/**
	 * Organization of the Contributor.
	 **/
	@ApiModelProperty(value = "Organization of the Contributor.")
	@JsonProperty("organizationName")
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	/**
	 * Contributor snapshot date.
	 **/
	@ApiModelProperty(value = "Contributor snapshot date. Part of the primary key.")
	@JsonProperty("snapshotDate")
	public Date getSnapshotDate() {
		return key == null ? null : key.getSnapshotDate();
	}

	public String getLoginId() {
		String regex = "https://github.com/";
		String loginId = "";
		if (url != null && url.startsWith(regex)) {
			loginId = url.split(regex)[1];
		}
		return loginId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Contributor {\n");

		sb.append("  id: ").append(getId()).append("\n");
		sb.append("  organizationId: ").append(getOrganizationId()).append("\n");
		sb.append("  name: ").append(name).append("\n");
		sb.append("  url: ").append(url).append("\n");
		sb.append("  organizationalCommitsCount: ").append(organizationalCommitsCount).append("\n");
		sb.append("  personalCommitsCount: ").append(personalCommitsCount).append("\n");
		sb.append("  personalProjectsCount: ").append(personalProjectsCount).append("\n");
		sb.append("  organizationalProjectsCount: ").append(organizationalProjectsCount).append("\n");
		sb.append("  organizationName: ").append(organizationName).append("\n");
		sb.append("  snapshotDate: ").append(getSnapshotDate()).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
