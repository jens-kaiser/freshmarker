package org.freshmarker.core.fragment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.ReduceException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.environment.ReducingVariableEnvironment;
import org.freshmarker.core.environment.VariableEnvironment;

import java.util.ArrayList;
import java.util.List;

public class VariableBlockFragment extends BlockFragment{
    public VariableBlockFragment(List<Fragment> fragments) {
        super(fragments);
    }

    @Override
    public void process(ProcessContext context) {
        Environment environment = context.getEnvironment();
        try {
            context.setEnvironment(new VariableEnvironment(environment));
            super.process(context);
        }   finally {
            context.setEnvironment(environment);
        }
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        Environment environment = context.getEnvironment();
        try {
            List<Fragment> reduced = new ArrayList<>();
            context.setEnvironment(new ReducingVariableEnvironment(environment));
            for (Fragment fragment : fragments) {
                reduced.add(fragment.reduce(context));
            }
            return new VariableBlockFragment(Fragments.optimizeReduction(reduced));
        } catch (WrongTypeException e) {
            throw new ReduceException(e.getMessage(), e);
        } catch (ProcessException e) {
            return this;
        } finally {
            context.setEnvironment(environment);
        }
    }
}
