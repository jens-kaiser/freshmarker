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
import ftl.ast.HashLiteral;
import ftl.ast.ListLiteral;
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
import org.freshmarker.core.model.operation.TemplateGtRelational;
import org.freshmarker.core.model.operation.TemplateGteRelational;
import org.freshmarker.core.model.operation.TemplateLtRelational;
import org.freshmarker.core.model.operation.TemplateLteRelational;
import org.freshmarker.core.model.operation.TemplateAddOperation;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateBooleanExpression;
import org.freshmarker.core.model.TemplateBuiltIn;
import org.freshmarker.core.model.TemplateBuiltInVariable;
import org.freshmarker.core.model.TemplateDefault;
import org.freshmarker.core.model.operation.TemplateDivOperation;
import org.freshmarker.core.model.TemplateDotKey;
import org.freshmarker.core.model.TemplateDynamicKey;
import org.freshmarker.core.model.TemplateEquality;
import org.freshmarker.core.model.TemplateExists;
import org.freshmarker.core.model.TemplateJunction;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateMethodCall;
import org.freshmarker.core.model.operation.TemplateModOperation;
import org.freshmarker.core.model.operation.TemplateMulOperation;
import org.freshmarker.core.model.TemplateNegative;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateRange;
import org.freshmarker.core.model.TemplateRightLimitedRange;
import org.freshmarker.core.model.TemplateRightUnlimitedRange;
import org.freshmarker.core.model.TemplateSign;
import org.freshmarker.core.model.TemplateSlice;
import org.freshmarker.core.model.operation.TemplateSubOperation;
import org.freshmarker.core.model.TemplateVariable;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InterpolationBuilder implements ExpressionVisitor<Object, TemplateObject> {

    private static final Logger logger = LoggerFactory.getLogger(InterpolationBuilder.class);

    public static final InterpolationBuilder INSTANCE = new InterpolationBuilder();

    private static final PositionalArgsListBuilder BUILDER = new PositionalArgsListBuilder();

    private InterpolationBuilder() {
        super();
    }

    @Override
    public TemplateObject visit(Token expression, Object input) {
        String image = expression.toString();
        return switch (expression.getType()) {
            case TRUE -> TemplateBoolean.TRUE;
            case FALSE -> TemplateBoolean.FALSE;
            case INTEGER -> TemplateNumber.of(Integer.parseInt(image));
            case DECIMAL -> new TemplateNumber(Double.parseDouble(image));
            case STRING_LITERAL -> new TemplateString(image.substring(1, image.length() - 1));
            case IDENTIFIER -> new TemplateVariable(expression.toString());
            case EXISTS_OPERATOR -> new TemplateExists((TemplateObject) input);
            case NULL -> TemplateNull.NULL_LITERAL;
            default -> throw new IllegalArgumentException(
                    "invalid token type: " + expression.getType() + " source='" + expression.getSource() + "'");
        };
    }

    @Override
    public TemplateDefault visit(DefaultToExpression expression, Object input) {
        TemplateObject base = expression.children().get(0).accept(this, input);
        if (expression.size() == 2) {
            return new TemplateDefault(base, TemplateString.EMPTY);
        }
        return new TemplateDefault(base, expression.children().get(2).accept(this, input));
    }

    @Override
    public TemplateObject visit(PrimaryExpression expression, Object input) {
        return handlePrimaryAndBase(input, expression.children());
    }

    @Override
    public TemplateObject visit(BaseExpression expression, Object input) {
        return handlePrimaryAndBase(input, expression.children());
    }

    private TemplateObject handlePrimaryAndBase(Object input, List<Node> children) {
        TemplateObject base = children.getFirst().accept(this, input);
        for (int i = 1; i < children.size(); i++) {
            base = children.get(i).accept(this, base);
        }
        return base;
    }

    @Override
    public TemplateObject visit(BooleanLiteral expression, Object input) {
        return expression.getFirst().accept(this, input);
    }

    @Override
    public TemplateNull visit(NullLiteral expression, Object input) {
        return TemplateNull.NULL_LITERAL;
    }

    @Override
    public TemplateBuiltIn visit(BuiltIn expression, Object input) {
        Token buildInName = (Token) expression.get(1);
        if (expression.size() < 3) {
            return new TemplateBuiltIn(buildInName.toString(), (TemplateObject) input, List.of());
        }
        List<TemplateObject> parameter = new ArrayList<>();
        Node child = expression.get(3);
        if (child instanceof PositionalArgsList) {
            child.accept(BUILDER, parameter);
        } else {
            parameter.add(child.accept(this, null));
        }
        return new TemplateBuiltIn(buildInName.toString(), (TemplateObject) input, parameter);
    }

    @Override
    public TemplateObject visit(DynamicKey expression, Object input) {
        TemplateObject dynamicKey = expression.get(1).accept(this, null);
        if (dynamicKey instanceof TemplateRange) {
            return new TemplateSlice((TemplateObject) input, dynamicKey);
        }
        return new TemplateDynamicKey((TemplateObject) input, dynamicKey);
    }

    @Override
    public TemplateDotKey visit(DotKey expression, Object input) {
        Token lastToken = (Token) expression.getLast();
        return new TemplateDotKey((TemplateObject) input, lastToken.toString());
    }

    @Override
    public TemplateExists visit(Exists expression, Object input) {
        return new TemplateExists((TemplateObject) input);
    }

    @Override
    public TemplateRange visit(RangeExpression expression, Object input) {
        TemplateObject left = expression.getFirst().accept(this, null);
        if (expression.size() < 3) {
            return new TemplateRightUnlimitedRange(left);
        }
        TemplateObject right = expression.get(2).accept(this, null);
        return new TemplateRightLimitedRange(left, right);
    }

    @Override
    public TemplateObject visit(AdditiveExpression expression, Object input) {
        TemplateObject result = expression.getFirst().accept(this, null);
        for (int i = 1; i < expression.size(); i += 2) {
            TokenType tokenType = (TokenType) expression.get(i).getType();
            TemplateObject second = expression.get(i + 1).accept(this, null);
            TemplateObject operation = switch (tokenType) {
                case PLUS -> new TemplateAddOperation(result, second);
                case MINUS -> new TemplateSubOperation(result, second);
                default -> throw new ParsingException("unsupported operation: " + tokenType, expression);
            };
            result = handlePrimitives(result, second, operation);
        }
        return result;
    }

    @Override
    public TemplateObject visit(MultiplicativeExpression expression, Object input) {
        TemplateObject result = expression.getFirst().accept(this, null);
        for (int i = 1; i < expression.size(); i += 2) {
            TokenType tokenType = (TokenType) expression.get(i).getType();
            TemplateObject second = expression.get(i + 1).accept(this, null);
            TemplateObject operation = switch (tokenType) {
                case TIMES -> new TemplateMulOperation(result, second);
                case DIVIDE -> new TemplateDivOperation(result, second);
                case PERCENT -> new TemplateModOperation(result, second);
                default -> throw new ParsingException("invalid operation: " + tokenType, expression);
            };
            result = handlePrimitives(result, second, operation);
        }
        return result;
    }

    private static TemplateObject handlePrimitives(TemplateObject result, TemplateObject second, TemplateObject operation) {
        if (result.isPrimitive() && second.isPrimitive()) {
            return operation.evaluateToObject(null);
        }
        return operation;
    }


    @Override
    public TemplateObject visit(Parenthesis expression, Object input) {
        return expression.get(1).accept(this, null);
    }

    @Override
    public TemplateObject visit(NotExpression expression, Object input) {
        TemplateObject subExpression = expression.get(1).accept(this, null);
        if (subExpression instanceof TemplateBooleanExpression templateBooleanExpression) {
            return templateBooleanExpression.not();
        }
        return new TemplateNegative(subExpression);
    }

    @Override
    public TemplateBuiltInVariable visit(BuiltinVariable expression, Object input) {
        Token lastToken = (Token) expression.getLast();
        return new TemplateBuiltInVariable(lastToken.toString());
    }

    @Override
    public TemplateObject visit(RelationalExpression expression, Object input) {
        TemplateObject left = expression.getFirst().accept(this, null);
        TemplateObject right = expression.get(2).accept(this, null);
        TokenType type = ((Token) expression.get(1)).getType();
        TemplateObject relational = switch (type) {
            case GT -> new TemplateGtRelational(left, right);
            case GTE -> new TemplateGteRelational(left, right);
            case LTE -> new TemplateLteRelational(left, right);
            case LT -> new TemplateLtRelational(left, right);
            default -> throw new ParsingException("invalid relation: " + type, expression);
        };
        if (left instanceof TemplateNumber && right instanceof TemplateNumber) {
            return relational.evaluateToObject(null);
        }
        return relational;
    }

    @Override
    public TemplateObject visit(AndExpression expression, Object input) {
        TokenType type = ((Token) expression.get(1)).getType();
        return switch (type) {
            case AND -> handleAnd(expression);
            case AND2 -> handleAnd2(expression);
            default -> throw new ParsingException("invalid conjunction: " + type, expression);
        };
    }

    private TemplateObject handleAnd(AndExpression expression) {
        TemplateObject left = expression.getFirst().accept(this, null);
        TemplateObject right = expression.get(2).accept(this, null);
        if (!(right instanceof TemplateBoolean r)) {
            return new TemplateJunction(TokenType.AND, left, right);
        }
        if (left instanceof TemplateBoolean l) {
            return TemplateBoolean.from(l.getValue() && r.getValue());
        }
        if (TemplateBoolean.TRUE.equals(right)) {
            return left;
        }
        return new TemplateJunction(TokenType.AND, left, right);
    }

    private TemplateObject handleAnd2(AndExpression expression) {
        TemplateObject left = expression.getFirst().accept(this, null);
        TemplateObject right = expression.get(2).accept(this, null);
        if (TemplateBoolean.TRUE.equals(right)) {
            return left;
        }
        if (TemplateBoolean.TRUE.equals(left)) {
            return right;
        }
        if ((TemplateBoolean.FALSE.equals(left) || TemplateBoolean.FALSE.equals(right))) {
            return TemplateBoolean.FALSE;
        }
        return new TemplateJunction(TokenType.AND2, left, right);
    }

    @Override
    public TemplateObject visit(OrExpression expression, Object input) {
        TokenType type = ((Token) expression.get(1)).getType();
        return switch (type) {
            case OR -> handleOr(expression);
            case OR2 -> handleOr2(expression);
            case XOR -> handleXor(expression);
            default -> throw new ParsingException("invalid disjunction: " + type, expression);
        };
    }

    private TemplateBooleanExpression handleXor(OrExpression expression) {
        TemplateObject left = expression.getFirst().accept(this, null);
        TemplateObject right = expression.get(2).accept(this, null);
        if (right instanceof TemplateBoolean r && left instanceof TemplateBoolean l) {
            return TemplateBoolean.from(l.getValue() ^ r.getValue());
        }
        return new TemplateJunction(TokenType.XOR, left, right);
    }

    private TemplateObject handleOr2(OrExpression expression) {
        TemplateObject left = expression.getFirst().accept(this, null);
        TemplateObject right = expression.get(2).accept(this, null);
        if (TemplateBoolean.FALSE.equals(left)) {
            return right;
        }
        if (TemplateBoolean.FALSE.equals(right)) {
            return left;
        }
        if ((TemplateBoolean.TRUE.equals(left) || TemplateBoolean.TRUE.equals(right))) {
            return TemplateBoolean.TRUE;
        }
        return new TemplateJunction(TokenType.OR2, left, right);
    }

    private TemplateObject handleOr(OrExpression expression) {
        TemplateObject left = expression.getFirst().accept(this, null);
        TemplateObject right = expression.get(2).accept(this, null);
        if (right instanceof TemplateBoolean r) {
            if (left instanceof TemplateBoolean l) {
                return TemplateBoolean.from(l.getValue() || r.getValue());
            }
            return left;
        }
        return new TemplateJunction(TokenType.OR, left, right);
    }

    @Override
    public TemplateObject visit(EqualityExpression expression, Object input) {
        TemplateObject left = expression.getFirst().accept(this, null);
        TemplateObject right = expression.get(2).accept(this, null);
        TemplateEquality equality = new TemplateEquality(left, right);
        TemplateObject result = expression.get(1).getType() == TokenType.NOT_EQUALS ? equality.not() : equality;
        return left.isPrimitive() && right.isPrimitive() ? result.evaluateToObject(null) : result;
    }

    @Override
    public TemplateObject visit(UnaryPlusMinusExpression expression, Object input) {
        Token token = (Token) expression.getFirst();
        TemplateObject templateObject = expression.get(1).accept(this, null);
        if (token.getType() == TokenType.PLUS) {
            return templateObject;
        }
        if (templateObject instanceof TemplateNumber number) {
            return number.negate();
        }
        return new TemplateSign(templateObject);
    }

    @Override
    public TemplateMethodCall visit(MethodInvoke expression, Object input) {
        String name = ((TemplateVariable) input).name();
        logger.debug("method invoke: {}", name);
        if (expression.get(1).getType() == TokenType.CLOSE_PAREN) {
            return new TemplateMethodCall(name, null);
        }
        List<TemplateObject> parameter = new ArrayList<>();
        Node child = expression.get(1);
        logger.debug("child: {}", child.getClass());
        if (child instanceof PositionalArgsList) {
            child.accept(BUILDER, parameter);
        } else {
            parameter.add(child.accept(this, null));
        }
        return new TemplateMethodCall(name, parameter);
    }

    @Override
    public TemplateBean visit(HashLiteral expression, Object input) {
        LinkedHashMap<String, Object> hash = new LinkedHashMap<>();
        for (int i = 0; i < expression.size() - 1; i += 4) {
            TemplateObject key = expression.get(i + 1).accept(this, null);
            if (!(key instanceof TemplateString string)) {
                throw new IllegalArgumentException("key is not a string");
            }
            TemplateObject value = expression.get(i + 3).accept(this, null);
            if (!value.isPrimitive()) {
                throw new IllegalArgumentException("value is not a primitive");
            }
            hash.put(string.getValue(), value);
        }
        return new TemplateBean(hash, null);
    }

    @Override
    public TemplateListSequence visit(ListLiteral expression, Object input) {
        List<Object> list = new ArrayList<>();
        for (int i = 1; i < expression.size() - 1; i++) {
            Node node = expression.get(i);
            if (node.getType() != TokenType.COMMA) {
                TemplateObject templateObject = node.accept(this, null);
                if (!templateObject.isPrimitive()) {
                    throw new IllegalArgumentException("value is not a primitive");
                }
                list.add(templateObject);
            }
        }
        return new TemplateListSequence(list);
    }
}
