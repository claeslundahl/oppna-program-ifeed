package se.vgregion.common.utils;

import java.lang.instrument.Instrumentation;

public class InstrumentationHome {

    public static Instrumentation instance;

    public static void premain(String args, Instrumentation inst) {
        System.out.println("Premain is beeing called!");
        instance = inst;
    }

    public static void main(String[] args) {
        System.out.println("Instrumentation " + instance);
    }



}
