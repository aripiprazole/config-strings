package me.devgabi.configstrings.grammar.parser;

import me.devgabi.configstrings.grammar.Expr;
import me.devgabi.configstrings.grammar.Token;
import me.devgabi.configstrings.grammar.TokenType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Parser {
    private final @NotNull List<Token> tokens;
    private final @NotNull List<ParseViolation> violations;
    private int index;

    public Parser(@NotNull List<Token> tokens) {
        this.tokens = tokens;
        this.violations = new ArrayList<>();
    }

    /**
     * Parses expression
     *
     * @return the parsed expr
     */
    @NotNull
    public Expr parse() {
        return elvis();
    }

    public @NotNull List<ParseViolation> getViolations() {
        return violations;
    }

    private Expr expr() {
        return elvis();
    }

    private Expr elvis() {
        Expr lhs = or();

        while (!isAtEnd() && match(TokenType.ELVIS)) {
            final Expr rhs = or();

            lhs = new Expr.Elvis(lhs, rhs);
        }

        return lhs;
    }

    private Expr or() {
        Expr lhs = and();

        while (!isAtEnd() && match(TokenType.OR)) {
            final Token op = previous();
            final Expr rhs = and();

            lhs = new Expr.Logical(lhs, op, rhs);
        }

        return lhs;
    }

    private Expr and() {
        Expr lhs = equality();

        while (!isAtEnd() && match(TokenType.AND)) {
            final Token op = previous();
            final Expr rhs = equality();

            lhs = new Expr.Logical(lhs, op, rhs);
        }

        return lhs;
    }

    private Expr equality() {
        Expr lhs = comparison();

        while (!isAtEnd() && match(TokenType.EQUALS_EQUALS, TokenType.BANG_EQUALS)) {
            final Token op = previous();
            final Expr rhs = comparison();

            lhs = new Expr.Logical(lhs, op, rhs);
        }

        return lhs;
    }

    private Expr comparison() {
        Expr lhs = term();

        while (
                !isAtEnd() && match(TokenType.LESS, TokenType.LESS_EQUALS, TokenType.GREATER_EQUALS, TokenType.GREATER)
        ) {
            final Token op = previous();
            final Expr rhs = term();

            lhs = new Expr.Logical(lhs, op, rhs);
        }

        return lhs;
    }

    private Expr term() {
        Expr lhs = factor();

        while (!isAtEnd() && match(TokenType.PLUS, TokenType.MINUS)) {
            final Token op = previous();
            final Expr rhs = factor();

            lhs = new Expr.Logical(lhs, op, rhs);
        }

        return lhs;
    }

    private Expr factor() {
        Expr lhs = unary();

        while (!isAtEnd() && match(TokenType.STAR, TokenType.SLASH)) {
            final Token op = previous();
            final Expr rhs = unary();

            lhs = new Expr.Binary(lhs, op, rhs);
        }

        return lhs;
    }

    private Expr unary() {
        if (!isAtEnd() && match(TokenType.STAR, TokenType.SLASH)) {
            final Token op = previous();
            final Expr rhs = unary();

            return new Expr.Unary(op, rhs);
        }

        return call();
    }

    private Expr call() {
        Expr lhs = primary();

        while (true) {
            if (isAtEnd()) {
                break;
            } else if (match(TokenType.L_PAREN)) {
                lhs = finishCall(lhs);
            } else if (match(TokenType.ARROW_LEFT)) {
                lhs = finishGet(lhs);
            } else {
                break;
            }
        }

        return lhs;
    }

    private Expr finishGet(final Expr receiver) {
        final Token member = consume(TokenType.IDENTIFIER, "Expecting member name");

        return new Expr.Get(receiver, member);
    }

    private Expr finishCall(final Expr callee) {
        final List<Expr> arguments = new ArrayList<>();

        while (!check(TokenType.R_PAREN)) {
            arguments.add(expr());
        }

        consume(TokenType.R_PAREN, "Expecting final of call");

        return new Expr.Call(callee, arguments);
    }

    private Expr primary() {
        if (match(TokenType.NUMERIC)) {
            return new Expr.Const(previous().getLiteral());
        } else if (match(TokenType.STRING)) {
            return string();
        } else if (match(TokenType.IDENTIFIER)) {
            return new Expr.Var(previous());
        } else if (match(TokenType.FALSE)) {
            return new Expr.Const(false);
        } else if (match(TokenType.TRUE)) {
            return new Expr.Const(true);
        } else if (match(TokenType.NULL)) {
            return new Expr.Const(null);
        } else if (match(TokenType.L_PAREN)) {
            final Expr group = new Expr.Group(expr());

            consume(TokenType.R_PAREN, "Expecting final of group");

            return group;
        }

        throw new ParseException(index, TokenType.PRIMARY, peek().getType());
    }

    private Expr string() {
        final Token lhs = previous();
        final List<Expr> interpolation = new ArrayList<>();

        while (!isAtEnd() && !check(TokenType.STRING_END)) {
            if (match(TokenType.STRING)) {
                interpolation.add(new Expr.Text(previous()));
            } else if (match(TokenType.INTERPOLATION_START)) {
                interpolation.add(expr());

                consume(TokenType.INTERPOLATION_END, "Expecting end of interpolation");
            } else {
                advance();
            }
        }

        advance(); // jump string end

        return new Expr.Text(lhs, interpolation);
    }

    private boolean check(final TokenType type) {
        return peek().getType().equals(type);
    }

    private boolean match(final TokenType... types) {
        for (final TokenType type : types) {
            if (check(type)) {
                advance();

                return true;
            }
        }

        return false;
    }

    private Token consume(final TokenType type, final String errorMessage) {
        if (check(type)) {
            return advance();
        }

        throw new ParseException(index, type, peek().getType(), errorMessage);
    }

    private Token peek() {
        return tokens.get(index);
    }

    private Token previous() {
        return tokens.get(index - 1);
    }

    private Token advance() {
        if (!isAtEnd()) {
            index++;
        }
        return tokens.get(index - 1);
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private void addViolation(final int index, final TokenType expected, final TokenType actual) {
        violations.add(new ParseViolation(index, expected, actual));
    }

    private void addViolation(
            final int index,
            final TokenType expected,
            final TokenType actual,
            final String message
    ) {
        violations.add(new ParseViolation(index, expected, actual, message));
    }
}
