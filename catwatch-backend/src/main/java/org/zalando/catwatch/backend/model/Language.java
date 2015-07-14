package org.zalando.catwatch.backend.model;

import java.math.BigDecimal;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.JsonProperty;


@ApiModel(description = "")
public class Language  {
  
  private String name = null;
  private BigDecimal projectsCount = null;
  private BigDecimal percentage = null;

  
  /**
   * Name of the programming language used.
   **/
  @ApiModelProperty(value = "Name of the programming language used.")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  
  /**
   * Count of projects using it.
   **/
  @ApiModelProperty(value = "Count of projects using it.")
  @JsonProperty("projectsCount")
  public BigDecimal getProjectsCount() {
    return projectsCount;
  }
  public void setProjectsCount(BigDecimal projectsCount) {
    this.projectsCount = projectsCount;
  }

  
  /**
   * Usage percentage of programming language.
   **/
  @ApiModelProperty(value = "Usage percentage of programming language.")
  @JsonProperty("percentage")
  public BigDecimal getPercentage() {
    return percentage;
  }
  public void setPercentage(BigDecimal percentage) {
    this.percentage = percentage;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Language {\n");
    
    sb.append("  name: ").append(name).append("\n");
    sb.append("  projectsCount: ").append(projectsCount).append("\n");
    sb.append("  percentage: ").append(percentage).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
