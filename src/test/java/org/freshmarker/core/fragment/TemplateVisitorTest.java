package org.freshmarker.core.fragment;

import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.TemplateObject;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TemplateVisitorTest {

    @Test
    void visit() {
        TemplateVisitor<Integer> visitor = new TemplateVisitor<Integer>() {
            @Override
            public Integer visit(BlockFragment fragment, List<Fragment> fragments) {
                return 1;
            }

            @Override
            public Integer visit(ConditionalFragment fragment, TemplateObject conditional, Fragment content) {
                return 2;
            }

            @Override
            public Integer visit(ConstantFragment fragment, String value) {
                return 3;
            }

            @Override
            public Integer visit(HashListFragment fragment) {
                return 4;
            }

            @Override
            public Integer visit(IfFragment fragment, List<ConditionalFragment> fragments, Fragment endFragment) {
                return 5;
            }

            @Override
            public Integer visit(InterpolationFragment fragment, TemplateMarkup expression) {
                return 6;
            }

            @Override
            public Integer visit(NestedInstructionFragment fragment) {
                return 7;
            }

            @Override
            public Integer visit(OutputFormatFragment fragment, String format, Fragment content) {
                return 8;
            }

            @Override
            public Integer visit(ReturnInstructionFragment fragment) {
                return 9;
            }

            @Override
            public Integer visit(SequenceListFragment fragment, String identifier, TemplateObject list, String looperIdentifier, Fragment block, TemplateObject filter, TemplateObject offset, TemplateObject limit) {
                return 10;
            }

            @Override
            public Integer visit(SettingFragment fragment) {
                return 11;
            }

            @Override
            public Integer visit(SwitchFragment fragment, TemplateObject switchExpression, List<ConditionalFragment> fragments, Fragment endFragment) {
                return 12;
            }

            @Override
            public Integer visit(UserDirectiveFragment fragment) {
                return 13;
            }

            @Override
            public Integer visit(VariableFragment fragment) {
                return 14;
            }

            @Override
            public Integer visit(HashListFragment hashListFragment, String keyIdentifier, String valueIdentifier, Comparator<String> comparator, TemplateObject list, String looperIdentifier, Fragment block, TemplateObject filter, TemplateObject offset, TemplateObject limit) {
                return 15;
            }
        };

        assertEquals(1, visitor.visit((BlockFragment)null, null));
        assertEquals(2, visitor.visit((ConditionalFragment)null, null, null));
        assertEquals(3, visitor.visit((ConstantFragment)null, null));
        assertEquals(4, visitor.visit((HashListFragment)null));
        assertEquals(5, visitor.visit((IfFragment)null, null, null));
        assertEquals(6, visitor.visit((InterpolationFragment)null, null));
        assertEquals(7, visitor.visit((NestedInstructionFragment)null));
        assertEquals(8, visitor.visit((OutputFormatFragment)null, null, null));
        assertEquals(9, visitor.visit((ReturnInstructionFragment)null));
        assertEquals(10, visitor.visit(null, null, null, null, null, null, null, null));
        assertEquals(11, visitor.visit((SettingFragment)null));
        assertEquals(12, visitor.visit(null, null, null, null));
        assertEquals(13, visitor.visit((UserDirectiveFragment)null));
        assertEquals(14, visitor.visit((VariableFragment)null));
        assertEquals(15, visitor.visit(null, null, null, null, null, null, null, null, null, null));
    }
}