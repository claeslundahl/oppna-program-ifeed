package se.vgregion.common.utils;

import java.lang.reflect.Field;
import java.util.*;

public class JavaAttributeMap extends AbstractMap<String, Object> {

  private final Object source;

  private final List<Field> fields = new ArrayList<>();

  private final Map<String, Field> named = new HashMap<>();

  public JavaAttributeMap(Object source) {
    super();
    this.source = source;
    initFields(source.getClass(), fields);
    for (Field field : fields) {
      named.put(field.getName(), field);
      field.setAccessible(true);
    }
  }

  private void initFields(Class clazz, List<Field> target) {
    if (clazz == null)
      return;
    target.addAll(Arrays.asList(clazz.getDeclaredFields()));
    initFields(clazz.getSuperclass(), target);
  }


  @Override
  public Set<Entry<String, Object>> entrySet() {
    Set<Entry<String, Object>> result = new HashSet<>();
    for (final String k : keySet()) {
      Entry<String, Object> item = new Entry<String, Object>() {
        @Override
        public String getKey() {
          return k;
        }

        @Override
        public Object getValue() {
          return get(k);
        }

        @Override
        public Object setValue(Object value) {
          return put(k, value);
        }
      };
      result.add(item);
    }
    return Collections.unmodifiableSet(result);
  }

  @Override
  public Object get(Object key) {
    if (!named.containsKey(key))
      throw new IllegalArgumentException(source.getClass().getName()
          + " does not contains attribute "
          + key + " only these " + named.keySet());
    try {
      Field field = named.get(key);

      return field.get(source);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Object put(String key, Object value) {
    Object result = get(key);
    try {
      Field field = named.get(key);
      field.set(source, value);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  @Override
  public Set<String> keySet() {
    return named.keySet();
  }

  public List<Field> getFields() {
    return fields;
  }

  public Field getField(String withThatName) {
    return named.get(withThatName);
  }

}
