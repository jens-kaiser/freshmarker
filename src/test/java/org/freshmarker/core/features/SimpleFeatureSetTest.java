package org.freshmarker.core.features;

import org.freshmarker.api.TemplateFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleFeatureSetTest {
    private enum DemoFeature implements org.freshmarker.api.TemplateFeature {
        FIRST,
        SECOND;

        @Override
        public boolean isEnabledByDefault() {
            return this == FIRST;
        }
    }

    private enum SecondDemoFeature implements org.freshmarker.api.TemplateFeature {
        FIRST
    }

    private enum UnknownFeature implements TemplateFeature {
        UNKNOWN_FEATURE
    }

    private SimpleFeatureSet simpleFeatureSet;

    @BeforeEach
    void setUp() {
        TemplateFeatures templateFeatures = new TemplateFeatures();
        templateFeatures.addFeatures(DemoFeature.values());
        templateFeatures.addFeatures(SecondDemoFeature.values());
        simpleFeatureSet = templateFeatures.create();
    }

    @Test
    void isEnabled() {
        assertTrue(simpleFeatureSet.isEnabled(DemoFeature.FIRST));
        assertFalse(simpleFeatureSet.isEnabled(DemoFeature.SECOND));
        assertTrue(simpleFeatureSet.isEnabled(SecondDemoFeature.FIRST));
    }

    @Test
    void isDisabled() {
        assertFalse(simpleFeatureSet.isDisabled(DemoFeature.FIRST));
        assertTrue(simpleFeatureSet.isDisabled(DemoFeature.SECOND));
        assertFalse(simpleFeatureSet.isDisabled(SecondDemoFeature.FIRST));
    }

    @Test
    void with() {
        SimpleFeatureSet derived = simpleFeatureSet.with(DemoFeature.SECOND);
        assertTrue(simpleFeatureSet.isDisabled(DemoFeature.SECOND));
        assertTrue(derived.isEnabled(DemoFeature.SECOND));
    }

    @Test
    void withWithUnknownFeature() {
        SimpleFeatureSet derived = simpleFeatureSet.with(UnknownFeature.UNKNOWN_FEATURE);
        assertFalse(derived.isEnabled(UnknownFeature.UNKNOWN_FEATURE));
        assertTrue(derived.isDisabled(UnknownFeature.UNKNOWN_FEATURE));
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
    void withoutWithUnknownFeature() {
        SimpleFeatureSet derived = simpleFeatureSet.without(UnknownFeature.UNKNOWN_FEATURE);
        assertFalse(derived.isEnabled(UnknownFeature.UNKNOWN_FEATURE));
        assertTrue(derived.isDisabled(UnknownFeature.UNKNOWN_FEATURE));
    }

    @Test
    void notChangingWithout() {
        SimpleFeatureSet derived = simpleFeatureSet.without(DemoFeature.SECOND);
        assertSame(simpleFeatureSet, derived);
    }
}