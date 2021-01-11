package me.devgabi.configstrings.value;

import me.devgabi.configstrings.reflection.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ConfigObject extends ConfigValue {
    public static final String TYPE_NAME = "object";

    private final @NotNull Map<String,ConfigValue> properties;

    private ConfigObject(@NotNull Map<String, ConfigValue> properties) {
        this.properties = properties;
    }

    public ConfigValue getMember(final String member) {
        return properties.get(member);
    }

    @Override
    public @NotNull Object unwrap() {
        return properties;
    }

    @Override
    public @NotNull String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public String toString() {
        return "object";
    }

    public static @NotNull ConfigObject of(final @NotNull Map<String, ConfigValue> properties) {
        return new ConfigObject(properties);
    }

    public static @NotNull ConfigObject of(final Object object) {
        return new ConfigObject(ReflectionUtils.transformObjectToPropertyMap(object));
    }
}
