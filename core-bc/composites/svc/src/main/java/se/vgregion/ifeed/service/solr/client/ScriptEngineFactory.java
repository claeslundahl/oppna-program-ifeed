package se.vgregion.ifeed.service.solr.client;

import javax.script.ScriptEngine;

public abstract class ScriptEngineFactory {

    private static ScriptEngineFactory instance;

    static {
        // Default
        instance = new PortalClassLoaderScriptEngineFactoryImpl();
    }

    public static ScriptEngineFactory getInstance() {
        return instance;
    }

    // To override default.
    public static void setScriptEngineFactory(ScriptEngineFactory scriptEngineFactory) {
        instance = scriptEngineFactory;
    }

    public abstract ScriptEngine createJavascriptScriptEngine();
}
