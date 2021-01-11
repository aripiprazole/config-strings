package me.devgabi.configstrings.value;

import me.devgabi.configstrings.evaluator.TypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class ConfigValue {
    ConfigValue() {
    }

    public <@NotNull T extends ConfigValue> T cast(final Class<T> tClass) {
        if(!tClass.isAssignableFrom(getClass())) {
            throw new TypeException(tClass.getTypeName(), getTypeName());
        }

        //noinspection unchecked
        return (T) this;
    }

    public boolean isNull() {
        return this instanceof ConfigNull;
    }

    public abstract @Nullable Object unwrap();

    public abstract @NotNull String getTypeName();

    public static @NotNull ConfigValue of(final @Nullable Object object) {
        if (object == null) {
            return ConfigNull.INSTANCE;
        } else if (object instanceof String) {
            return ConfigString.of((String) object);
        } else if (object instanceof Boolean) {
            return ConfigBool.of(((Boolean) object).booleanValue());
        } else if (object instanceof Double) {
            return ConfigNumeric.of(((Double) object).doubleValue());
        } else if (object instanceof Integer) {
            return ConfigNumeric.of(((Integer) object).doubleValue());
        } else if (object instanceof Short) {
            return ConfigNumeric.of(((Short) object).doubleValue());
        } else if (object instanceof Byte) {
            return ConfigNumeric.of(((Byte) object).doubleValue());
        } else if (object instanceof Long) {
            return ConfigNumeric.of(((Long) object).doubleValue());
        } else if (object instanceof Float) {
            return ConfigNumeric.of(((Float) object).doubleValue());
        } else if (object instanceof AtomicInteger) {
            return ConfigNumeric.of(((AtomicInteger) object).doubleValue());
        } else if (object instanceof AtomicLong) {
            return ConfigNumeric.of(((AtomicLong) object).doubleValue());
        } else {
            return ConfigObject.of(object);
        }
    }
}
