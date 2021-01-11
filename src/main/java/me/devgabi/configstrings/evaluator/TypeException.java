package me.devgabi.configstrings.evaluator;

import org.jetbrains.annotations.NotNull;

public final class TypeException extends RuntimeException {
    private final @NotNull String expected;
    private final @NotNull String actual;

    public TypeException(@NotNull String expected, @NotNull String actual) {
        super("Expecting type " + expected + " but got " + actual);
        this.expected = expected;
        this.actual = actual;
    }

    public @NotNull String getActual() {
        return actual;
    }

    public @NotNull String getExpected() {
        return expected;
    }
}
