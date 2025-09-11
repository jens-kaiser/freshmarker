package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token;
import ftl.Token.TokenType;
import ftl.ast.AdditiveExpression;
import ftl.ast.AndExpression;
import ftl.ast.BaseExpression;
import ftl.ast.BaseNode;
import ftl.ast.BooleanLiteral;
import ftl.ast.BuiltIn;
import ftl.ast.BuiltinVariable;
import ftl.ast.ConditionalExpression;
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
import org.freshmarker.api.FeatureSet;
import org.freshmarker.core.BuiltinHandlingFeature;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.ftl.TemplateDictionary.VariableType;
import org.freshmarker.core.model.DefaultTemplateVariable;
import org.freshmarker.core.model.KeyValueLoopVariable;
import org.freshmarker.core.model.LooperVariable;
import org.freshmarker.core.model.ModelVariable;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateBooleanExpression;
import org.freshmarker.core.model.TemplateBuiltIn;
import org.freshmarker.core.model.TemplateBuiltInVariable;
import org.freshmarker.core.model.TemplateDefault;
import org.freshmarker.core.model.TemplateDotKey;
import org.freshmarker.core.model.TemplateDynamicKey;
import org.freshmarker.core.model.TemplateEquality;
import org.freshmarker.core.model.TemplateExists;
import org.freshmarker.core.model.TemplateJunction;
import org.freshmarker.core.model.TemplateLengthLimitedRange;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateMethodCall;
import org.freshmarker.core.model.TemplateNot;
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
import org.freshmarker.core.model.builtin.HookedBuiltIn;
import org.freshmarker.core.model.builtin.LogBuiltIn;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

public class InterpolationBuilder implements ExpressionVisitor<Object, TemplateObject> {

    private static final Logger logger = LoggerFactory.getLogger(InterpolationBuilder.class);

    private final PositionalArgsListBuilder argsListBuilder;

    private final FeatureSet featureSet;
    private final StaticContext templateContext;
    private final TemplateDictionary dictionary;

    public InterpolationBuilder(FeatureSet featureSet, StaticContext templateContext, TemplateDictionary dictionary) {
        this.featureSet = featureSet;
        this.templateContext = templateContext;
        this.dictionary = dictionary;
        argsListBuilder = new PositionalArgsListBuilder(this);
    }

    private record TemplateObjectAndNode(TemplateObject templateObject, String node) { }

    @Override
    public TemplateObject visit(Token expression, Object input) {
        String image = expression.toString();
        return switch (expression.getType()) {
            case TRUE -> TemplateBoolean.TRUE;
            case FALSE -> TemplateBoolean.FALSE;
            case INTEGER -> getIntegerTemplateNumber(input == TokenType.MINUS ? "-" + image : image);
            case LONG -> getLongTemplateNumber(input == TokenType.MINUS ? "-" + image : image);
            case DECIMAL -> new TemplateNumber(Double.parseDouble(image));
            case STRING_LITERAL -> new TemplateString(image.substring(1, image.length() - 1));
            case IDENTIFIER -> {
                VariableType type = dictionary.getVariable(image);
                yield switch (type) {
                    case MODEL -> new ModelVariable(image);
                    case LOOPER -> new LooperVariable(image);
                    case KEY, VALUE -> new KeyValueLoopVariable(image);
                    default -> new DefaultTemplateVariable(image);
                };
            }
            case EXISTS_OPERATOR -> new TemplateExists(((TemplateObjectAndNode) input).templateObject());
            case NULL -> TemplateNull.NULL_LITERAL;
            default -> throw new IllegalArgumentException(
                    "invalid token type: " + expression.getType() + " source='" + expression.getSource() + "'");
        };
    }

    private static TemplateNumber getLongTemplateNumber(String image) {
        return new TemplateNumber(Long.parseLong(image.substring(0, image.length() - 1)));
    }

    private static TemplateNumber getIntegerTemplateNumber(String image) {
        long number = Long.parseLong(image);
        if ( number < Integer.MIN_VALUE || number > Integer.MAX_VALUE)  {
            return new TemplateNumber(number);
        }
        return TemplateNumber.of((int)number);
    }

    @Override
    public TemplateDefault visit(DefaultToExpression expression, Object input) {
        TemplateObject base = expression.children().get(0).accept(this, null);
        if (expression.size() == 2) {
            return new TemplateDefault(base, TemplateString.EMPTY);
        }
        return new TemplateDefault(base, expression.children().get(2).accept(this, null));
    }

    @Override
    public TemplateDefault visit(ConditionalExpression expression, Object input) {
        TemplateObject base = expression.children().get(0).accept(this, null);
        return new TemplateDefault(base, expression.children().get(2).accept(this, null));
    }

    @Override
    public TemplateObject visit(PrimaryExpression expression, Object input) {
        return handlePrimaryAndBase(expression, expression.children());
    }

    @Override
    public TemplateObject visit(BaseExpression expression, Object input) {
        return handlePrimaryAndBase(expression, expression.children());
    }

    private TemplateObject handlePrimaryAndBase(Node node, List<Node> children) {
        TemplateObject base = children.getFirst().accept(this, null);
        StringBuilder builder = new StringBuilder(node.getLocation() + " '" + children.getFirst().toString());
        for (int i = 1; i < children.size(); i++) {
            base = children.get(i).accept(this, new TemplateObjectAndNode(base, builder + "'"));
            builder.append(children.get(i));
        }
        return base;
    }

    @Override
    public TemplateObject visit(BooleanLiteral expression, Object input) {
        return expression.getFirst().accept(this, null);
    }

    @Override
    public TemplateNull visit(NullLiteral expression, Object input) {
        return TemplateNull.NULL_LITERAL;
    }

    @Override
    public TemplateObject visit(BuiltIn expression, Object input) {
        String buildInName = ((Token) expression.get(1)).toString();
        TemplateObjectAndNode templateObjectAndNode = (TemplateObjectAndNode) input;
        if ("log".equals(buildInName)) {
            return new LogBuiltIn(templateObjectAndNode.templateObject(), templateObjectAndNode.node());
        }
        boolean pipe = expression.getFirst().getType() == TokenType.BUILT_IN2;
        boolean ignoreOptionalEmpty = pipe || featureSet.isEnabled(BuiltinHandlingFeature.IGNORE_OPTIONAL_EMPTY);
        boolean ignoreNull = pipe || featureSet.isEnabled(BuiltinHandlingFeature.IGNORE_NULL);
        List<TemplateObject> parameter = collectParameters(expression);
        return createBuiltIn(buildInName, templateObjectAndNode, parameter, ignoreOptionalEmpty, ignoreNull);
    }

    private List<TemplateObject> collectParameters(BuiltIn expression) {
        if (expression.size() < 3) {
            return List.of();
        }
        List<TemplateObject> parameter = new ArrayList<>();
        Node child = expression.get(3);
        if (child instanceof PositionalArgsList) {
            child.accept(argsListBuilder, parameter);
        } else {
            parameter.add(child.accept(this, null));
        }
        return parameter;
    }

    private TemplateObject createBuiltIn(String buildInName, TemplateObjectAndNode templateObjectAndNode, List<TemplateObject> parameter, boolean ignoreOptionalEmpty, boolean ignoreNull) {
        Optional<Entry<BuiltInKey, org.freshmarker.api.BuiltIn>> builtIn = templateContext.builtIns().byName(buildInName);
        if (builtIn.isPresent()) {
            return new HookedBuiltIn(templateObjectAndNode.templateObject(), builtIn.get().getKey(), builtIn.get().getValue(), parameter, ignoreOptionalEmpty, ignoreNull);
        }
        return new TemplateBuiltIn(buildInName, templateObjectAndNode.templateObject(), parameter, ignoreOptionalEmpty, ignoreNull);
    }

    @Override
    public TemplateObject visit(DynamicKey expression, Object input) {
        TemplateObject dynamicKey = expression.get(1).accept(this, null);
        if (dynamicKey instanceof TemplateRange) {
            return new TemplateSlice(((TemplateObjectAndNode)input).templateObject(), dynamicKey);
        }
        return new TemplateDynamicKey(((TemplateObjectAndNode)input).templateObject(), dynamicKey);
    }

    @Override
    public TemplateDotKey visit(DotKey expression, Object input) {
        Token lastToken = (Token) expression.getLast();
        return new TemplateDotKey(((TemplateObjectAndNode)input).templateObject(), lastToken.toString());
    }

    @Override
    public TemplateExists visit(Exists expression, Object input) {
        return new TemplateExists(((InterpolationBuilder.TemplateObjectAndNode)input).templateObject());
    }

    @Override
    public TemplateRange visit(RangeExpression expression, Object input) {
        TemplateObject left = expression.getFirst().accept(this, null);
        Token range = (Token)expression.get(1);
        if (expression.size() < 3) {
            if (range.getType() != TokenType.DOT_DOT) {
                throw new ParsingException("right unlimited does not support: " + range, expression);
            }
            return new TemplateRightUnlimitedRange(left);
        }
        return switch (range.getType()) {
            case DOT_DOT  -> new TemplateRightLimitedRange(left, expression.get(2).accept(this, null), false);
            case DOT_DOT_EXCLUSIVE -> new TemplateRightLimitedRange(left, expression.get(2).accept(this, null), true);
            case DOT_DOT_LENGTH -> new TemplateLengthLimitedRange(left, expression.get(2).accept(this, null));
            default -> throw new ParsingException("right limited does not support: " + range, expression);
        };
    }

    @Override
    public TemplateObject visit(AdditiveExpression expression, Object input) {
        return handleMultiplicativeAndAdditiveExpression(expression);
    }

    @Override
    public TemplateObject visit(MultiplicativeExpression expression, Object input) {
        return handleMultiplicativeAndAdditiveExpression(expression);
    }

    private TemplateObject handleMultiplicativeAndAdditiveExpression(BaseNode expression) {
        TemplateObject result = expression.getFirst().accept(this, null);
        for (int i = 1; i < expression.size(); i += 2) {
            Token token = (Token) expression.get(i);
            TemplateObject second = expression.get(i + 1).accept(this, null);
            TemplateOperation operation = new TemplateOperation(token.getType(), result, second);
            if (result.isPrimitive() && second.isPrimitive()) {
                try {
                    result = operation.evaluateToObject(null);
                } catch (ProcessException e) {
                    throw new ParsingException(e.getMessage(), expression);
                }
            } else {
                result = operation;
            }
        }
        return result;
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
        return new TemplateNot(subExpression);
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
        TemplateRelational relational = new TemplateRelational(type, left, right);
        try {
            if (left.isPrimitive() && right.isPrimitive()) {
                return relational.evaluateToObject(null);
            }
            return relational;
        } catch (ProcessException e) {
            throw new ParsingException(e.getMessage(), expression);
        } catch (RuntimeException e) {
            return relational;
        }
    }

    @Override
    public TemplateObject visit(AndExpression expression, Object input) {
        return switch (((Token) expression.get(1)).getType()) {
            case AND -> handleAnd(TokenType.AND, expression);
            case AND2 -> handleAnd(TokenType.AND2, expression);
            default -> throw new IllegalArgumentException("invalid conjunction");
        };
    }

    private TemplateObject handleAnd(TokenType type, AndExpression expression) {
        TemplateObject left = expression.getFirst().accept(this, null);
        TemplateObject right = expression.get(2).accept(this, null);
        if (TemplateBoolean.TRUE.equals(right)) {
            return left;
        }
        if (TemplateBoolean.TRUE.equals(left)) {
            return right;
        }
        if (TemplateBoolean.FALSE.equals(left) && TemplateBoolean.FALSE.equals(right) || type == TokenType.AND2 && TemplateBoolean.FALSE.equals(left)) {
            return TemplateBoolean.FALSE;
        }
        return new TemplateJunction(type, left, right);
    }

    @Override
    public TemplateObject visit(OrExpression expression, Object input) {
        return switch (((Token) expression.get(1)).getType()) {
            case OR -> handleOr(TokenType.OR, expression);
            case OR2 -> handleOr(TokenType.OR2, expression);
            case XOR -> handleXor(expression);
            default -> throw new IllegalArgumentException("invalid disjunction");
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

    private TemplateObject handleOr(TokenType type, OrExpression expression) {
        TemplateObject left = expression.getFirst().accept(this, null);
        TemplateObject right = expression.get(2).accept(this, null);
        if (TemplateBoolean.FALSE.equals(right)) {
            return left;
        }
        if (TemplateBoolean.FALSE.equals(left)) {
            return right;
        }
        if (TemplateBoolean.TRUE.equals(left) && TemplateBoolean.TRUE.equals(right) || type == TokenType.OR2 && TemplateBoolean.TRUE.equals(left)) {
            return TemplateBoolean.TRUE;
        }
        return new TemplateJunction(type, left, right);
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
        TemplateObject templateObject = expression.get(1).accept(this, token.getType());
        if (token.getType() == TokenType.PLUS || templateObject.isPrimitive()) {
            return templateObject;
        }
        if (templateObject instanceof TemplateNumber number) {
            return number.negate();
        }
        return new TemplateSign(templateObject);
    }

    @Override
    public TemplateMethodCall visit(MethodInvoke expression, Object input) {
        String name = ((TemplateVariable) ((TemplateObjectAndNode)input).templateObject()).name();
        logger.debug("method invoke: {}", name);
        if (expression.get(1).getType() == TokenType.CLOSE_PAREN) {
            return new TemplateMethodCall(name, null);
        }
        List<TemplateObject> parameter = new ArrayList<>();
        Node child = expression.get(1);
        logger.debug("child: {}", child.getClass());
        if (child instanceof PositionalArgsList) {
            child.accept(argsListBuilder, parameter);
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
            hash.put(string.getValue(), expression.get(i + 3).accept(this, null));
        }
        return new TemplateBean(hash, null);
    }

    @Override
    public TemplateListSequence visit(ListLiteral expression, Object input) {
        List<Object> list = new ArrayList<>();
        for (int i = 1; i < expression.size() - 1; i++) {
            Node node = expression.get(i);
            if (node.getType() != TokenType.COMMA) {
                list.add(node.accept(this, null));
            }
        }
        return new TemplateListSequence(list);
    }
}
