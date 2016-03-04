package org.zalando.catwatch.backend.model.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zalando.catwatch.backend.model.Project;

import static org.zalando.catwatch.backend.util.JavaScriptExecutor.newExecutor;

@Component
public class Scorer {

    @Value("${scoring.project}")
    private String scoringProject;

    public int score(Project project) {
        
        String jsCode = "";
        jsCode += "var scoring = " + scoringProject + ";\n";
        jsCode += "result.value = scoring(project);";
        
        return ((Number) newExecutor(jsCode).bind("project", project).execute()).intValue();
    }

    public void setScoringProject(String scoringProject) {
        this.scoringProject = scoringProject;
    }

}
