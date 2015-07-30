
var projects = data.projects
.filter(function(project){
    return project.organizationName == "zalando" || project.organizationName == "zalando-stups";
})
.map(function(project) {
    return {
        // requirement for a quick solution: sum(#stars + #forks + #contributors + (#commits / 100))
        score : ( project.forksCount > 0 ? ( project.starsCount + project.forksCount + project.contributorsCount + project.commitsCount / 100 ) : 0),
        description : ( ""+ project.starsCount + " stars,"
                          + project.forksCount + " forks,"
                          + project.contributorsCount + " cont, "
                          + project.commitsCount + " commits"),
        project : ( "" + project.name + "(" + project.gitHubProjectId + ")" )
    };
})
.sort(function(p1, p2) {
    var compare = p1.score - p2.score;
    if (compare != 0) {
        return compare;
    }
    else {
        return p1.project.localeCompare(p2.project);
    }
})
.map(function(p) {
     console.log(p);
     return p;
});

console.log("number of projects", projects.length);
