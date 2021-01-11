package me.devgabi.configstrings.value;

import org.jetbrains.annotations.NotNull;

public final class ConfigString extends ConfigValue {
    public static final String TYPE_NAME = "string";
    private final @NotNull String string;

    private ConfigString(@NotNull String string) {
        this.string = string;
    }

    @Override
    public @NotNull String unwrap() {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public @NotNull String getTypeName() {
        return TYPE_NAME;
    }

    public static @NotNull ConfigString of(final @NotNull String string) {
        return new ConfigString(string);
    }
}
