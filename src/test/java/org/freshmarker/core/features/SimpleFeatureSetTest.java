package org.freshmarker.core.features;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleFeatureSetTest {
    private enum DemoFeature implements TemplateFeature {
        FIRST,
        SECOND
    }

    private SimpleFeatureSet simpleFeatureSet;

    @BeforeEach
    void setUp() {
        TemplateFeatures templateFeatures = new TemplateFeatures();
        templateFeatures.addSwitches(DemoFeature.FIRST, true);
        templateFeatures.addSwitches(DemoFeature.SECOND, false);
        simpleFeatureSet = templateFeatures.create();
    }

    @Test
    void isEnabled() {
        assertTrue(simpleFeatureSet.isEnabled(DemoFeature.FIRST));
        assertFalse(simpleFeatureSet.isEnabled(DemoFeature.SECOND));
    }

    @Test
    void isDisabled() {
        assertFalse(simpleFeatureSet.isDisabled(DemoFeature.FIRST));
        assertTrue(simpleFeatureSet.isDisabled(DemoFeature.SECOND));
    }

    @Test
    void with() {
        SimpleFeatureSet derived = simpleFeatureSet.with(DemoFeature.SECOND);
        assertTrue(simpleFeatureSet.isDisabled(DemoFeature.SECOND));
        assertTrue(derived.isEnabled(DemoFeature.SECOND));
    }

    @Test
    void notChangingWith() {
        SimpleFeatureSet derived = simpleFeatureSet.with(DemoFeature.FIRST);
        assertSame(simpleFeatureSet, derived);
    }

    @Test
    void without() {
        SimpleFeatureSet derived = simpleFeatureSet.without(DemoFeature.FIRST);
        assertTrue(simpleFeatureSet.isEnabled(DemoFeature.FIRST));
        assertTrue(derived.isDisabled(DemoFeature.FIRST));
    }

    @Test
    void notChangingWithout() {
        SimpleFeatureSet derived = simpleFeatureSet.without(DemoFeature.SECOND);
        assertSame(simpleFeatureSet, derived);
    }
}