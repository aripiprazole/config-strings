package me.devgabi.configstrings.grammar.parser;

import me.devgabi.configstrings.grammar.TokenType;

public final class ParseViolation extends RuntimeException {
    private final int index;
    private final TokenType expected;
    private final TokenType actual;

    public ParseViolation(int index, TokenType expected, TokenType actual, String message) {
        super(message);
        this.index = index;
        this.expected = expected;
        this.actual = actual;
    }

    public ParseViolation(int index, TokenType expected, TokenType actual) {
        super("Expected " + expected + " but found " + actual + " in index " + index);
        this.index = index;
        this.expected = expected;
        this.actual = actual;
    }

    public int getIndex() {
        return index;
    }

    public TokenType getExpected() {
        return expected;
    }

    public TokenType getActual() {
        return actual;
    }

}
