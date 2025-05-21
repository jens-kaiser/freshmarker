package org.freshmarker.core.fragment;

import java.util.List;

public final class Fragments {
    private Fragments() {
        super();
    }

    public static Fragment optimize(List<Fragment> fragments, boolean enabledVariableContext) {
        if (enabledVariableContext) {
            return optimizeWithVariableContext(fragments);
        }
        return switch (fragments.size()) {
            case 0 -> ConstantFragment.EMPTY;
            case 1 -> fragments.getFirst();
            default -> new BlockFragment(fragments);
        };
    }

    public static Fragment optimizeWithVariableContext(List<Fragment> fragments) {
        boolean containsVar = isContainsVar(fragments);
        return switch (fragments.size()) {
            case 0 -> ConstantFragment.EMPTY;
            case 1 -> containsVar ? ConstantFragment.EMPTY : fragments.getFirst();
            default -> containsVar ? new VariableBlockFragment(fragments) : new BlockFragment(fragments);
        };
    }

    public static List<Fragment> withVariableContext(List<Fragment> fragments) {
        if (!isContainsVar(fragments)) {
            return fragments;
        }
        return fragments.size() < 2 ? List.of(ConstantFragment.EMPTY) : List.of(new VariableBlockFragment(fragments));
    }

    private static boolean isContainsVar(List<Fragment> fragments) {
        return fragments.stream().anyMatch(VarVariableFragment.class::isInstance);
    }

}