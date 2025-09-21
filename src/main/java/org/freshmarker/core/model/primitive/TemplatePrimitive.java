package org.freshmarker.core.model.primitive;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateObjectVisitor;
import org.freshmarker.core.model.TemplateRelational.Relation;

import java.util.Map;
import java.util.Objects;

public class TemplatePrimitive<P> implements TemplateObject {
    private static final Map<TokenType, Relation> RELATIONS = Map.of(
            TokenType.LT, Relation.LT,
            TokenType.GT, Relation.GT,
            TokenType.LTE, Relation.LTE,
            TokenType.GTE, Relation.GTE,
            TokenType.UNICODE_GTE, Relation.GTE,
            TokenType.COMPARE, Relation.COMPARE
    );

    private final P value;

    public TemplatePrimitive(P value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    public P getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TemplatePrimitive<?> templatePrimitive) {
            return value.equals(templatePrimitive.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        return this;
    }

    @Override
    public Class<?> getModelType() {
        return value.getClass();
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, this.toString());
    }

    @Override
    public boolean equality(TemplateObject operand, ProcessContext context) {
        return equals(operand);
    }

    @Deprecated
    public TemplatePrimitive<?> relational(TokenType operator, TemplatePrimitive<?> operand, ProcessContext context) {
        return TemplateBoolean.from(relation(RELATIONS.get(operator), operand, context));
    }

    public TemplatePrimitive<?> relational(Relation operator, TemplatePrimitive<?> operand, ProcessContext context) {
        return TemplateBoolean.from(relation(operator, operand, context));
    }

    @Deprecated
    protected TemplatePrimitive<?> compareValues(TokenType operator, int compare) {
        return compareValues(RELATIONS.get(operator), compare);
    }

    protected TemplatePrimitive<?> compareValues(Relation operator, int compare) {
        if (Relation.COMPARE == operator) {
            return TemplateNumber.of(Integer.signum(compare));
        }
        return TemplateBoolean.from(switch (operator) {
            case LT -> compare < 0;
            case GT -> compare > 0;
            case LTE -> compare <= 0;
            case GTE -> compare >= 0;
            default -> throw new ProcessException("unsupported operation: " + operator);
        });
    }
}
