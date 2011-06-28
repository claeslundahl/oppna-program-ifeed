package se.vgregion.common.utils;

public final class CommonUtils {
    private CommonUtils() {
    }

    public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String enumName) {
        if(isNull(enumClass)) {
            throw new IllegalArgumentException("enumClass must not be null.");
        }
        try {
            return Enum.valueOf(enumClass, enumName);
        } catch (NullPointerException  e) {
            return null;
        }
    }

    public static boolean isNull(Object o) {
        return o == null;
    }


}