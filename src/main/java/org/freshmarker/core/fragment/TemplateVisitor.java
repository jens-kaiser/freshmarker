package org.freshmarker.core.fragment;

import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.TemplateObject;

import java.util.Comparator;
import java.util.List;

public interface TemplateVisitor<R> {
    default R visit(BlockFragment fragment, List<Fragment> fragments) {
        return null;
    }

    default R visit(ConditionalFragment fragment, TemplateObject conditional, Fragment content) {
        return null;
    }

    default R visit(ConstantFragment fragment, String value) {
        return null;
    }

    default R visit(HashListFragment fragment) {
        return null;
    }

    default R visit(IfFragment fragment, List<ConditionalFragment> fragments, Fragment endFragment) {
        return null;
    }

    default R visit(InterpolationFragment fragment, TemplateMarkup expression) {
        return null;
    }

    default R visit(NestedInstructionFragment fragment) {
        return null;
    }

    default R visit(OutputFormatFragment fragment, String format, Fragment content) {
        return null;
    }

    default R visit(ReturnInstructionFragment fragment) {
        return null;
    }

    default R visit(SequenceListFragment fragment, String identifier, TemplateObject list, String looperIdentifier, Fragment block, TemplateObject filter, TemplateObject offset, TemplateObject limit) {
        return null;
    }

    default R visit(SettingFragment fragment) {
        return null;
    }

    default R visit(SwitchFragment fragment, TemplateObject switchExpression, List<ConditionalFragment> fragments, Fragment endFragment) {
        return null;
    }

    default R visit(UserDirectiveFragment fragment) {
        return null;
    }

    default R visit(VariableFragment fragment) {
        return null;
    }

    default R visit(HashListFragment hashListFragment, String keyIdentifier, String valueIdentifier, Comparator<String> comparator, TemplateObject list, String looperIdentifier, Fragment block, TemplateObject filter, TemplateObject offset, TemplateObject limit) {
        return null;
    }
}
