package se.vgregion.ifeed.service.solr.client;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class PortalClassLoaderScriptEngineFactoryImpl extends ScriptEngineFactory {

    @Override
    public ScriptEngine createJavascriptScriptEngine() {
        // This procedure is to mitigate issue where the current classloader otherwise would be the OSGI classloader,
        // resulting in sem.getEngineByName("javascript") returning null.

        Thread thread = Thread.currentThread();

        // Get the thread's class loader. You'll reinstate it after using
        // the data source you look up using JNDI

        ClassLoader origLoader = thread.getContextClassLoader();

        // Set Liferay's class loader on the thread

        thread.setContextClassLoader(PortalClassLoaderUtil.getClassLoader());

        try {
            ScriptEngineManager sem = new ScriptEngineManager();
            return sem.getEngineByName("javascript");
        } finally {
            // Switch back to the original context class loader

            thread.setContextClassLoader(origLoader);
        }
    }
}
