package me.devgabi.configstrings.grammar.parser;

import me.devgabi.configstrings.grammar.TokenType;
import org.jetbrains.annotations.NotNull;

public final class ParseException extends RuntimeException {
    private final int index;
    private final @NotNull TokenType expected;
    private final @NotNull TokenType actual;

    public ParseException(int index, @NotNull TokenType expected, @NotNull TokenType actual, @NotNull String message) {
        super(message);
        this.index = index;
        this.expected = expected;
        this.actual = actual;
    }

    public ParseException(int index, @NotNull TokenType expected, @NotNull TokenType actual) {
        super("Expected " + expected + " but found " + actual + " in index " + index);
        this.index = index;
        this.expected = expected;
        this.actual = actual;
    }

    public int getIndex() {
        return index;
    }

    public @NotNull TokenType getExpected() {
        return expected;
    }

    public @NotNull TokenType getActual() {
        return actual;
    }
}
