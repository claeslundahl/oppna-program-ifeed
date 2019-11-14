package se.vgregion.common.utils;

import java.lang.instrument.Instrumentation;

public class MemoryTool {

    public long measure(Object instance) {
        Instrumentation instrumentation = InstrumentationHome.instance;
        if (instrumentation == null) {
            return -1;
        }
        Holder<Long> sizeSum = new Holder<Long>(0l);
        Traverser.go(instance, o -> {
            sizeSum.content = (((Long) sizeSum.content) + instrumentation.getObjectSize(o));
        });
        System.out.println("Size of Application instance is " + sizeSum.content);
        return (long) sizeSum.content;
    }

    private class Holder<T> {
        public T content;

        public Holder(T content) {
            this.content = content;
        }

    }

}
