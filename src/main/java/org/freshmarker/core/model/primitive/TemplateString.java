package org.freshmarker.core.model.primitive;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateObjectVisitor;
import org.freshmarker.core.model.TemplateOperation.Operator;
import org.freshmarker.core.model.TemplateRelational.Relation;

import java.text.Collator;

import static org.freshmarker.core.SystemFeature.LOCALE_SENSITIVE_STRING_COMPARE;

public class TemplateString extends TemplatePrimitive<String> {

    public static final TemplateString EMPTY = new TemplateString("");

    public TemplateString(String value) {
        super(value);
    }

    public TemplateString concat(TemplateString other, String separator) {
        if (getValue().isEmpty()) {
            return other;
        }
        if (other.getValue().isEmpty()) {
            return this;
        }
        return new TemplateString(getValue() + separator + other.getValue());
    }

    @Override
    public TemplateObject operation(Operator operator, TemplateObject operand, ProcessContext context) {
        return switch (operator) {
            case PLUS -> concat(operand.evaluate(context, TemplateString.class), "");
            case CONCAT -> concat(operand.evaluate(context, TemplateString.class), " ");
            case MULTIPLY -> repeat(operand, context);
            default -> super.operation(operator, operand, context);
        };
    }

    private TemplateString repeat(TemplateObject operand, ProcessContext context) {
        int repeat = operand.evaluate(context, TemplateNumber.class).asInt();
        return new TemplateString(getValue().repeat(repeat));
    }

    private int checkLowerBound(int lowerBound) {
        if (lowerBound < 0) {
            throw new ProcessException("negative slicing values not allowed: " + lowerBound);
        }
        return lowerBound;
    }

    public TemplateString substring(int min, int max) {
        if (min < max) {
            return new TemplateString(getValue().substring(checkLowerBound(min), max + 1));
        }
        return new TemplateString(new StringBuilder(getValue().substring(checkLowerBound(max), min + 1)).reverse().toString());
    }

    @Override
    public boolean equality(TemplateObject operand, ProcessContext context) {
        if (!(operand instanceof TemplateString string)) {
            return false;
        }
        if (context.getFeatureSet().isEnabled(LOCALE_SENSITIVE_STRING_COMPARE)) {
            return compareWithCollator(string, context) == 0;
        }
        return getValue().equals(string.getValue());
    }

    @Override
    public TemplatePrimitive<?> relational(Relation operator, TemplatePrimitive<?> operand, ProcessContext context) {
        TemplateString rightValue = (TemplateString) operand;
        if (context.getFeatureSet().isEnabled(LOCALE_SENSITIVE_STRING_COMPARE)) {
            return compareValues(operator, compareWithCollator(rightValue, context));
        }
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));
    }

    private int compareWithCollator(TemplateString rightValue, ProcessContext context) {
        Collator collator = Collator.getInstance(context.getLocale());
        context.getFeatureSet().getConfigured(LOCALE_SENSITIVE_STRING_COMPARE).map(Integer.class::cast).ifPresent(collator::setStrength);
        return collator.compare(getValue(), rightValue.getValue());
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, "'" + this + "'");
    }
}
