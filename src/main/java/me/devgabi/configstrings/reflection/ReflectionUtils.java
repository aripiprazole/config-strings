package me.devgabi.configstrings.reflection;

import me.devgabi.configstrings.value.ConfigValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionUtils {
    public static Map<String, ConfigValue> transformObjectToPropertyMap(final Object object) {
        final Map<String, ConfigValue> dest = new HashMap<>();
        final Class<?> aClass = object.getClass();

        for (final Field field : aClass.getDeclaredFields()) {
            final String name = field.getName();
            try {
                final Object valueObject = field.get(object);
                final ConfigValue value = ConfigValue.of(valueObject);

                dest.put(name, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return dest;
    }

}
