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

import java.util.Objects;
import java.util.Optional;

public interface ExpressionVisitor<I, O> {

  default O handleWithException(Object expression) {
    throw new UnsupportedOperationException("unsupported: " + Optional.ofNullable(expression).map(Object::getClass).orElse(null));
  }

  default O visit(Node expression, I input) {
    return handleWithException(expression);
  }

  default O visit(Token expression, I input) {
    return handleWithException(expression);
  }

  default O visit(Expression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(OrExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(AndExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(EqualityExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(RelationalExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(RangeExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(AdditiveExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(MultiplicativeExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(UnaryPlusMinusExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(NotExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(PrimaryExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(DefaultToExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(BaseExpression expression, I input) {
    return handleWithException(expression);
  }

  default O visit(NumberLiteral expression, I input) {
    return handleWithException(expression);
  }

  default O visit(HashLiteral expression, I input) {
    return handleWithException(expression);
  }

  default O visit(StringLiteral expression, I input) {
    return handleWithException(expression);
  }

  default O visit(BooleanLiteral expression, I input) {
    return handleWithException(expression);
  }

  default O visit(NullLiteral expression, I input) {
    return handleWithException(expression);
  }

  default O visit(ListLiteral expression, I input) {
    return handleWithException(expression);
  }

  default O visit(Parenthesis expression, I input) {
    return handleWithException(expression);
  }

  default O visit(BuiltinVariable expression, I input) {
    return handleWithException(expression);
  }

  default O visit(DotKey expression, I input) {
    return handleWithException(expression);
  }

  default O visit(DynamicKey expression, I input) {
    return handleWithException(expression);
  }

  default O visit(MethodInvoke expression, I input) {
    return handleWithException(expression);
  }

  default O visit(BuiltIn expression, I input) {
    return handleWithException(expression);
  }

  default O visit(Exists expression, I input) {
    return handleWithException(expression);
  }

  default O visit(PositionalArgsList expression, I input)  {
    return handleWithException(expression);
  }

  default O visit(ParameterList expression, I input) { return handleWithException(expression); }
}
