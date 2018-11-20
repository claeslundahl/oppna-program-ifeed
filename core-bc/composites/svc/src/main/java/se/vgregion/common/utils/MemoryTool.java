package se.vgregion.common.utils;

import org.apache.commons.lang.mutable.MutableLong;

import java.lang.instrument.Instrumentation;

public class MemoryTool {

    public long measure(Object instance) {
        Instrumentation instrumentation = InstrumentationHome.instance;
        if (instrumentation == null) {
            return -1;
        }
        MutableLong sizeSum = new MutableLong(0l);
        Traverser.go(instance, o -> {
            sizeSum.setValue(((Long) sizeSum.getValue()) + instrumentation.getObjectSize(o));
        });
        System.out.println("Size of Application instance is " + sizeSum.getValue());
        return (long) sizeSum.getValue();
    }

}
