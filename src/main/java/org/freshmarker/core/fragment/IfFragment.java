package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.primitive.TemplateBoolean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IfFragment implements Fragment {

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
}
