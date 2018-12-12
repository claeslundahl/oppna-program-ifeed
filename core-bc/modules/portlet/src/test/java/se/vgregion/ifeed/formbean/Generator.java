package se.vgregion.ifeed.formbean;

import net.sf.cglib.beans.BeanMap;

import java.util.Collection;

public class Generator {

    public static void main(String[] arga) throws NoSuchFieldException {
        printAsTypescript(new se.vgregion.ifeed.service.ifeed.Filter());
    }

    private static void printAsTypescript(Object obj) throws NoSuchFieldException {
        BeanMap bm = BeanMap.create(obj);

        for (Object key : bm.keySet()) {
            if (key.equals("class")) {
                continue;
            }
            Class pt = bm.getPropertyType((String) key);
            if (Collection.class.isAssignableFrom(pt)) {
                /*Field field = obj.getClass().getField((String) key);
                ParameterizedType par= (ParameterizedType) field.getGenericType();
                Type actualType = par.getActualTypeArguments()[0];*/
                // System.out.println(key + ": Array<"+actualType.getTypeName()+"> = [];");

                System.out.println(key + ": Array<> = [];");
            } else if (Number.class.isAssignableFrom(pt)) {
                System.out.println(key + ": number;");
            } else if (String.class.isAssignableFrom(pt)) {
                System.out.println(key + ": string;");
            } else if (Boolean.class.isAssignableFrom(pt)) {
                System.out.println(key + ": boolean;");
            } else {
                System.out.println(key + ": " + pt.getSimpleName() + ";");
            }
        }

    }
}
