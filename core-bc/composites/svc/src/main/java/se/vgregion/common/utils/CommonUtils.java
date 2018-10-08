package se.vgregion.common.utils;

import net.sf.cglib.beans.BeanMap;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

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
        //byte[] data = Base64.decode(s);
        byte[] data = DatatypeConverter.parseBase64Binary(s);
        //byte[] data = s.getBytes();
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
        return DatatypeConverter.printBase64Binary(baos.toByteArray());
        //return new String(Base64.encode(baos.toByteArray()));
        //return new String(baos.toByteArray());
    }


    public static <T> List<T> fromCsv(Class<T> clazz, String source) {
        try {
            return fromCsvImp(clazz, source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static <T> List<T> fromCsvImp(Class<T> clazz, String source) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<T> result = new ArrayList<>();
        String[] rows = source.split(Pattern.quote("\n"));

        Map<Integer, String> fieldPosition = new TreeMap<>();
        String[] first = rows[0].split(Pattern.quote(";"));

        for (int i = 0; i < first.length; i++) {
            fieldPosition.put(i, first[i].trim());
        }

        for (int i = 1; i < rows.length; i++) {
            String row = rows[i];
            String[] cells = row.split(Pattern.quote(";"));
            T fi = clazz.
                    getDeclaredConstructor().
                    newInstance();
            result.add(fi);

            BeanMap bm = BeanMap.create(fi);
            for (int c = 0, j = fieldPosition.size(); c < j; c++) {
                if (c >= cells.length) {
                    continue;
                }
                String name = fieldPosition.get(c);
                if (name == null || !bm.containsKey(name)) {
                    continue;
                }
                Class<?> type = bm.getPropertyType(name);

                if (type.equals(Boolean.TYPE)) {
                    String part = cells[c].trim();
                    boolean b = "yes".equalsIgnoreCase(part) || "true".equalsIgnoreCase(part);
                    bm.put(name, b);
                } else {
                    bm.put(name, cells[c].trim());
                }
            }

        }

        return result;
    }

    public static String toBeanText(Collection keys, Collection<? extends Object> items) {
        Collection<Map> itemz = new HashSet<>();
        for (Object item : items) {
            itemz.add((Map) BeanMap.create(item));
        }
        return toText(keys, itemz);
    }

    public static String toText(Collection keys, Collection<? extends Map> items) {
        StringBuilder sb = new StringBuilder();
        final List<String> heading = new ArrayList<>();
        for (Object key : keys) {
            heading.add(String.valueOf(key));
        }
        sb.append(String.join(";", heading));
        sb.append("\n");
        for (Map<?, ?> item : items) {
            final List<String> row = new ArrayList<>();
            for (Object key : keys) {
                Object value = item.get(key);
                if (value != null && !value.toString().trim().isEmpty()) {
                    row.add(String.valueOf(value));
                } else {
                    row.add("");
                }
            }
            sb.append(String.join(";", row));
            sb.append("\n");
        }
        return sb.toString();
    }

}
