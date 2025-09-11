package org.freshmarker.core.model;

import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateObjectVisitorTest {

    @Test
    void visit() {
        TemplateObjectVisitor<Integer> visitor = new TemplateObjectVisitor<>() {
        };

        assertNull(TemplateString.EMPTY.accept(visitor));
        assertNull(new TemplateBuiltIn(null, null, null, false, false).accept(visitor));
        assertNull(new TemplateBuiltInVariable(null).accept(visitor));
        assertNull(new TemplateDefault(null, null).accept(visitor));
        assertNull(new TemplateMarkup(null).accept(visitor));
        assertNull(new DefaultTemplateVariable(null).accept(visitor));
        assertNull(new ModelVariable(null).accept(visitor));
        assertNull(new TemplateEquality(null, null).accept(visitor));
        assertNull(new TemplateNot(null).accept(visitor));
        assertNull(new TemplateDotKey(null, null).accept(visitor));
        assertNull(new TemplateExists(null).accept(visitor));
        assertNull(new TemplateDynamicKey(null, null).accept(visitor));
        assertNull(new TemplateJunction(null, null, null).accept(visitor));
        assertNull(new TemplateMethodCall(null, null).accept(visitor));
        assertNull(new TemplateOperation(null, null, null).accept(visitor));
        assertNull(new TemplateRelational(null, null, null).accept(visitor));
        assertNull(new TemplateSlice(null, null).accept(visitor));
        assertNull(new TemplateRightLimitedRange(null, null, false).accept(visitor));
        assertNull(new TemplateRightUnlimitedRange(null).accept(visitor));
        assertNull(new TemplateLengthLimitedRange(null, null).accept(visitor));
    }
}