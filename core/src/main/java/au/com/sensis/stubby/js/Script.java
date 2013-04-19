package au.com.sensis.stubby.js;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

public class Script {

    private String source;

    public Script(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    private ScriptEngine createEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.setContext(new SimpleScriptContext());
        return engine;
    }

    public Object execute(ScriptWorld world) {
        ScriptEngine engine = createEngine();

        engine.put("request", world.getRequest()); // TODO: deprecate (use 'exchange.request')
        engine.put("response", world.getResponse()); // TODO: deprecate (use 'exchange.response')
        engine.put("exchange", world);

        try {
            return engine.eval(source); // note: result is actually not used by stub server atm.
        } catch (ScriptException e) {
            throw new RuntimeException("Error executing script", e);
        }
    }

}
