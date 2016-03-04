package org.zalando.catwatch.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "A programming language as it is used in source code of GitHub repositories. "
		+ "Compare with https://developer.github.com/v3/repos/#list-languages")
public class Language  {
  
  private String name = null;
  private Integer projectsCount = null;
  private Integer percentage = null;

  public Language(){
  }
  
  public Language(String name){
	  this.name = name;
  }
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
  public Integer getProjectsCount() {
    return projectsCount;
  }
  public void setProjectsCount(Integer projectsCount) {
    this.projectsCount = projectsCount;
  }

  
  /**
   * Usage percentage of programming language.
   **/
  @ApiModelProperty(value = "Usage percentage of programming language.")
  @JsonProperty("percentage")
  public Integer getPercentage() {
    return percentage;
  }
  public void setPercentage(Integer percentage) {
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
