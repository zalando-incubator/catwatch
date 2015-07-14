package org.zalando.catwatch.backend.model;

import java.math.BigDecimal;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.JsonProperty;


@ApiModel(description = "")
public class Statistics  {
  
  private BigDecimal privateProjectCount = null;
  private BigDecimal publicProjectCount = null;
  private BigDecimal membersCount = null;
  private BigDecimal teamsCount = null;
  private BigDecimal allContributorsCount = null;
  private BigDecimal allStarsCount = null;
  private BigDecimal allForksCount = null;
  private BigDecimal allSizeCount = null;
  private BigDecimal programLanguagesCount = null;
  private BigDecimal tagsCount = null;
  private String snapshotDate = null;

  
  /**
   * Count of private projects.
   **/
  @ApiModelProperty(value = "Count of private projects.")
  @JsonProperty("privateProjectCount")
  public BigDecimal getPrivateProjectCount() {
    return privateProjectCount;
  }
  public void setPrivateProjectCount(BigDecimal privateProjectCount) {
    this.privateProjectCount = privateProjectCount;
  }

  
  /**
   * Count of public projects.
   **/
  @ApiModelProperty(value = "Count of public projects.")
  @JsonProperty("publicProjectCount")
  public BigDecimal getPublicProjectCount() {
    return publicProjectCount;
  }
  public void setPublicProjectCount(BigDecimal publicProjectCount) {
    this.publicProjectCount = publicProjectCount;
  }

  
  /**
   * Count of memebers.
   **/
  @ApiModelProperty(value = "Count of memebers.")
  @JsonProperty("membersCount")
  public BigDecimal getMembersCount() {
    return membersCount;
  }
  public void setMembersCount(BigDecimal membersCount) {
    this.membersCount = membersCount;
  }

  
  /**
   * Count of teams.
   **/
  @ApiModelProperty(value = "Count of teams.")
  @JsonProperty("teamsCount")
  public BigDecimal getTeamsCount() {
    return teamsCount;
  }
  public void setTeamsCount(BigDecimal teamsCount) {
    this.teamsCount = teamsCount;
  }

  
  /**
   * Count of contributors.
   **/
  @ApiModelProperty(value = "Count of contributors.")
  @JsonProperty("allContributorsCount")
  public BigDecimal getAllContributorsCount() {
    return allContributorsCount;
  }
  public void setAllContributorsCount(BigDecimal allContributorsCount) {
    this.allContributorsCount = allContributorsCount;
  }

  
  /**
   * Count of stars.
   **/
  @ApiModelProperty(value = "Count of stars.")
  @JsonProperty("allStarsCount")
  public BigDecimal getAllStarsCount() {
    return allStarsCount;
  }
  public void setAllStarsCount(BigDecimal allStarsCount) {
    this.allStarsCount = allStarsCount;
  }

  
  /**
   * Count of forks.
   **/
  @ApiModelProperty(value = "Count of forks.")
  @JsonProperty("allForksCount")
  public BigDecimal getAllForksCount() {
    return allForksCount;
  }
  public void setAllForksCount(BigDecimal allForksCount) {
    this.allForksCount = allForksCount;
  }

  
  /**
   * Count of projects.
   **/
  @ApiModelProperty(value = "Count of projects.")
  @JsonProperty("allSizeCount")
  public BigDecimal getAllSizeCount() {
    return allSizeCount;
  }
  public void setAllSizeCount(BigDecimal allSizeCount) {
    this.allSizeCount = allSizeCount;
  }

  
  /**
   * Count of programming languages used.
   **/
  @ApiModelProperty(value = "Count of programming languages used.")
  @JsonProperty("programLanguagesCount")
  public BigDecimal getProgramLanguagesCount() {
    return programLanguagesCount;
  }
  public void setProgramLanguagesCount(BigDecimal programLanguagesCount) {
    this.programLanguagesCount = programLanguagesCount;
  }

  
  /**
   * Count of tags.
   **/
  @ApiModelProperty(value = "Count of tags.")
  @JsonProperty("tagsCount")
  public BigDecimal getTagsCount() {
    return tagsCount;
  }
  public void setTagsCount(BigDecimal tagsCount) {
    this.tagsCount = tagsCount;
  }

  
  /**
   * Statistics snapshot date.
   **/
  @ApiModelProperty(value = "Statistics snapshot date.")
  @JsonProperty("snapshotDate")
  public String getSnapshotDate() {
    return snapshotDate;
  }
  public void setSnapshotDate(String snapshotDate) {
    this.snapshotDate = snapshotDate;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Statistics {\n");
    
    sb.append("  privateProjectCount: ").append(privateProjectCount).append("\n");
    sb.append("  publicProjectCount: ").append(publicProjectCount).append("\n");
    sb.append("  membersCount: ").append(membersCount).append("\n");
    sb.append("  teamsCount: ").append(teamsCount).append("\n");
    sb.append("  allContributorsCount: ").append(allContributorsCount).append("\n");
    sb.append("  allStarsCount: ").append(allStarsCount).append("\n");
    sb.append("  allForksCount: ").append(allForksCount).append("\n");
    sb.append("  allSizeCount: ").append(allSizeCount).append("\n");
    sb.append("  programLanguagesCount: ").append(programLanguagesCount).append("\n");
    sb.append("  tagsCount: ").append(tagsCount).append("\n");
    sb.append("  snapshotDate: ").append(snapshotDate).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
