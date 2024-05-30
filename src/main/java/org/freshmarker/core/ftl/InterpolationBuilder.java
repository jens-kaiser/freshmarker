package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token;
import ftl.Token.TokenType;
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
import ftl.ast.MethodInvoke;
import ftl.ast.MultiplicativeExpression;
import ftl.ast.NotExpression;
import ftl.ast.NullLiteral;
import ftl.ast.OrExpression;
import ftl.ast.Parenthesis;
import ftl.ast.PositionalArgsList;
import ftl.ast.PrimaryExpression;
import ftl.ast.RangeExpression;
import ftl.ast.RelationalExpression;
import ftl.ast.UnaryPlusMinusExpression;
import org.freshmarker.core.model.TemplateBooleanExpression;
import org.freshmarker.core.model.TemplateBuiltIn;
import org.freshmarker.core.model.TemplateBuiltInVariable;
import org.freshmarker.core.model.TemplateDefault;
import org.freshmarker.core.model.TemplateDotKey;
import org.freshmarker.core.model.TemplateDynamicKey;
import org.freshmarker.core.model.TemplateEquality;
import org.freshmarker.core.model.TemplateExists;
import org.freshmarker.core.model.TemplateJunction;
import org.freshmarker.core.model.TemplateMethodCall;
import org.freshmarker.core.model.TemplateNegative;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateOperation;
import org.freshmarker.core.model.TemplateRange;
import org.freshmarker.core.model.TemplateRelational;
import org.freshmarker.core.model.TemplateRightLimitedRange;
import org.freshmarker.core.model.TemplateRightUnlimitedRange;
import org.freshmarker.core.model.TemplateSign;
import org.freshmarker.core.model.TemplateSlice;
import org.freshmarker.core.model.TemplateVariable;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InterpolationBuilder implements ExpressionVisitor<Object, TemplateObject> {

    private static final Logger logger = LoggerFactory.getLogger(InterpolationBuilder.class);

    public static final InterpolationBuilder INSTANCE = new InterpolationBuilder();

    private InterpolationBuilder() {
        super();
    }

    @Override
    public TemplateObject visit(Token expression, Object input) {
        String image = expression.toString();
        return switch (expression.getType()) {
            case TRUE -> TemplateBoolean.TRUE;
            case FALSE -> TemplateBoolean.FALSE;
            case INTEGER -> new TemplateNumber(Integer.parseInt(image));
            case DECIMAL -> new TemplateNumber(Double.parseDouble(image));
            case STRING_LITERAL -> new TemplateString(image.substring(1, image.length() - 1));
            case RAW_STRING -> new TemplateString(image.substring(2, image.length() - 1));
            case IDENTIFIER -> new TemplateVariable(expression.toString());
            case EXISTS_OPERATOR -> new TemplateExists((TemplateObject) input);
            default -> throw new IllegalArgumentException(
                    "invalid token type: " + expression.getType() + " source='" + expression.getSource() + "'");
        };
    }

    @Override
    public TemplateObject visit(DefaultToExpression expression, Object input) {
        TemplateObject base = expression.children().get(0).accept(this, input);
        if (expression.getChildCount() == 2) {
            return new TemplateDefault(base, TemplateString.EMPTY);
        }
        return new TemplateDefault(base, expression.children().get(2).accept(this, input));
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
        if (expression.getChildCount() < 3) {
            return new TemplateBuiltIn(buildInName.toString(), (TemplateObject) input, List.of());
        }
        List<TemplateObject> parameter = new ArrayList<>();
        Node child = expression.getChild(3);
        if (child instanceof PositionalArgsList) {
            child.accept(PositionalArgsListBuilder.INSTANCE, parameter);
        } else {
            parameter.add(child.accept(this, null));
        }
        logger.info("parameters: {}", parameter);
        return new TemplateBuiltIn(buildInName.toString(), (TemplateObject) input, parameter);
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
        Token lastToken = (Token) expression.getChild(expression.getChildCount() - 1);
        String dotKey = lastToken.toString();
        logger.info("dotkey: {}", dotKey);
        return new TemplateDotKey((TemplateObject) input, dotKey);
    }

    @Override
    public TemplateObject visit(Exists expression, Object input) {
        return new TemplateExists((TemplateObject) input);
    }

    @Override
    public TemplateObject visit(RangeExpression expression, Object input) {
        TemplateObject left = expression.getChild(0).accept(this, null);
        if (expression.getChildCount() < 3) {
            return new TemplateRightUnlimitedRange(left);
        }
        TemplateObject right = expression.getChild(2).accept(this, null);
        return new TemplateRightLimitedRange(left, right);
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
                result = token.getType() == TokenType.PLUS ? left.get().add(right.get()) : left.get().subtract(right.get());
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
        TemplateObject subExpression = expression.getChild(1).accept(this, null);
        if (subExpression instanceof TemplateBooleanExpression templateBooleanExpression) {
            return templateBooleanExpression.not();
        }
        return new TemplateNegative(subExpression);
    }

    @Override
    public TemplateObject visit(BuiltinVariable expression, Object input) {
        Token lastToken = (Token) expression.getChild(expression.getChildCount() - 1);
        return new TemplateBuiltInVariable(lastToken.toString());
    }

    @Override
    public TemplateObject visit(RelationalExpression expression, Object input) {
        TemplateObject left = expression.getChild(0).accept(this, null);
        TemplateObject right = expression.getChild(2).accept(this, null);
        return new TemplateRelational(((Token) expression.getChild(1)).getType(), left, right);
    }

    @Override
    public TemplateObject visit(AndExpression expression, Object input) {
        TemplateObject left = expression.getChild(0).accept(this, null);
        TemplateObject right = expression.getChild(2).accept(this, null);
        TokenType type = ((Token) expression.getChild(1)).getType();
        switch (type) {
            case AND -> {
                if (right instanceof TemplateBoolean r) {
                    if (left instanceof TemplateBoolean l) {
                        return TemplateBoolean.from(l.getValue() & r.getValue());
                    }
                    if (TemplateBoolean.TRUE.equals(right)) {
                        return left;
                    }
                }
            }
            case AND2 -> {
                if (TemplateBoolean.TRUE.equals(right)) {
                    return left;
                }
                if (TemplateBoolean.TRUE.equals(left)) {
                    return right;
                }
                if ((TemplateBoolean.FALSE.equals(left) || TemplateBoolean.FALSE.equals(right))) {
                    return TemplateBoolean.FALSE;
                }
            }
        }
        return new TemplateJunction(type, left, right);
    }

    @Override
    public TemplateObject visit(OrExpression expression, Object input) {
        TemplateObject left = expression.getChild(0).accept(this, null);
        TemplateObject right = expression.getChild(2).accept(this, null);
        TokenType type = ((Token) expression.getChild(1)).getType();
        switch (type) {
            case OR -> {
                if (right instanceof TemplateBoolean r) {
                    if (left instanceof TemplateBoolean l) {
                        return TemplateBoolean.from(l.getValue() | r.getValue());
                    }
                    return left;
                }
            }
            case OR2 -> {
                if (TemplateBoolean.FALSE.equals(left)) {
                    return right;
                }
                if (TemplateBoolean.FALSE.equals(right)) {
                    return left;
                }
                if ((TemplateBoolean.TRUE.equals(left) || TemplateBoolean.TRUE.equals(right))) {
                    return TemplateBoolean.TRUE;
                }
            }
            case XOR -> {
                if (right instanceof TemplateBoolean r && left instanceof TemplateBoolean l) {
                    return TemplateBoolean.from(l.getValue() ^ r.getValue());
                }
            }
        }
        return new TemplateJunction(type, left, right);
    }

    @Override
    public TemplateObject visit(EqualityExpression expression, Object input) {
        TemplateObject left = expression.getChild(0).accept(this, null);
        TemplateObject right = expression.getChild(2).accept(this, null);
        TokenType equality = (TokenType) expression.getChild(1).getType();
        TemplateEquality result = new TemplateEquality(left, right);
        return equality == TokenType.NOT_EQUALS ? result.not() : result;
    }

    @Override
    public TemplateObject visit(UnaryPlusMinusExpression expression, Object input) {
        logger.debug("visit unary plus minus expression: {}", expression);
        Token token = (Token) expression.getChild(0);
        TemplateObject templateObject = expression.getChild(1).accept(this, null);
        if (token.getType() == TokenType.PLUS) {
            return templateObject;
        }
        return templateObject.asNumber().<TemplateObject>map(TemplateNumber::negate).orElseGet(() -> new TemplateSign(templateObject));
    }

    @Override
    public TemplateObject visit(MethodInvoke expression, Object input) {
        String name = ((TemplateVariable) input).name();
        logger.debug("method invoke: {}", name);
        if (expression.getChild(1).getType() == TokenType.CLOSE_PAREN) {
            return new TemplateMethodCall(name, null);
        }
        List<TemplateObject> parameter = new ArrayList<>();
        Node child = expression.getChild(1);
        logger.debug("child: {}", child.getClass());
        if (child instanceof PositionalArgsList) {
            child.accept(PositionalArgsListBuilder.INSTANCE, parameter);
        } else {
            parameter.add(child.accept(this, null));
        }

        return new TemplateMethodCall(name, parameter);
    }
}
