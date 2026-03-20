package com.backandwhite.common.utils;

import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class FilterUtils {

    private FilterUtils() {
    }

    public static <T> Specification<T> buildSpecification(Class<T> entityClass, Map<String, Object> filters) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (filters != null && !filters.isEmpty()) {
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value == null) {
                        continue;
                    }
                    Class<?> fieldType = resolveFieldType(entityClass, key);
                    if (fieldType == null) {
                        continue;
                    }
                    Object parsed = convertValue(value, fieldType);
                    predicates.add(cb.equal(root.get(key), parsed));
                }
            }
            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    public static Class<?> resolveFieldType(Class<?> type, String fieldName) {
        Field field = findField(type, fieldName);
        return field == null ? null : field.getType();
    }

    public static Object convertValue(Object value, Class<?> type) {
        if (!(value instanceof String strValue)) {
            return value;
        }
        if (type.equals(String.class)) {
            return strValue;
        }
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return Integer.parseInt(strValue);
        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.parseLong(strValue);
        }
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Boolean.parseBoolean(strValue);
        }
        if (type.equals(Double.class) || type.equals(double.class)) {
            return Double.parseDouble(strValue);
        }
        if (type.equals(Float.class) || type.equals(float.class)) {
            return Float.parseFloat(strValue);
        }
        if (type.equals(UUID.class)) {
            return UUID.fromString(strValue);
        }
        if (type.equals(LocalDate.class)) {
            return LocalDate.parse(strValue);
        }
        if (type.equals(LocalDateTime.class)) {
            return LocalDateTime.parse(strValue);
        }
        if (type.isEnum()) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Class<? extends Enum> enumType = (Class<? extends Enum>) type;
            return Enum.valueOf(enumType, strValue);
        }
        return strValue;
    }

    private static Field findField(Class<?> type, String fieldName) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ex) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
