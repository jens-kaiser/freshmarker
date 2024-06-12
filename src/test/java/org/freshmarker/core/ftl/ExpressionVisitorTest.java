package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token;
import ftl.ast.AdditiveExpression;
import ftl.ast.AndExpression;
import ftl.ast.BaseExpression;
import ftl.ast.BooleanLiteral;
import ftl.ast.BuiltIn;
import ftl.ast.BuiltinVariable;
import ftl.ast.DefaultToExpression;
import ftl.ast.DotKey;
import ftl.ast.DynamicKey;
import ftl.ast.EqualityExpression;
import ftl.ast.Exists;
import ftl.ast.Expression;
import ftl.ast.HashLiteral;
import ftl.ast.ListLiteral;
import ftl.ast.MethodInvoke;
import ftl.ast.MultiplicativeExpression;
import ftl.ast.NotExpression;
import ftl.ast.NullLiteral;
import ftl.ast.NumberLiteral;
import ftl.ast.OrExpression;
import ftl.ast.ParameterList;
import ftl.ast.Parenthesis;
import ftl.ast.PrimaryExpression;
import ftl.ast.RangeExpression;
import ftl.ast.RelationalExpression;
import ftl.ast.StringLiteral;
import ftl.ast.UnaryPlusMinusExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpressionVisitorTest {
    private ExpressionVisitor<String, String> visitor;

    @BeforeEach
    void setUp() {
        visitor = new ExpressionVisitor<>() {
        };
    }

    @Test
    void visitNode() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((Node) null, ""));
    }

    @Test
    void visitToken() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((Token) null, ""));
    }

    @Test
    void visitExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((Expression) null, ""));
    }

    @Test
    void visitOrExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((OrExpression) null, ""));
    }

    @Test
    void visitAndExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((AndExpression) null, ""));
    }

    @Test
    void visitEqualityExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((EqualityExpression) null, ""));
    }

    @Test
    void visitRelationalExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((RelationalExpression) null, ""));
    }

    @Test
    void visitRangeExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((RangeExpression) null, ""));
    }

    @Test
    void visitAdditiveExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((AdditiveExpression) null, ""));
    }

    @Test
    void visitMultiplicativeExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((MultiplicativeExpression) null, ""));
    }

    @Test
    void visitUnaryPlusMinusExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((UnaryPlusMinusExpression) null, ""));
    }

    @Test
    void visitNotExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((NotExpression) null, ""));
    }

    @Test
    void visitPrimaryExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((PrimaryExpression) null, ""));
    }

    @Test
    void visitDefaultToExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((DefaultToExpression) null, ""));
    }

    @Test
    void visitBaseExpression() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((BaseExpression) null, ""));
    }

    @Test
    void visitNumberLiteral() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((NumberLiteral) null, ""));
    }

    @Test
    void visitHashLiteral() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((HashLiteral) null, ""));
    }

    @Test
    void visitStringLiteral() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((StringLiteral) null, ""));
    }

    @Test
    void visitBooleanLiteral() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((BooleanLiteral) null, ""));
    }

    @Test
    void visitNullLiteral() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((NullLiteral) null, ""));
    }

    @Test
    void visitListLiteral() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((ListLiteral) null, ""));
    }

    @Test
    void visitParenthesis() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((Parenthesis) null, ""));
    }

    @Test
    void visitBuiltinVariable() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((BuiltinVariable) null, ""));
    }

    @Test
    void visitDotKey() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((DotKey) null, ""));
    }

    @Test
    void visitDynamicKey() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((DynamicKey) null, ""));
    }

    @Test
    void visitMethodInvoke() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((MethodInvoke) null, ""));
    }

    @Test
    void visitBuiltIn() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((BuiltIn) null, ""));
    }

    @Test
    void visitExists() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((Exists) null, ""));
    }

    @Test
    void visitPositionalArgsList() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((Exists) null, ""));
    }

    @Test
    void visitParameterList() {
        assertThrows(UnsupportedOperationException.class, () -> visitor.visit((ParameterList) null, ""));
    }
}