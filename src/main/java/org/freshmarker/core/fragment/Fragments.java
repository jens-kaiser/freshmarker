package org.freshmarker.core.fragment;

import java.util.List;

public final class Fragments {
    public static Fragment optimize(List<Fragment> fragments) {
        return switch (fragments.size()) {
            case 0 -> ConstantFragment.EMPTY;
            case 1 -> fragments.getFirst();
            default -> new BlockFragment(fragments);
        };
    }
}