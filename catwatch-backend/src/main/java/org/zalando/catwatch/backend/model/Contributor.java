package org.zalando.catwatch.backend.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@ApiModel(description = "")
public class Contributor {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

	private String name = null;
	private String url = null;
	private BigDecimal organizationalCommitsCount = null;
	private BigDecimal personalCommitsCount = null;
	private BigDecimal personalProjectsCount = null;
	private BigDecimal organizationalProjectsCount = null;

	public Contributor() {
		super();
	}

	public Contributor(String name) {
		super();
		this.name = name;
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
	public BigDecimal getOrganizationalCommitsCount() {
		return organizationalCommitsCount;
	}

	public void setOrganizationalCommitsCount(BigDecimal organizationalCommitsCount) {
		this.organizationalCommitsCount = organizationalCommitsCount;
	}

	/**
	 * Count of personal commits.
	 **/
	@ApiModelProperty(value = "Count of personal commits.")
	@JsonProperty("personalCommitsCount")
	public BigDecimal getPersonalCommitsCount() {
		return personalCommitsCount;
	}

	public void setPersonalCommitsCount(BigDecimal personalCommitsCount) {
		this.personalCommitsCount = personalCommitsCount;
	}

	/**
	 * Count of personal projects of contributor.
	 **/
	@ApiModelProperty(value = "Count of personal projects of contributor.")
	@JsonProperty("personalProjectsCount")
	public BigDecimal getPersonalProjectsCount() {
		return personalProjectsCount;
	}

	public void setPersonalProjectsCount(BigDecimal personalProjectsCount) {
		this.personalProjectsCount = personalProjectsCount;
	}

	/**
	 * Count of organization projects of contributor.
	 **/
	@ApiModelProperty(value = "Count of organization projects of contributor.")
	@JsonProperty("organizationalProjectsCount")
	public BigDecimal getOrganizationalProjectsCount() {
		return organizationalProjectsCount;
	}

	public void setOrganizationalProjectsCount(BigDecimal organizationalProjectsCount) {
		this.organizationalProjectsCount = organizationalProjectsCount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Contributor {\n");

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
