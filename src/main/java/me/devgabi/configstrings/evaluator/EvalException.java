package me.devgabi.configstrings.evaluator;

import org.jetbrains.annotations.NotNull;

public final class EvalException extends RuntimeException {
    public EvalException(@NotNull String message) {
        super(message);
    }

    public EvalException(@NotNull Exception exception) {
        super(exception);
    }
}
