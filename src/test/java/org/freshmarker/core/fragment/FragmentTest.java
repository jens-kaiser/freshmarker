package org.freshmarker.core.fragment;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FragmentTest {
    @Test
    void equalConstantFragments() {
        ConstantFragment constantFragment1 = new ConstantFragment("test");
        ConstantFragment constantFragment2 = new ConstantFragment("test");
        ConstantFragment constantFragment3 = new ConstantFragment("tset");
        assertEquals(constantFragment1, constantFragment2);
        assertNotEquals(constantFragment1, constantFragment3);
        assertEquals(constantFragment1.hashCode(), constantFragment2.hashCode());
    }

    @Test
    void equalBlockFragments() {
        BlockFragment blockFragment1 = new BlockFragment(List.of(new ConstantFragment("test")));
        BlockFragment blockFragment2 = new BlockFragment(List.of(new ConstantFragment("test")));
        BlockFragment blockFragment3 = new BlockFragment(List.of(new ConstantFragment("tset")));
        assertEquals(blockFragment1, blockFragment2);
        assertNotEquals(blockFragment1, blockFragment3);
        assertEquals(blockFragment1.hashCode(), blockFragment2.hashCode());
    }
}