package org.freshmarker.core.fragment;

import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.TemplateObject;

import java.util.Comparator;
import java.util.List;

public interface TemplateVisitor {
    default void visit(BlockFragment fragment, List<Fragment> fragments) {

    }

    default void visit(ConditionalFragment fragment, TemplateObject conditional, Fragment content) {

    }

    default void visit(ConstantFragment fragment, String value) {

    }

    default void visit(HashListFragment fragment) {

    }

    default void visit(IfFragment fragment, List<ConditionalFragment> fragments, Fragment endFragment) {

    }

    default void visit(InterpolationFragment fragment, TemplateMarkup expression) {

    }

    default void visit(NestedInstructionFragment fragment) {

    }

    default void visit(OutputFormatFragment fragment, String format, Fragment content) {

    }

    default void visit(ReturnInstructionFragment fragment) {

    }

    default void visit(SequenceListFragment fragment, String identifier, TemplateObject list, String looperIdentifier, Fragment block, TemplateObject filter, TemplateObject offset, TemplateObject limit) {

    }

    default void visit(SettingFragment fragment) {

    }

    default void visit(SwitchFragment fragment) {

    }

    default void visit(UserDirectiveFragment fragment) {

    }

    default void visit(VariableFragment fragment) {

    }

    default void visit(HashListFragment hashListFragment, String keyIdentifier, String valueIdentifier, Comparator<String> comparator, TemplateObject list, String looperIdentifier, Fragment block, TemplateObject filter, TemplateObject offset, TemplateObject limit) {

    }
}
