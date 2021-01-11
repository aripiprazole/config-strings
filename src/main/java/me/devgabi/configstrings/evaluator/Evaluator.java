package me.devgabi.configstrings.evaluator;

import me.devgabi.configstrings.grammar.Expr;
import me.devgabi.configstrings.grammar.TokenType;
import com.lorenzoog.configstrings.value.*;
import me.devgabi.configstrings.value.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Evaluator implements Expr.Visitor<ConfigValue> {
    private final Map<String, ConfigValue> environment;

    public Evaluator(Map<String, ConfigValue> environment) {
        this.environment = environment;
    }

    @Override
    public ConfigValue visit(Expr expr) {
        try {
            return expr.accept(this);
        } catch (final RuntimeException e) {
            throw new EvalException(e);
        }
    }

    @Override
    public ConfigValue visitConstExpr(final Expr.Const expr) {
        return ConfigValue.of(expr.getLiteral());
    }

    @Override
    public ConfigValue visitGroupExpr(final Expr.Group expr) {
        return visit(expr.getExpr());
    }

    @Override
    public ConfigValue visitUnaryExpr(final Expr.Unary expr) {
        final TokenType op = expr.getOp().getType();

        switch (op) {
            case BANG: {
                final ConfigValue rhs = visit(expr.getRhs());
                if (!(rhs instanceof ConfigBool)) {
                    throw new TypeException(ConfigBool.TYPE_NAME, rhs.getTypeName());
                }
                final ConfigBool numeric = (ConfigBool) rhs;

                return ConfigBool.of(!numeric.unwrap());
            }
            case MINUS: {
                final ConfigValue rhs = visit(expr.getRhs());
                if (!(rhs instanceof ConfigNumeric)) {
                    throw new TypeException(ConfigNumeric.TYPE_NAME, rhs.getTypeName());
                }
                final ConfigNumeric numeric = (ConfigNumeric) rhs;

                return ConfigNumeric.of(-numeric.unwrap());
            }
        }

        throw new EvalException("Unsupported unary operation " + op);
    }

    @Override
    public ConfigValue visitLogicalExpr(final Expr.Logical expr) {
        final TokenType op = expr.getOp().getType();

        final ConfigValue lhsValue = visit(expr.getLhs());
        final ConfigValue rhsValue = visit(expr.getRhs());

        if (expr.acceptAny()) {
            switch (op) {
                case EQUALS_EQUALS:
                    return ConfigBool.of(lhsValue.equals(rhsValue));

                case BANG_EQUALS:
                    return ConfigBool.of(!lhsValue.equals(rhsValue));
            }
        } else if (expr.acceptBool()) {
            if (!(lhsValue instanceof ConfigBool)) {
                throw new TypeException(ConfigBool.TYPE_NAME, lhsValue.getTypeName());
            }

            if (!(rhsValue instanceof ConfigBool)) {
                throw new TypeException(ConfigBool.TYPE_NAME, rhsValue.getTypeName());
            }

            final ConfigBool lhs = (ConfigBool) lhsValue;
            final ConfigBool rhs = (ConfigBool) rhsValue;

            switch (op) {
                case AND:
                    return ConfigBool.of(lhs.unwrap() && rhs.unwrap());

                case OR:
                    return ConfigBool.of(lhs.unwrap() || rhs.unwrap());
            }
        } else if (expr.acceptNumeric()) {
            if (!(lhsValue instanceof ConfigNumeric)) {
                throw new TypeException(ConfigNumeric.TYPE_NAME, lhsValue.getTypeName());
            }

            if (!(rhsValue instanceof ConfigNumeric)) {
                throw new TypeException(ConfigNumeric.TYPE_NAME, rhsValue.getTypeName());
            }

            final ConfigNumeric lhs = (ConfigNumeric) lhsValue;
            final ConfigNumeric rhs = (ConfigNumeric) rhsValue;

            switch (op) {
                case GREATER_EQUALS:
                    return ConfigBool.of(lhs.unwrap() >= rhs.unwrap());

                case GREATER:
                    return ConfigBool.of(lhs.unwrap() > rhs.unwrap());

                case LESS:
                    return ConfigBool.of(lhs.unwrap() < rhs.unwrap());

                case LESS_EQUALS:
                    return ConfigBool.of(lhs.unwrap() <= rhs.unwrap());
            }
        }


        throw new EvalException("Unsupported logical operation " + op);
    }

    @Override
    public ConfigValue visitBinaryExpr(final Expr.Binary expr) {
        final TokenType op = expr.getOp().getType();

        final ConfigValue rhsValue = visit(expr.getRhs());
        if (!(rhsValue instanceof ConfigNumeric)) {
            throw new TypeException(ConfigNumeric.TYPE_NAME, rhsValue.getTypeName());
        }

        final ConfigValue lhsValue = visit(expr.getLhs());
        if (lhsValue instanceof ConfigString && op == TokenType.PLUS) {
            return ConfigString.of(((ConfigString) lhsValue).unwrap() + rhsValue.toString());
        }

        if (!(lhsValue instanceof ConfigNumeric)) {
            throw new TypeException(ConfigNumeric.TYPE_NAME, lhsValue.getTypeName());
        }

        final ConfigNumeric lhs = (ConfigNumeric) lhsValue;
        final ConfigNumeric rhs = (ConfigNumeric) rhsValue;

        switch (op) {
            case MINUS:
                return ConfigNumeric.of(lhs.unwrap() - rhs.unwrap());

            case STAR:
                return ConfigNumeric.of(lhs.unwrap() * rhs.unwrap());

            case SLASH:
                return ConfigNumeric.of(lhs.unwrap() / rhs.unwrap());

            case PLUS:
                return ConfigNumeric.of(lhs.unwrap() + rhs.unwrap());

            default:
                break;
        }

        throw new EvalException("Unsupported binary operation " + op);
    }

    @Override
    public ConfigValue visitVarExpr(final Expr.Var expr) {
        final Object name = expr.getName().getLiteral();
        if (name == null) {
            throw new EvalException("Expecting accessing a named variable");
        }

        final ConfigValue value = environment.get(name.toString());
        if (value == null) {
            return ConfigNull.INSTANCE;
        }

        return value;
    }

    @Override
    public ConfigValue visitCallExpr(final Expr.Call expr) {
        final ConfigValue value = visit(expr.getCallee());
        if (!(value instanceof ConfigFunction)) {
            throw new TypeException(ConfigFunction.TYPE_NAME, value.getTypeName());
        }
        final ConfigFunction function = (ConfigFunction) value;
        final List<ConfigValue> arguments = expr.getArguments().stream()
                .map(this::visit)
                .collect(Collectors.toList());

        if (function.arity() > arguments.size()) {
            throw new EvalException("Incorrect function arity");
        }

        return function.invoke(arguments);
    }

    @Override
    public ConfigValue visitGetExpr(final Expr.Get expr) {
        final ConfigValue value = visit(expr.getReceiver());
        if (!(value instanceof ConfigObject)) {
            throw new TypeException(ConfigObject.TYPE_NAME, value.getTypeName());
        }
        final ConfigObject object = (ConfigObject) value;

        final Object name = expr.getMember().getLiteral();
        if (name == null) {
            throw new EvalException("Expecting a named member");
        }

        return object.getMember(name.toString());
    }

    @Override
    public ConfigValue visitElvisExpr(final Expr.Elvis expr) {
        final ConfigValue lhs = visit(expr.getLhs());

        if (lhs.isNull()) {
            return visit(expr.getRhs());
        } else {
            return lhs;
        }
    }

    @Override
    public ConfigValue visitTextExpr(final Expr.Text text) {
        final Object lhs = text.getLhs().getLiteral();
        if (lhs == null) {
            throw new EvalException("LHS is null");
        }

        final StringBuilder builder = new StringBuilder(lhs.toString());
        for (final Expr element : text.getInterpolation()) {
            builder.append(visit(element).toString());
        }

        return ConfigString.of(builder.toString());
    }
}
