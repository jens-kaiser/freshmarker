package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;

import java.util.List;
import java.util.Objects;

public class BlockFragment implements Fragment {

    protected final List<Fragment> fragments;

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
        return Fragments.optimize(fragments.stream().map(f -> f.reduce(context)).filter(f -> f != ConstantFragment.EMPTY).toList(), false);
    }

    @Override
    public int getSize() {
        return fragments.stream().mapToInt(Fragment::getSize).sum() + 1;
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this, fragments);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BlockFragment that)) {
            return false;
        }
        return Objects.equals(fragments, that.fragments);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fragments);
    }
}
