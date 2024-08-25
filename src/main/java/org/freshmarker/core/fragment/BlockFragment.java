package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;

import java.util.List;

public class BlockFragment implements Fragment {

    private final List<Fragment> fragments;

    public BlockFragment(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public void process(ProcessContext context) {
        for (Fragment fragment : fragments) {
            fragment.process(context);
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
