package me.devgabi.configstrings.grammar.lexer;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class LexException extends RuntimeException {
    private final @NotNull List<LexViolation> violations;

    public LexException(@NotNull List<LexViolation> violations) {
        super("Some violations was found when lexing the input");
        this.violations = violations;
    }

    public @NotNull List<LexViolation> getViolations() {
        return violations;
    }
}
