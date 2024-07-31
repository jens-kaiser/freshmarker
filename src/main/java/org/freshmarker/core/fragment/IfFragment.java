package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IfFragment implements Fragment {
    private static final Logger log = LoggerFactory.getLogger(IfFragment.class);

    private final List<ConditionalFragment> fragments = new ArrayList<>();
    private Fragment elseFragment = ConstantFragment.EMPTY;

    public void addFragment(ConditionalFragment fragment) {
        fragments.add(fragment);
    }

    public void addElseFragment(Fragment fragment) {
        elseFragment = fragment;
    }

    @Override
    public void process(ProcessContext context) {
        fragments.stream().filter(f -> filterByConditional(context, f))
                .map(f -> (Fragment) f).findFirst().or(() -> Optional.ofNullable(elseFragment)).ifPresent(f -> f.process(context));
    }

    private boolean filterByConditional(ProcessContext context, ConditionalFragment conditionalFragment) {
        try {
            return conditionalFragment.getConditional().evaluate(context, TemplateBoolean.class) == TemplateBoolean.TRUE;
        } catch (UnsupportedBuiltInException e) {
            throw new UnsupportedBuiltInException(e.getMessage(), conditionalFragment.getNode(), e);
        } catch (WrongTypeException e) {
            throw new WrongTypeException(e.getMessage(), conditionalFragment.getNode(), e);
        }
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        try {
            for (ConditionalFragment fragment : fragments) {
                if (filterByConditional(context, fragment)) {
                    return fragment.reduce(context);
                }
            }
            return elseFragment.reduce(context);
        } catch (RuntimeException e) {
            log.info("cannot reduce: {}", e.getMessage(), e);
        }
        IfFragment ifFragment = new IfFragment();
        ifFragment.fragments.addAll(fragments.stream().map(f -> (ConditionalFragment) f.reduce(context)).toList());
        ifFragment.elseFragment = elseFragment.reduce(context);
        return ifFragment;
    }

    @Override
    public int getSize() {
        int extra = elseFragment == ConstantFragment.EMPTY ? 0 : 1;
        return fragments.stream().mapToInt(Fragment::getSize).sum() + extra + 1;
    }
}
