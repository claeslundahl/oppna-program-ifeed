package se.vgregion.common.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Json {

    private static Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            Expose expose = f.getAnnotation(Expose.class);
            String getterNamePostfix = (f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1));
            try {
                f.getDeclaringClass().getMethod("get" + getterNamePostfix);
            } catch (NoSuchMethodException e) {
                try {
                    f.getDeclaringClass().getMethod("is" + getterNamePostfix);
                } catch (NoSuchMethodException e1) {
                    return true;
                }
            }
            if (expose != null) {
                if (!expose.serialize()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }).setDateFormat("yyyy-MM-dd HH:mm:ss SSS").create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T toObject(Class<T> clazz, String json) {
        return gson.fromJson(json, clazz);
    }

    public static <T> List<T> toObjects(final Class<T> clazz, String json) {
        Type tt = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return gson.fromJson(json, tt);
    }

}
