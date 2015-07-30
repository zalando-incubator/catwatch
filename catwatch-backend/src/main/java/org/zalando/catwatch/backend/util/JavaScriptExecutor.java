package org.zalando.catwatch.backend.util;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScriptExecutor {

    private String jsCode;

    private ScriptEngine engine;

    private Bindings bindings;

    /**
     * @param jsCode
     *            JavaScript code. Contains usually an assignment to <code>result.value</code>.
     */
    public static JavaScriptExecutor newExecutor(String jsCode) {
        return new JavaScriptExecutor(jsCode);
    }

    private JavaScriptExecutor(String jsCode) {
        super();
        this.jsCode = jsCode;

        ScriptEngineManager mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName("JavaScript");
        bindings = engine.createBindings();
    }

    public JavaScriptExecutor bind(String key, Object value) {
        bindings.put(key, value);
        return this;
    }

    /**
     * @return Returns <code>result.value</code>.
     */
    @SuppressWarnings("unchecked")
    public <T> T execute() {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            bindings.put("result", map);
            engine.eval(jsCode, bindings);
            return (T) map.get("value");
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}
