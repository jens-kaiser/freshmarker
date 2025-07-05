package org.freshmarker.core.features;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.freshmarker.api.TemplateFeature;

public class SimpleFeatureSet implements org.freshmarker.api.FeatureSet {
    private final BitSet bitSet;
    private final TemplateFeatures templateFeatures;
    private final Map<TemplateFeature, Object> parameters;

    public SimpleFeatureSet(BitSet bitSet, TemplateFeatures templateFeatures, Map<TemplateFeature, Object> parameters) {
        this.bitSet = bitSet;
        this.templateFeatures = templateFeatures;
        this.parameters = parameters;
    }

    public SimpleFeatureSet(SimpleFeatureSet featureSet) {
        this((BitSet)featureSet.bitSet.clone(), featureSet.templateFeatures, new HashMap<>(featureSet.parameters));
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

    @Override
    public Optional<Object> getConfigured(TemplateFeature feature) {
        if (isEnabled(feature)) {
            return Optional.ofNullable(parameters.get(feature));
        }
        return Optional.empty();
    }

    public SimpleFeatureSet with(TemplateFeature feature, Object value) {
        int flag = templateFeatures.getFlag(feature);
        if (flag == -1 || bitSet.get(flag)) {
            parameters.put(feature, value);
            return this;
        }
        BitSet newBitSet = (BitSet)bitSet.clone();
        newBitSet.set(flag);
        HashMap<TemplateFeature, Object> newParameters = new HashMap<>(parameters);
        newParameters.put(feature, value);
        return new SimpleFeatureSet(newBitSet, templateFeatures, newParameters);
    }

    public SimpleFeatureSet with(TemplateFeature feature) {
        int flag = templateFeatures.getFlag(feature);
        if (flag == -1 || bitSet.get(flag)) {
            return this;
        }
        BitSet newBitSet = (BitSet)bitSet.clone();
        newBitSet.set(flag);
        return new SimpleFeatureSet(newBitSet, templateFeatures, new HashMap<>(parameters));
    }

    public SimpleFeatureSet without(TemplateFeature feature) {
        int flag = templateFeatures.getFlag(feature);
        if (flag == -1 || !bitSet.get(flag)) {
            return this;
        }
        BitSet newBitSet = (BitSet)bitSet.clone();
        newBitSet.clear(flag);
        return new SimpleFeatureSet(newBitSet, templateFeatures, new HashMap<>(parameters));
    }
}
