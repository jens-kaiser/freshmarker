package org.freshmarker.core.ftl;

import ftl.FTLConstants.TokenType;
import ftl.Node;
import ftl.Token;
import ftl.ast.AdditiveExpression;
import ftl.ast.BaseExpression;
import ftl.ast.BooleanLiteral;
import ftl.ast.BuiltIn;
import ftl.ast.BuiltinVariable;
import ftl.ast.DotKey;
import ftl.ast.DynamicKey;
import ftl.ast.Exists;
import ftl.ast.MultiplicativeExpression;
import ftl.ast.NotExpression;
import ftl.ast.NullLiteral;
import ftl.ast.Parenthesis;
import ftl.ast.PositionalArgsList;
import ftl.ast.PrimaryExpression;
import ftl.ast.RangeExpression;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.freshmarker.core.model.TemplateDotKey;
import org.freshmarker.core.model.TemplateExists;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.TemplateDynamicKey;
import org.freshmarker.core.model.TemplateFunction;
import org.freshmarker.core.model.TemplateNegative;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.primitive.TemplateObject;
import org.freshmarker.core.model.TemplateOperation;
import org.freshmarker.core.model.TemplateRange;
import org.freshmarker.core.model.TemplateSlice;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.TemplateVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterpolationBuilder implements ExpressionVisitor<Object, TemplateObject> {

  private static final Logger logger = LoggerFactory.getLogger(InterpolationBuilder.class);

  @Override
  public TemplateObject visit(Token expression, Object input) {
    String image = expression.getImage();
    switch (expression.getType()) {
      case TRUE:
        return TemplateBoolean.TRUE;
      case FALSE:
        return TemplateBoolean.FALSE;
      case INTEGER:
        return new TemplateNumber(Integer.valueOf(image), Type.INTEGER);
      case DECIMAL:
        return new TemplateNumber(Double.valueOf(image), Type.DOUBLE);
      case STRING_LITERAL:
        return new TemplateString(image.substring(1, image.length() - 1));
      case RAW_STRING:
        return new TemplateString(image.substring(2, image.length() - 1));
      case IDENTIFIER:
        return new TemplateVariable(expression.getImage());
      case EXISTS_OPERATOR:
        return new TemplateExists((TemplateObject) input);
      default:
        throw new IllegalArgumentException("invalid token type: " + expression.getType());
    }
  }

  @Override
  public TemplateObject visit(PrimaryExpression expression, Object input) {
    logger.info("visit primary expression: {}", expression);
    return handlePrimaryAndBase(input, expression.children());
  }

  @Override
  public TemplateObject visit(BaseExpression expression, Object input) {
    logger.info("visit base expression: {}", expression);
    return handlePrimaryAndBase(input, expression.children());
  }

  private TemplateObject handlePrimaryAndBase(Object input, List<Node> children) {
    TemplateObject base = children.get(0).accept(this, input);
    for (int i = 1; i < children.size(); i++) {
      base = children.get(i).accept(this, base);
    }
    return base;
  }

  @Override
  public TemplateObject visit(BooleanLiteral expression, Object input) {
    return expression.getChild(0).accept(this, input);
  }

  @Override
  public TemplateObject visit(NullLiteral expression, Object input) {
    return TemplateNull.NULL;
  }

  @Override
  public TemplateObject visit(BuiltIn expression, Object input) {
    logger.info("visit builtin expression: {}", expression);
    Token buildInName = (Token) expression.getChild(1);
    List<TemplateObject> parameter = new ArrayList<>();
    if (expression.getChildCount() < 3) {
      return new TemplateFunction(buildInName.getImage(), (TemplateObject) input, List.of());
    }
      Node child = expression.getChild(3);
    if (child instanceof PositionalArgsList) {
      child.accept(new ParameterListBuilder(), parameter);
    } else {
      parameter.add(child.accept(this, null));
    }
    logger.info("parameters: {}", parameter);
    return new TemplateFunction(buildInName.getImage(), (TemplateObject) input, parameter);
  }

  @Override
  public TemplateObject visit(DynamicKey expression, Object input) {
    TemplateObject dynamicKey = expression.getChild(1).accept(this, null);
    if (dynamicKey instanceof TemplateRange) {
      return new TemplateSlice((TemplateObject) input, dynamicKey);
    }
    return new TemplateDynamicKey((TemplateObject) input, dynamicKey);
  }

  @Override
  public TemplateObject visit(DotKey expression, Object input) {
    String dotKey = expression.getLastToken().getImage();
    logger.info("dotkey: {}", dotKey);
    return new TemplateDotKey((TemplateObject)input, dotKey);
  }

  @Override
  public TemplateObject visit(Exists expression, Object input) {
    return new TemplateExists((TemplateObject) input);
  }

  @Override
  public TemplateObject visit(RangeExpression expression, Object input) {
    TemplateObject left = expression.getChild(0).accept(this, null);
    if (expression.getChildCount() < 3) {
      return new TemplateRange(false, true, left, TemplateNull.NULL);
    }
    TemplateObject right = expression.getChild(2).accept(this, null);
    return new TemplateRange(false, false, left, right);
  }

  @Override
  public TemplateObject visit(AdditiveExpression expression, Object input) {
    if (expression.getChildCount() == 1) {
      return expression.getChild(0).accept(this, null);
    }
    TemplateObject result = expression.getChild(0).accept(this, null);
    for (int i = 1; i < expression.getChildCount(); i += 2) {
      Token token = (Token) expression.getChild(i);
      TemplateObject second = expression.getChild(i + 1).accept(this, null);
      Optional<TemplateNumber> left = result.asNumber();
      Optional<TemplateNumber> right = second.asNumber();
      if (left.isPresent() && right.isPresent()) {
        result = token.getType() == TokenType.PLUS  ? left.get().add(right.get()) : left.get().subtract(right.get());
      } else {
        result = new TemplateOperation(token.getType(), result, second);
      }
    }
    return result;
  }

  @Override
  public TemplateObject visit(MultiplicativeExpression expression, Object input) {
    if (expression.getChildCount() == 1) {
      return expression.getChild(0).accept(this, null);
    }
    TemplateObject result = expression.getChild(0).accept(this, null);
    logger.info("first: {}", result);
    for (int i = 1; i < expression.getChildCount(); i += 2) {
      Token token = (Token) expression.getChild(i);
      TemplateObject second = expression.getChild(i + 1).accept(this, null);
      Optional<TemplateNumber> left = result.asNumber();
      Optional<TemplateNumber> right = second.asNumber();
      if (left.isPresent() && right.isPresent()) {
        result = token.getType() == TokenType.TIMES ? left.get().multiply(right.get()) : left.get().divide(right.get());
      } else {
        result = new TemplateOperation(token.getType(), result, second);
      }
    }
    return result;
  }

  @Override
  public TemplateObject visit(Parenthesis expression, Object input) {
    return expression.getChild(1).accept(this, null);
  }

  @Override
  public TemplateObject visit(NotExpression expression, Object input) {
    return new TemplateNegative(expression.getChild(1).accept(this, null));
  }

  @Override
  public TemplateObject visit(BuiltinVariable expression, Object input) {
    return new TemplateVariable("." + expression.getLastToken().getImage());
  }
}
