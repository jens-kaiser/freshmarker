package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SwitchFragment implements Fragment {
    private static final Logger log = LoggerFactory.getLogger(SwitchFragment.class);

    private final List<ConditionalFragment> fragments = new ArrayList<>();
    private Fragment defaultFragment;

    private final TemplateObject switchExpression;
    private final Node node;

    public SwitchFragment(TemplateObject switchExpression, Node node) {
        this.switchExpression = switchExpression;
        this.node = node;
    }

    public void addFragment(ConditionalFragment fragment) {
        fragments.add(fragment);
    }

    public void addDefaultFragment(Fragment fragment) {
        defaultFragment = fragment;
    }

    public void process(ProcessContext context) {
        TemplatePrimitive<?> switchValue = evaluatePrimitive(this.switchExpression, context, node);
        for (ConditionalFragment fragment : fragments) {
            if (switchValue.equals(evaluatePrimitive(fragment.getConditional(), context, fragment.getNode()))) {
                fragment.process(context);
                return;
            }
        }
        defaultFragment.process(context);
    }

    private TemplatePrimitive<?> evaluatePrimitive(TemplateObject conditional, ProcessContext context, Node node) {
        try {
            return conditional.evaluateToObject(context).asPrimitive().orElseThrow(() -> new WrongTypeException("not a primitive type", node));
        } catch (UnsupportedBuiltInException e) {
            throw new UnsupportedBuiltInException(e.getMessage(), node, e);
        } catch (WrongTypeException e) {
            throw new WrongTypeException(e.getMessage(), node, e);
        } catch (ProcessException e) {
            throw new ProcessException(e.getMessage(), node, e);
        }
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        try {
            TemplatePrimitive<?> switchValue = evaluatePrimitive(this.switchExpression, context, node);
            for (ConditionalFragment fragment : fragments) {
                if (switchValue.equals(evaluatePrimitive(fragment.getConditional(), context, fragment.getNode()))) {
                    return fragment.reduce(context);
                }
            }
            return defaultFragment.reduce(context);
        } catch (RuntimeException e) {
            log.info("cannot reduce: {}", e.getMessage(), e);
        }
        SwitchFragment switchFragment = new SwitchFragment(switchExpression, node);
        switchFragment.fragments.addAll(fragments.stream().map(f -> f.reduce(context)).toList());
        switchFragment.defaultFragment = defaultFragment.reduce(context);
        return switchFragment;
    }

    @Override
    public int getSize() {
        int extra = defaultFragment == ConstantFragment.EMPTY ? 0 : 1;
        return fragments.stream().mapToInt(Fragment::getSize).sum() + extra + 1;
    }
}
