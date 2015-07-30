function(project) { 
  return project.forksCount > 0 ? 
      ( project.starsCount + project.forksCount + project.contributorsCount + project.commitsCount / 100 ) 
      : 0
}