package org.freshmarker.core.model;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

public interface TemplateObjectVisitor {
    default void visit(TemplatePrimitive<?> primitive, String string) {

    }

    default void visit(TemplateBean bean) {

    }

    default void visit(TemplateBuiltIn builtIn) {

    }

    default void visit(TemplateBuiltInVariable builtInVariable) {

    }

    default void visit(TemplateDefault fallback) {

    }

    default void visit(TemplateMarkup markup, TemplateObject content) {

    }

    default void visit(TemplateVariable variable, String name) {

    }

    default void visit(TemplateEquality templateEquality, TemplateObject left, TemplateObject right) {

    }

    default void visit(TemplateNegative templateNegative, TemplateObject expression) {

    }

    default void visit(TemplateDotKey templateDotKey, TemplateObject map, String dotKey) {

    }

    default void visit(TemplateExists templateExists) {

    }
}
