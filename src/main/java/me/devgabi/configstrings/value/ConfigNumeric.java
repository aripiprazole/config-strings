package me.devgabi.configstrings.value;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ConfigNumeric extends ConfigValue {
    public static final String TYPE_NAME = "numeric";

    private final double numeric;

    private ConfigNumeric(double numeric) {
        this.numeric = numeric;
    }

    @Override
    public @NotNull Double unwrap() {
        return numeric;
    }

    @Override
    public @NotNull String getTypeName() {
        return "numeric";
    }

    public static @NotNull ConfigNumeric of(final double numeric) {
        return new ConfigNumeric(numeric);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigNumeric that = (ConfigNumeric) o;
        return Double.compare(that.numeric, numeric) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeric);
    }

    @Override
    public String toString() {
        return String.valueOf(numeric);
    }
}
