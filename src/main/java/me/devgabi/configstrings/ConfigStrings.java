package me.devgabi.configstrings;

import me.devgabi.configstrings.evaluator.Evaluator;
import me.devgabi.configstrings.grammar.Expr;
import me.devgabi.configstrings.grammar.parser.Parser;
import me.devgabi.configstrings.grammar.lexer.Lexer;
import me.devgabi.configstrings.value.*;
import me.devgabi.configstrings.value.ConfigFunction;
import me.devgabi.configstrings.value.ConfigNumeric;
import me.devgabi.configstrings.value.ConfigString;
import me.devgabi.configstrings.value.ConfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Code strings "string" language
 */
@SuppressWarnings({"CodeBlock2Expr", "unused"})
public final class ConfigStrings {
    /**
     * Returns the builtin environment
     *
     * @return the builtin functions for ConfigStrings
     */
    public static @NotNull Map<String, ConfigValue> builtin() {
        return new HashMap<>(BUILTIN);
    }

    /**
     * Parses code with lexer flexible option, that will not
     * throw errors when find unexpected characters
     *
     * @param code the target code
     * @return the parsed expression
     */
    public static @NotNull Expr parse(final String code) {
        return parse(code, true);
    }

    /**
     * Parses the code and return the expression
     *
     * @param code     the target code
     * @param flexible if the lexer is flexible
     * @return the parsed expression
     */
    public static @NotNull Expr parse(final String code, final boolean flexible) {
        final Lexer lexer = new Lexer(code, flexible);
        final Parser parser = new Parser(lexer.lex());

        return parser.parse();
    }

    /**
     * Evaluates a expression into a string
     *
     * @param expr        a expression
     * @param environment a map environment
     * @return a evaluated string
     */
    public static @NotNull String evaluate(final Expr expr, final Map<String, ConfigValue> environment) {
        return new Evaluator(environment).visit(expr).toString();
    }

    private static final @NotNull Map<String, ConfigValue> BUILTIN = new HashMap<>();

    static {
        BUILTIN.put("lower", ConfigFunction.of(1, arguments -> {
            return ConfigString.of(
                    arguments.get(0)
                            .cast(ConfigString.class)
                            .unwrap()
                            .toLowerCase(Locale.ROOT)
            );
        }));

        BUILTIN.put("upper", ConfigFunction.of(1, arguments -> {
            return ConfigString.of(
                    arguments.get(0)
                            .cast(ConfigString.class)
                            .unwrap()
                            .toUpperCase(Locale.ROOT)
            );
        }));

        BUILTIN.put("sqrt", ConfigFunction.of(1, arguments -> {
            return ConfigNumeric.of(Math.sqrt(arguments.get(0).cast(ConfigNumeric.class).unwrap()));
        }));

        BUILTIN.put("cbrt", ConfigFunction.of(1, arguments -> {
            return ConfigNumeric.of(Math.cbrt(arguments.get(0).cast(ConfigNumeric.class).unwrap()));
        }));
    }
}
