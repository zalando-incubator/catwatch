package org.zalando.catwatch.backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@ApiModel(description = "A contributor is a (GitHub) user that may have contributed to projects. "
		+ "Equals to a GitHub acocunt. See https://developer.github.com/v3/users/#get-a-single-user")
public class Contributor {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name = null;
	private String url = null;
	private Integer organizationalCommitsCount = null;
	private Integer personalCommitsCount = null;
	private Integer personalProjectsCount = null;
	private Integer organizationalProjectsCount = null;

	public Contributor() {
		super();
	}

	public Contributor(String name) {
		super();
		this.name = name;
	}

	public long getId() {
		return id;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Contributor {\n");

		sb.append("  id: ").append(id).append("\n");
		sb.append("  name: ").append(name).append("\n");
		sb.append("  url: ").append(url).append("\n");
		sb.append("  organizationalCommitsCount: ").append(organizationalCommitsCount).append("\n");
		sb.append("  personalCommitsCount: ").append(personalCommitsCount).append("\n");
		sb.append("  personalProjectsCount: ").append(personalProjectsCount).append("\n");
		sb.append("  organizationalProjectsCount: ").append(organizationalProjectsCount).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
