package org.freshmarker.core.features;

import java.util.BitSet;

import org.freshmarker.api.TemplateFeature;

public class SimpleFeatureSet implements FeatureSet {
    private final BitSet bitSet;
    private final TemplateFeatures templateFeatures;

    public SimpleFeatureSet(BitSet bitSet, TemplateFeatures templateFeatures) {
        this.bitSet = bitSet;
        this.templateFeatures = templateFeatures;
    }

    public SimpleFeatureSet(SimpleFeatureSet featureSet) {
        this((BitSet)featureSet.bitSet.clone(), featureSet.templateFeatures);
    }

    @Override
    public boolean isEnabled(TemplateFeature feature) {
        int flag = templateFeatures.getFlag(feature);
        return flag != -1 && bitSet.get(flag);
    }

    @Override
    public boolean isDisabled(TemplateFeature feature) {
        return !isEnabled(feature);
    }

    public SimpleFeatureSet with(TemplateFeature feature) {
        int flag = templateFeatures.getFlag(feature);
        if (flag == -1 || bitSet.get(flag)) {
            return this;
        }
        BitSet newBitSet = (BitSet)bitSet.clone();
        newBitSet.set(flag);
        return new SimpleFeatureSet(newBitSet, templateFeatures);
    }

    public SimpleFeatureSet without(TemplateFeature feature) {
        int flag = templateFeatures.getFlag(feature);
        if (flag == -1 || !bitSet.get(flag)) {
            return this;
        }
        BitSet newBitSet = (BitSet)bitSet.clone();
        newBitSet.clear(flag);
        return new SimpleFeatureSet(newBitSet, templateFeatures);
    }
}
