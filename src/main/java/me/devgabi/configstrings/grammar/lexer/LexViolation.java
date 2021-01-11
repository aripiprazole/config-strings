package me.devgabi.configstrings.grammar.lexer;

public final class LexViolation extends RuntimeException {
    private final char unexpectedChar;
    private final int index;

    public LexViolation(char unexpectedChar, int index) {
        super("Unexpected char " + unexpectedChar + " at " + index);
        this.unexpectedChar = unexpectedChar;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public char getUnexpectedChar() {
        return unexpectedChar;
    }
}
