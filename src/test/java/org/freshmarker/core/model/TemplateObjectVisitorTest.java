package org.freshmarker.core.model;

import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateObjectVisitorTest {

    @Test
    void visit() {
        TemplateObjectVisitor<Integer> visitor = new TemplateObjectVisitor<>() {
            @Override
            public Integer visit(TemplatePrimitive<?> primitive, String string) {
                return 1;
            }

            @Override
            public Integer visit(TemplateBuiltIn builtIn) {
                return 2;
            }

            @Override
            public Integer visit(TemplateBuiltInVariable builtInVariable) {
                return 3;
            }

            @Override
            public Integer visit(TemplateDefault fallback) {
                return 4;
            }

            @Override
            public Integer visit(TemplateMarkup markup, TemplateObject content) {
                return 5;
            }

            @Override
            public Integer visit(TemplateVariable variable, String name) {
                return 6;
            }

            @Override
            public Integer visit(TemplateEquality templateEquality, TemplateObject left, TemplateObject right) {
                return 7;
            }

            @Override
            public Integer visit(TemplateNegative templateNegative, TemplateObject expression) {
                return 8;
            }

            @Override
            public Integer visit(TemplateDotKey templateDotKey, TemplateObject map, String dotKey) {
                return 9;
            }

            @Override
            public Integer visit(TemplateExists templateExists) {
                return 10;
            }

            @Override
            public Integer visit(TemplateDynamicKey templateDynamicKey, TemplateObject sequence, TemplateObject dynamicKey) {
                return 11;
            }

            @Override
            public Integer visit(TemplateJunction templateJunction) {
                return 12;
            }

            @Override
            public Integer visit(TemplateMethodCall templateMethodCall) {
                return 13;
            }

            @Override
            public Integer visit(TemplateOperation templateOperation) {
                return 14;
            }

            @Override
            public Integer visit(TemplateRelational templateRelational) {
                return 15;
            }

            @Override
            public Integer visit(TemplateSlice templateSlice, TemplateObject sequence, TemplateObject range) {
                return 16;
            }

            @Override
            public Integer visit(TemplateRightLimitedRange templateRightLimitedRange, TemplateObject lower, TemplateObject upper, boolean exclusive) {
                return 17;
            }

            @Override
            public Integer visit(TemplateRightUnlimitedRange templateRightUnlimitedRange, TemplateObject lower) {
                return 18;
            }

            @Override
            public Integer visit(TemplateLengthLimitedRange templateLengthLimitedRange, TemplateObject lower, TemplateObject upper, TemplateObject count) {
                return 19;
            }
        };

        assertEquals(1, visitor.visit((TemplatePrimitive<?>)null, null));
        assertEquals(2, visitor.visit((TemplateBuiltIn)null));
        assertEquals(3, visitor.visit((TemplateBuiltInVariable)null));
        assertEquals(4, visitor.visit((TemplateDefault)null));
        assertEquals(5, visitor.visit((TemplateMarkup)null, null));
        assertEquals(6, visitor.visit((TemplateVariable)null, null));
        assertEquals(7, visitor.visit((TemplateEquality)null, null, null));
        assertEquals(8, visitor.visit((TemplateNegative)null, null));
        assertEquals(9, visitor.visit((TemplateDotKey)null, null, null));
        assertEquals(10, visitor.visit((TemplateExists)null));
        assertEquals(11, visitor.visit((TemplateDynamicKey)null, null, null));
        assertEquals(12, visitor.visit((TemplateJunction)null));
        assertEquals(13, visitor.visit((TemplateMethodCall)null));
        assertEquals(14, visitor.visit((TemplateOperation)null));
        assertEquals(15, visitor.visit((TemplateRelational)null));
        assertEquals(16, visitor.visit((TemplateSlice)null, null, null));
        assertEquals(17, visitor.visit(null, null, null, false));
        assertEquals(18, visitor.visit((TemplateRightUnlimitedRange)null, null));
        assertEquals(19, visitor.visit((TemplateLengthLimitedRange)null, null, null, null));
    }
}