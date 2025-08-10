package org.freshmarker.core.fragment;

import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateVisitorTest {

    @Test
    void visit() {
        TemplateVisitor<Integer> visitor = new TemplateVisitor<>() {
        };

        assertNull(new BlockFragment(null).accept(visitor));
        assertNull(new ConditionalFragment(TemplateString.EMPTY, new ConstantFragment(null), null).accept(visitor));
        assertNull(new ConstantFragment(null).accept(visitor));
        assertNull(new HashListFragment(null, null, null, null, null, null, null, null, null, null).accept(visitor));
        assertNull(new IfFragment(null, null, null).accept(visitor));
        assertNull(new InterpolationFragment(null, null).accept(visitor));
        assertNull(new NestedInstructionFragment().accept(visitor));
        assertNull(new OutputFormatFragment(null, null, null).accept(visitor));
        assertNull(new ReturnInstructionFragment().accept(visitor));
        assertNull(new SequenceListFragment(null, null, null, null, null, null, null, null).accept(visitor));
        assertNull(new SettingFragment(null, null, null).accept(visitor));
        assertNull(new ListSwitchFragment(null, null, List.of(), null).accept(visitor));
        assertNull(new MapSwitchFragment(null, null, Map.of(), null).accept(visitor));
        assertNull(new UserDirectiveFragment(null, null, null, null).accept(visitor));
        assertNull(new VarVariableFragment(null, null, null).accept(visitor));
        assertNull(new SetVariableFragment(null, null, null).accept(visitor));
        assertNull(new HashListFragment(null, null, null, null, null, null, null, null, null, null).accept(visitor));
        assertNull(new TryFragment(null, null).accept(visitor));
    }
}