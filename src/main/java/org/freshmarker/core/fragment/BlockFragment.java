package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockFragment implements Fragment {

    private List<Fragment> fragments;

    public BlockFragment() {
        super();
    }

    public BlockFragment(List<Fragment> fragments) {
        this.fragments = fragments;
    }

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

    @Override
    public Fragment reduce(ReduceContext context) {
        List<Fragment> list = fragments.stream().map(f -> f.reduce(context)).filter(f -> f != ConstantFragment.EMPTY).toList();
        return switch (list.size()) {
            case 0 -> ConstantFragment.EMPTY;
            case 1 -> list.getFirst();
            default -> new BlockFragment(list);
        };
    }

    @Override
    public int getSize() {
        return fragments.stream().mapToInt(Fragment::getSize).sum() + 1;
    }
}
