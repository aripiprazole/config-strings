package me.devgabi.configstrings.value;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class ConfigFunction extends ConfigValue {
    public static final String TYPE_NAME = "function";

    private final int arity;
    private final Function<List<ConfigValue>, ConfigValue> function;

    private ConfigFunction(int arity, Function<List<ConfigValue>, ConfigValue> function) {
        this.arity = arity;
        this.function = function;
    }

    public ConfigValue invoke(final List<ConfigValue> arguments) {
        return function.apply(arguments);
    }

    public int arity() {
        return arity;
    }

    @Override
    public @NotNull Object unwrap() {
        return function;
    }

    @Override
    public @NotNull String getTypeName() {
        return TYPE_NAME;
    }

    public static @NotNull ConfigFunction of(final int arity, final Function<List<ConfigValue>, ConfigValue> function) {
        return new ConfigFunction(arity, function);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigFunction that = (ConfigFunction) o;
        return Objects.equals(function, that.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function);
    }

    @Override
    public String toString() {
        return "function";
    }

}
