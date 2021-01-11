package me.devgabi.configstrings.grammar.lexer;

import me.devgabi.configstrings.grammar.Token;
import me.devgabi.configstrings.grammar.TokenType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Lexer {
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("true", TokenType.TRUE);
        KEYWORDS.put("false", TokenType.FALSE);
        KEYWORDS.put("null", TokenType.NULL);
    }

    private final @NotNull String input;
    private final @NotNull List<Token> tokens;
    private final @NotNull List<LexViolation> violations;
    private final boolean flexible;
    private int index;
    private int start;

    public Lexer(@NotNull String input, boolean flexible) {
        this.input = input;
        this.tokens = new ArrayList<>();
        this.flexible = flexible;
        this.violations = new ArrayList<>();
    }

    public @NotNull List<Token> lex() {
        while (!isAtEnd()) {
            start = index;

            lexToken();
        }

        addToken(TokenType.EOF);

        if (!flexible) {
            throw new LexException(violations);
        }

        return tokens;
    }

    public List<LexViolation> getViolations() {
        return violations;
    }

    private void lexToken() {
        final char c = advance();

        switch (c) {
            case '+':
                addToken(TokenType.PLUS);
                break;

            case '-':
                if (match('>')) {
                    addToken(TokenType.ARROW_LEFT);
                } else {
                    addToken(TokenType.MINUS);
                }
                break;

            case '*':
                addToken(TokenType.STAR);
                break;

            case '!':
                if (match('=')) {
                    addToken(TokenType.EQUALS_EQUALS);
                } else {
                    addToken(TokenType.BANG);
                }
                break;

            case '{':
                addToken(TokenType.L_BRACKET);
                break;

            case '}':
                addToken(TokenType.R_BRACKET);
                break;

            case '(':
                addToken(TokenType.L_PAREN);
                break;

            case ')':
                addToken(TokenType.R_PAREN);
                break;

            case '|':
                if (match('|')) {
                    addToken(TokenType.OR);
                } else {
                    addViolation(c);
                }
                break;

            case '&':
                if (match('&')) {
                    addToken(TokenType.AND);
                } else {
                    addViolation(c);
                }
                break;

            case '=':
                if (match('=')) {
                    addToken(TokenType.EQUALS_EQUALS);
                } else {
                    addViolation(c);
                }

                break;

            case '>':
                if (match('=')) {
                    addToken(TokenType.GREATER_EQUALS);
                } else {
                    addToken(TokenType.GREATER);
                }
                break;

            case '<':
                if (match('=')) {
                    addToken(TokenType.LESS_EQUALS);
                } else {
                    addToken(TokenType.LESS);
                }
                break;

            case '?':
                if (match(':')) {
                    addToken(TokenType.ELVIS);
                } else {
                    addViolation(c);
                }
                break;

            case '"':
                lexString();

                break;

            case '\'':
                lexSimpleString();

                break;

            case ' ':
            case '\t':
            case '\n':
            case '\r':
                break;

            default:
                if (Character.isDigit(c)) {
                    lexNumeric();
                } else {
                    lexIdentifier();
                }

                break;
        }
    }

    private void lexInterpolation() {
        while (true) {
            if (peek() != '}' && peekNext() != '}') {
                lexToken();
            } else {
                addToken(TokenType.INTERPOLATION_END);
                advance();
                advance();
                break;
            }
        }
    }

    private void lexString() {
        start++;
        while (!isAtEnd() && peek() != '"') {
            index++;

            if (matchInterpolationStart()) {
                addToken(TokenType.STRING, input.substring(start, index - 2));
                start = index;
                addToken(TokenType.INTERPOLATION_START);
                lexInterpolation();
                start = index;
            }
        }

        advance(); // jump "
        addToken(TokenType.STRING, input.substring(start + 1, index - 1));
        addToken(TokenType.STRING_END);
    }

    private void lexSimpleString() {
        start++;
        while (!isAtEnd() && peek() != '\'') {
            index++;

            if (matchInterpolationStart()) {
                addToken(TokenType.STRING, input.substring(start, index - 2));
                start = index;
                addToken(TokenType.INTERPOLATION_START);
                lexInterpolation();
                start = index;
            }
        }

        advance(); // jump '

        addToken(TokenType.STRING, input.substring(start + 1, index - 1));
        addToken(TokenType.STRING_END);
    }

    private void lexIdentifier() {
        while (!isAtEnd() && Character.isLetterOrDigit(peek())) {
            index++;
        }

        final String identifier = input.substring(start, index);

        addToken(KEYWORDS.getOrDefault(identifier, TokenType.IDENTIFIER), identifier);
    }

    private void lexNumeric() {
        while (!isAtEnd() && Character.isDigit(peek())) {
            index++;
        }

        if (peek() == '.' && Character.isDigit(peekNext())) {
            while (!isAtEnd() && peek() != ' ') {
                index++;
            }
        }

        final String numeric = input.substring(start, index);

        addToken(TokenType.NUMERIC, Double.parseDouble(numeric));
    }

    private boolean matchInterpolationStart() {
        final boolean bool = peek() == '{' && peekNext() == '{';
        if (bool) {
            advance();
            advance(); // jump the {{
        }

        return bool;
    }

    private void addViolation(final char c) {
        violations.add(new LexViolation(c, index));
    }

    private char peek() {
        if (isAtEnd()) return '\0';

        return input.charAt(index);
    }

    private char advance() {
        index++;
        return input.charAt(index - 1);
    }

    private char peekNext() {
        if (index + 1 >= input.length()) return '\0';

        return input.charAt(index + 1);
    }

    private boolean match(char c) {
        if (isAtEnd()) return false;

        final boolean bool = input.charAt(index) == c;
        if (bool) {
            advance();
        }

        return bool;
    }

    private void addToken(TokenType type) {
        tokens.add(new Token(type, index, null));
    }

    private void addToken(TokenType type, Object literal) {
        tokens.add(new Token(type, index, literal));
    }

    private boolean isAtEnd() {
        return index >= input.length();
    }
}
