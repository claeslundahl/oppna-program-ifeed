package se.vgregion.common.utils;

import java.io.*;
import java.util.Locale;

public final class CommonUtils {
    public static final Locale SWEDISH_LOCALE = new Locale("sv_SE");

    private CommonUtils() {
    }

    public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String enumName) {
        if (isNull(enumClass)) {
            throw new IllegalArgumentException("enumClass must not be null.");
        }
        try {
            return Enum.valueOf(enumClass, enumName);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static Object toObject(String s) {
        try {
            return toObjectImpl(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Read the object from Base64 string.
     */
    private static Object toObjectImpl(String s) throws IOException,
            ClassNotFoundException {
        //byte [] data = Base64Coder.decode(s);
        byte[] data = s.getBytes();
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    public static String toString(Serializable o) {
        try {
            return toStringImpl(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write the object to a Base64 string.
     */
    private static String toStringImpl(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        //return new String( Base64Coder.encode(baos.toByteArray()) );
        return new String(baos.toByteArray());
    }


}
