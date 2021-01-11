package me.devgabi.configstrings.value;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ConfigBool extends ConfigValue {
    public static final String TYPE_NAME = "bool";

    private final boolean bool;

    private ConfigBool(boolean bool) {
        this.bool = bool;
    }

    @Override
    public @NotNull Boolean unwrap() {
        return bool;
    }

    @Override
    public @NotNull String getTypeName() {
        return TYPE_NAME;
    }

    public static @NotNull ConfigBool of(final boolean bool) {
        return new ConfigBool(bool);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigBool that = (ConfigBool) o;
        return bool == that.bool;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bool);
    }

    @Override
    public String toString() {
        return String.valueOf(bool);
    }
}
