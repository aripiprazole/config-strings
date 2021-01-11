package me.devgabi.configstrings.grammar;

public enum TokenType {
    // symbols
    L_BRACKET, R_BRACKET,
    L_PAREN, R_PAREN, ARROW_LEFT,

    // binary symbols
    BANG, PLUS, STAR, SLASH, MINUS,

    // logical symbols
    GREATER, GREATER_EQUALS, EQUALS_EQUALS,
    LESS, LESS_EQUALS, BANG_EQUALS, ELVIS,
    OR, AND,

    // literals
    PRIMARY, IDENTIFIER, NUMERIC, STRING,
    TRUE, FALSE, NULL,

    // specials
    INTERPOLATION_START, INTERPOLATION_END,
    STRING_END, EOF
}
