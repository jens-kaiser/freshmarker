package org.freshmarker.core.model;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

public interface TemplateObjectVisitor<R> {
    default R visit(TemplatePrimitive<?> primitive, String string) {
        return null;
    }

    default R visit(TemplateBuiltIn builtIn) {
        return null;
    }

    default R visit(TemplateBuiltInVariable builtInVariable) {
        return null;
    }

    default R visit(TemplateDefault fallback) {
        return null;
    }

    default R visit(TemplateMarkup markup, TemplateObject content) {
        return null;
    }

    default R visit(TemplateVariable variable, String name) {
        return null;
    }

    default R visit(TemplateEquality templateEquality, TemplateObject left, TemplateObject right) {
        return null;
    }

    default R visit(TemplateNot templateNot, TemplateObject expression) {
        return null;
    }

    default R visit(TemplateDotKey templateDotKey, TemplateObject map, String dotKey) {
        return null;
    }

    default R visit(TemplateExists templateExists) {
        return null;
    }

    default R visit(TemplateDynamicKey templateDynamicKey, TemplateObject sequence, TemplateObject dynamicKey) {
        return null;
    }

    default R visit(TemplateJunction templateJunction) {
        return null;
    }

    default R visit(TemplateMethodCall templateMethodCall) {
        return null;
    }

    default R visit(TemplateOperation templateOperation) {
        return null;
    }

    default R visit(TemplateRelational templateRelational) {
        return null;
    }

    default R visit(TemplateSlice templateSlice, TemplateObject sequence, TemplateObject range) {
        return null;
    }

    default R visit(TemplateRightLimitedRange templateRightLimitedRange, TemplateObject lower, TemplateObject upper, boolean exclusive) {
        return null;
    }

    default R visit(TemplateRightUnlimitedRange templateRightUnlimitedRange, TemplateObject lower) {
        return null;
    }

    default R visit(TemplateLengthLimitedRange templateLengthLimitedRange, TemplateObject lower, TemplateObject upper, TemplateObject count) {
        return null;
    }
}
