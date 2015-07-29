package org.zalando.catwatch.backend.github;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zalando.catwatch.backend.model.Project;

@Component
public class Scorer {

    @Value("${scoring.project}")
    private String scoringProject;

    public int score(Project project) {

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try {
            Bindings bindings = engine.createBindings();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("project", project);
            bindings.put("map", map);
            String js = "var scoring = " + scoringProject + ";\n map.score = scoring(map.project);";
            engine.eval(js, bindings);
            return ((Number) map.get("score")).intValue();
        } catch (ScriptException e) {
            throw new RuntimeException("never to happen: " + e.getMessage(), e);
        }
    }

    public void setScoringProject(String scoringProject) {
        this.scoringProject = scoringProject;
    }

}
