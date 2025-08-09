package org.freshmarker.core.model.primitive;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateObjectVisitor;

import java.util.Objects;

public class TemplatePrimitive<P> implements TemplateObject {

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

    public boolean equality(TemplateObject operand, ProcessContext context) {
        return equals(operand);
    }

    public TemplatePrimitive<?> relational(TokenType operator, TemplatePrimitive<?> operand, ProcessContext context) {
        return TemplateBoolean.from(relation(operator, operand, context));
    }

    protected TemplatePrimitive<?> compareValues(TokenType operator, int compare) {
        if (TokenType.COMPARE == operator) {
            return TemplateNumber.of(Integer.signum(compare));
        }
        return TemplateBoolean.from(switch (operator) {
            case LT -> compare < 0;
            case GT -> compare > 0;
            case LTE -> compare <= 0;
            case GTE, UNICODE_GTE -> compare >= 0;
            default -> throw new ProcessException("unsupported operation: " + operator);
        });
    }
}
