package me.devgabi.configstrings.grammar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Expr {
    private Expr() {}

    public interface Visitor<T> {
        default T visit(final Expr expr) {
            return expr.accept(this);
        }

        T visitConstExpr(final Const expr);

        T visitGroupExpr(final Group expr);

        T visitUnaryExpr(final Unary expr);

        T visitLogicalExpr(final Logical expr);

        T visitBinaryExpr(final Binary expr);

        T visitVarExpr(final Var expr);

        T visitCallExpr(final Call expr);

        T visitGetExpr(final Get expr);

        T visitElvisExpr(final Elvis expr);

        T visitTextExpr(final Text text);
    }

    public abstract <T> @NotNull T accept(final Visitor<T> visitor);

    public static final class Const extends Expr {
        private final @Nullable Object literal;

        public Const(@Nullable Object literal) {
            this.literal = literal;
        }

        public @Nullable Object getLiteral() {
            return literal;
        }

        @Override
        public String toString() {
            return "Const{" +
                    "literal=" + literal +
                    '}';
        }

        @Override
        public <T> @NotNull T accept(Visitor<T> visitor) {
            return visitor.visitConstExpr(this);
        }
    }

    public static final class Group extends Expr {
        private final @NotNull Expr expr;

        public Group(@NotNull Expr expr) {
            this.expr = expr;
        }

        public @NotNull Expr getExpr() {
            return expr;
        }

        @Override
        public String toString() {
            return "Group{" +
                    "expr=" + expr +
                    '}';
        }

        @Override
        public <T> @NotNull T accept(Visitor<T> visitor) {
            return visitor.visitGroupExpr(this);
        }
    }

    public static final class Unary extends Expr {
        private final @NotNull Token op;
        private final @NotNull Expr rhs;

        public Unary(@NotNull Token op, @NotNull Expr rhs) {
            this.op = op;
            this.rhs = rhs;
        }

        public @NotNull Expr getRhs() {
            return rhs;
        }

        public @NotNull Token getOp() {
            return op;
        }

        @Override
        public String toString() {
            return "Unary{" +
                    "op=" + op +
                    ", rhs=" + rhs +
                    '}';
        }

        @Override
        public <T> @NotNull T accept(Visitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    public static final class Logical extends Expr {
        private static final List<TokenType> BOOL_OPS = new ArrayList<>();
        private static final List<TokenType> ANY_OPS = new ArrayList<>();
        private static final List<TokenType> NUMERIC_OPS = new ArrayList<>();

        static {
            BOOL_OPS.add(TokenType.AND);
            BOOL_OPS.add(TokenType.OR);

            ANY_OPS.add(TokenType.EQUALS_EQUALS);
            ANY_OPS.add(TokenType.BANG_EQUALS);

            NUMERIC_OPS.add(TokenType.GREATER);
            NUMERIC_OPS.add(TokenType.GREATER_EQUALS);
            NUMERIC_OPS.add(TokenType.LESS);
            NUMERIC_OPS.add(TokenType.LESS_EQUALS);
        }

        private final @NotNull Expr lhs;
        private final @NotNull Token op;
        private final @NotNull Expr rhs;

        public Logical(@NotNull Expr lhs, @NotNull Token op, @NotNull Expr rhs) {
            this.lhs = lhs;
            this.op = op;
            this.rhs = rhs;
        }

        public boolean acceptBool() {
            return BOOL_OPS.contains(op.getType());
        }

        public boolean acceptNumeric() {
            return NUMERIC_OPS.contains(op.getType());
        }

        public boolean acceptAny() {
            return ANY_OPS.contains(op.getType());
        }

        public @NotNull Expr getRhs() {
            return rhs;
        }

        public @NotNull Token getOp() {
            return op;
        }

        public @NotNull Expr getLhs() {
            return lhs;
        }

        @Override
        public String toString() {
            return "Logical{" +
                    "lhs=" + lhs +
                    ", op=" + op +
                    ", rhs=" + rhs +
                    '}';
        }

        @Override
        public <T> @NotNull T accept(Visitor<T> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    public static final class Binary extends Expr {
        private final @NotNull Expr lhs;
        private final @NotNull Token op;
        private final @NotNull Expr rhs;

        public Binary(@NotNull Expr lhs, @NotNull Token op, @NotNull Expr rhs) {
            this.lhs = lhs;
            this.op = op;
            this.rhs = rhs;
        }

        public @NotNull Expr getRhs() {
            return rhs;
        }

        public @NotNull Token getOp() {
            return op;
        }

        public @NotNull Expr getLhs() {
            return lhs;
        }

        @Override
        public String toString() {
            return "Binary{" +
                    "lhs=" + lhs +
                    ", op=" + op +
                    ", rhs=" + rhs +
                    '}';
        }

        @Override
        public <T> @NotNull T accept(Visitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    public static final class Var extends Expr {
        private final @NotNull Token name;

        public Var(@NotNull Token name) {
            this.name = name;
        }

        public @NotNull Token getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Var{" +
                    "name=" + name +
                    '}';
        }

        @Override
        public <T> @NotNull T accept(Visitor<T> visitor) {
            return visitor.visitVarExpr(this);
        }
    }

    public static final class Call extends Expr {
        private final @NotNull Expr callee;
        private final @NotNull List<@NotNull Expr> arguments;

        public Call(@NotNull Expr callee, @NotNull List<Expr> arguments) {
            this.callee = callee;
            this.arguments = arguments;
        }

        public @NotNull Expr getCallee() {
            return callee;
        }

        public @NotNull List<@NotNull Expr> getArguments() {
            return arguments;
        }

        @Override
        public String toString() {
            return "Call{" +
                    "name=" + callee +
                    ", arguments=" + arguments +
                    '}';
        }

        @Override
        public <T> @NotNull T accept(Visitor<T> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

    public static final class Get extends Expr {
        private final @NotNull Expr receiver;
        private final @NotNull Token member;

        public Get(@NotNull Expr receiver, @NotNull Token member) {
            this.receiver = receiver;
            this.member = member;
        }

        @Override
        public <T> @NotNull T accept(Visitor<T> visitor) {
            return visitor.visitGetExpr(this);
        }

        public @NotNull Expr getReceiver() {
            return receiver;
        }

        public @NotNull Token getMember() {
            return member;
        }

        @Override
        public String toString() {
            return "Get{" +
                    "receiver=" + receiver +
                    ", member=" + member +
                    '}';
        }
    }

    public static final class Elvis extends Expr {
        private final @NotNull Expr lhs;
        private final @NotNull Expr rhs;

        public Elvis(@NotNull Expr lhs, @NotNull Expr rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public @NotNull Expr getLhs() {
            return lhs;
        }

        public @NotNull Expr getRhs() {
            return rhs;
        }

        @Override
        public <T> @NotNull T accept(Visitor<T> visitor) {
            return visitor.visitElvisExpr(this);
        }

        @Override
        public String toString() {
            return "Elvis{" +
                    "lhs=" + lhs +
                    ", rhs=" + rhs +
                    '}';
        }
    }

    public static final class Text extends Expr {
        private final @NotNull Token lhs;
        private final @NotNull List<Expr> interpolation;

        public Text(@NotNull Token lhs, @NotNull List<Expr> interpolation) {
            this.lhs = lhs;
            this.interpolation = interpolation;
        }

        public Text(@NotNull Token lhs) {
            this.lhs = lhs;
            this.interpolation = new ArrayList<>();
        }

        @Override
        public <T> @NotNull T accept(final Visitor<T> visitor) {
            return visitor.visitTextExpr(this);
        }

        public @NotNull Token getLhs() {
            return lhs;
        }

        public @NotNull List<Expr> getInterpolation() {
            return interpolation;
        }
    }
}
