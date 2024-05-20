package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockFragment implements Fragment {

    private List<Fragment> fragments;

    public void addFragment(Fragment fragment) {
        fragments = Objects.requireNonNullElseGet(fragments, ArrayList::new);
        fragments.add(fragment);
    }

    @Override
    public void process(ProcessContext context) {
        if (fragments != null) {
            fragments.forEach(f -> f.process(context));
        }
    }
}
