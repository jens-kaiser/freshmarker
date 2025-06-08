package org.freshmarker.core.fragment;

import java.util.List;

public interface ReductionStrategy {
    void handle(List<Fragment> fragments, int index);

    default boolean needsVariableContext(Fragment fragment) {
        if (fragment instanceof BlockFragment blockFragment)  {
            for (Fragment f : blockFragment.fragments) {
                boolean needsVariableContext = needsVariableContext(f);
                if (needsVariableContext) {
                    return true;
                }
            }
            return false;
        }
        return !(fragment instanceof ConstantFragment);
    }
}
