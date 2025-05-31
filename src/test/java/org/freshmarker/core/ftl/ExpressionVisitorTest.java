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
import ftl.ast.PositionalArgsList;
import ftl.ast.PrimaryExpression;
import ftl.ast.RangeExpression;
import ftl.ast.RelationalExpression;
import ftl.ast.StringLiteral;
import ftl.ast.UnaryPlusMinusExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionVisitorTest {
    private ExpressionVisitor<String, String> visitor;

    @BeforeEach
    void setUp() {
        visitor = new ExpressionVisitor<>() {

            @Override
            public String handleWithException(Object expression) {
                return "";
            }
        };
    }

    @Test
    void visitNode() {
        assertEquals("", visitor.visit((Node) null, ""));
    }

    @Test
    void visitToken() {
        assertEquals("", visitor.visit((Token) null, ""));
    }

    @Test
    void visitExpression() {
        assertEquals("", visitor.visit((Expression) null, ""));
    }

    @Test
    void visitOrExpression() {
        assertEquals("", visitor.visit((OrExpression) null, ""));
    }

    @Test
    void visitAndExpression() {
        assertEquals("", visitor.visit((AndExpression) null, ""));
    }

    @Test
    void visitEqualityExpression() {
        assertEquals("", visitor.visit((EqualityExpression) null, ""));
    }

    @Test
    void visitRelationalExpression() {
        assertEquals("", visitor.visit((RelationalExpression) null, ""));
    }

    @Test
    void visitRangeExpression() {
        assertEquals("", visitor.visit((RangeExpression) null, ""));
    }

    @Test
    void visitAdditiveExpression() {
        assertEquals("", visitor.visit((AdditiveExpression) null, ""));
    }

    @Test
    void visitMultiplicativeExpression() {
        assertEquals("", visitor.visit((MultiplicativeExpression) null, ""));
    }

    @Test
    void visitUnaryPlusMinusExpression() {
        assertEquals("", visitor.visit((UnaryPlusMinusExpression) null, ""));
    }

    @Test
    void visitNotExpression() {
        assertEquals("", visitor.visit((NotExpression) null, ""));
    }

    @Test
    void visitPrimaryExpression() {
        assertEquals("", visitor.visit((PrimaryExpression) null, ""));
    }

    @Test
    void visitDefaultToExpression() {
        assertEquals("", visitor.visit((DefaultToExpression) null, ""));
    }

    @Test
    void visitBaseExpression() {
        assertEquals("", visitor.visit((BaseExpression) null, ""));
    }

    @Test
    void visitNumberLiteral() {
        assertEquals("", visitor.visit((NumberLiteral) null, ""));
    }

    @Test
    void visitHashLiteral() {
        assertEquals("", visitor.visit((HashLiteral) null, ""));
    }

    @Test
    void visitStringLiteral() {
        assertEquals("", visitor.visit((StringLiteral) null, ""));
    }

    @Test
    void visitBooleanLiteral() {
        assertEquals("", visitor.visit((BooleanLiteral) null, ""));
    }

    @Test
    void visitNullLiteral() {
        assertEquals("", visitor.visit((NullLiteral) null, ""));
    }

    @Test
    void visitListLiteral() {
        assertEquals("", visitor.visit((ListLiteral) null, ""));
    }

    @Test
    void visitParenthesis() {
        assertEquals("", visitor.visit((Parenthesis) null, ""));
    }

    @Test
    void visitBuiltinVariable() {
        assertEquals("", visitor.visit((BuiltinVariable) null, ""));
    }

    @Test
    void visitDotKey() {
        assertEquals("", visitor.visit((DotKey) null, ""));
    }

    @Test
    void visitDynamicKey() {
        assertEquals("", visitor.visit((DynamicKey) null, ""));
    }

    @Test
    void visitMethodInvoke() {
        assertEquals("", visitor.visit((MethodInvoke) null, ""));
    }

    @Test
    void visitBuiltIn() {
        assertEquals("", visitor.visit((BuiltIn) null, ""));
    }

    @Test
    void visitExists() {
        assertEquals("", visitor.visit((Exists) null, ""));
    }

    @Test
    void visitPositionalArgsList() {
        assertEquals("", visitor.visit((PositionalArgsList) null, ""));
    }

    @Test
    void visitParameterList() {
        assertEquals("", visitor.visit((ParameterList) null, ""));
    }
}