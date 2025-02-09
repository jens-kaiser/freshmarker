package org.freshmarker.core.model;

import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateObjectVisitorTest {

    @Test
    void visit() {
        TemplateObjectVisitor<Integer> visitor = new TemplateObjectVisitor<>() {
        };

        assertNull(visitor.visit((TemplatePrimitive<?>) null, null));
        assertNull(visitor.visit((TemplateBuiltIn) null));
        assertNull(visitor.visit((TemplateBuiltInVariable) null));
        assertNull(visitor.visit((TemplateDefault) null));
        assertNull(visitor.visit((TemplateMarkup) null, null));
        assertNull(visitor.visit((TemplateVariable) null, null));
        assertNull(visitor.visit((TemplateEquality) null, null, null));
        assertNull(visitor.visit((TemplateNegative) null, null));
        assertNull(visitor.visit((TemplateDotKey) null, null, null));
        assertNull(visitor.visit((TemplateExists) null));
        assertNull(visitor.visit((TemplateDynamicKey) null, null, null));
        assertNull(visitor.visit((TemplateJunction) null));
        assertNull(visitor.visit((TemplateMethodCall) null));
        assertNull(visitor.visit((TemplateOperation) null));
        assertNull(visitor.visit((TemplateRelational) null));
        assertNull(visitor.visit((TemplateSlice) null, null, null));
        assertNull(visitor.visit(null, null, null, false));
        assertNull(visitor.visit((TemplateRightUnlimitedRange) null, null));
        assertNull(visitor.visit(null, null, null, null));
    }
}