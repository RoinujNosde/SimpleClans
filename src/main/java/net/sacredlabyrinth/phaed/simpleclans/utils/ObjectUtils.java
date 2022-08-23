package net.sacredlabyrinth.phaed.simpleclans.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

public class ObjectUtils {

    private ObjectUtils() {
    }

    public static void updateFields(Object origin, Object destination) throws IllegalAccessException {
        if (origin.getClass() != destination.getClass()) {
            throw new IllegalArgumentException("origin and destination must be of the same type");
        }
        Field[] fields = origin.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (Modifier.isFinal(field.getModifiers())) {
                copyValues(field, field.get(origin), field.get(destination));
                continue;
            }
            field.set(destination, field.get(origin));
        }
    }

    private static boolean isPrimitive(Field field) {
        Class<?> type = field.getType();
        return type.isPrimitive() || Number.class.isAssignableFrom(type) || type == String.class;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void copyValues(Field field, Object originValue, Object destValue) {
        if (isPrimitive(field)) {
            return;
        }
        if (originValue instanceof Collection) {
            Collection<?> destColl = (Collection<?>) destValue;
            destColl.clear();
            destColl.addAll((Collection) originValue);
            return;
        }
        if (originValue instanceof Map) {
            Map<?, ?> destMap = (Map<?, ?>) destValue;
            destMap.clear();
            destMap.putAll((Map) originValue);
            return;
        }
        throw new UnsupportedOperationException(String.format("unknown field type: %s", originValue.getClass()));
    }

}
