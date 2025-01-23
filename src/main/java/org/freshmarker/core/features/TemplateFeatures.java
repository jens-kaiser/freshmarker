package org.freshmarker.core.features;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class TemplateFeatures implements FeatureSet {

    record Entry(int flag, boolean enabled) {

    }

    Map<TemplateFeature, Entry> masks = new HashMap<>();

    public void addFeatures(TemplateFeature... features) {
        for (TemplateFeature feature : features) {
            addFeature(feature, feature.isEnabledByDefault());
        }
    }

    public void addFeature(TemplateFeature feature, boolean enabled) {
        if (masks.containsKey(feature)) {
            return;
        }
        masks.put(feature, new Entry(masks.size(), enabled));
    }

    public SimpleFeatureSet create() {
        BitSet bitSet = new BitSet(masks.size());
        masks.values().stream().filter(Entry::enabled).forEach(v -> bitSet.set(v.flag()));
        return new SimpleFeatureSet(bitSet, this);
    }

    int getFlag(TemplateFeature feature) {
        Entry entry = masks.get(feature);
        return entry == null ? -1 : entry.flag();
    }

    @Override
    public boolean isEnabled(TemplateFeature feature) {
        return masks.getOrDefault(feature, new Entry(0, false)).enabled();
    }

    @Override
    public boolean isDisabled(TemplateFeature feature) {
        return !masks.getOrDefault(feature, new Entry(0, false)).enabled();
    }
}
