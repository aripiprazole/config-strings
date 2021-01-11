package me.devgabi.configstrings.value;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ConfigNull extends ConfigValue {
    public static final String TYPE_NAME = "null";
    public static final ConfigNull INSTANCE = new ConfigNull();

    private ConfigNull() {}

    @Override
    public @Nullable Object unwrap() {
        return null;
    }

    @Override
    public @NotNull String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public String toString() {
        return "null";
    }
}
