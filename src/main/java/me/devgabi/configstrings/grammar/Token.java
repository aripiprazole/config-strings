package me.devgabi.configstrings.grammar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Token {
    @NotNull
    private final TokenType type;

    @Nullable
    private final Object literal;

    private final int position;

    public Token(@NotNull TokenType type, int position, @Nullable Object literal) {
        this.type = type;
        this.position = position;
        this.literal = literal;
    }

    public @NotNull TokenType getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public @Nullable Object getLiteral() {
        return literal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return position == token.position && type == token.type && Objects.equals(literal, token.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, position, literal);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", position=" + position +
                ", literal=" + literal +
                '}';
    }
}
