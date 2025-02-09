package org.freshmarker.core.fragment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateVisitorTest {

    @Test
    void visit() {
        TemplateVisitor<Integer> visitor = new TemplateVisitor<>() {
        };

        assertNull(visitor.visit((BlockFragment) null, null));
        assertNull(visitor.visit((ConditionalFragment) null, null, null));
        assertNull(visitor.visit((ConstantFragment) null, null));
        assertNull(visitor.visit((HashListFragment) null));
        assertNull(visitor.visit((IfFragment) null, null, null));
        assertNull(visitor.visit((InterpolationFragment) null, null));
        assertNull(visitor.visit((NestedInstructionFragment) null));
        assertNull(visitor.visit((OutputFormatFragment) null, null, null));
        assertNull(visitor.visit((ReturnInstructionFragment) null));
        assertNull(visitor.visit(null, null, null, null, null, null, null, null));
        assertNull(visitor.visit((SettingFragment) null));
        assertNull(visitor.visit(null, null, null, null));
        assertNull(visitor.visit((UserDirectiveFragment) null));
        assertNull(visitor.visit((VariableFragment) null));
        assertNull(visitor.visit(null, null, null, null, null, null, null, null, null, null));
    }
}