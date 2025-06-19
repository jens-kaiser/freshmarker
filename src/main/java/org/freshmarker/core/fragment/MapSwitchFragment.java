package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.ReduceException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

public class MapSwitchFragment extends AbstractConditionalFragment implements SwitchFragment {

    private final TemplateObject switchExpression;
    private final Map<TemplatePrimitive<?>, Fragment> fragmentMap;

    public MapSwitchFragment(TemplateObject switchExpression, Node node, Map<TemplatePrimitive<?>, Fragment> fragmentMap, Fragment endFragment) {
        super(endFragment, node);
        this.switchExpression = switchExpression;
        this.fragmentMap = fragmentMap;
    }

    public void process(ProcessContext context) {
        TemplatePrimitive<?> switchValue = evaluatePrimitive(this.switchExpression, context, node);
        Fragment fragment = Objects.requireNonNullElse(fragmentMap.get(switchValue), endFragment);
        fragment.process(context);
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        try {
            TemplatePrimitive<?> switchValue = evaluatePrimitive(switchExpression, context, node);
            return Objects.requireNonNullElse(fragmentMap.get(switchValue), endFragment).reduce(context);
        } catch (WrongTypeException e) {
            throw new ReduceException(e.getMessage(), node, e);
        } catch (ProcessException ignored) {

        }

        try {
            Map<TemplatePrimitive<?>, Fragment> reduced = fragmentMap.entrySet().stream().collect(toMap(Entry::getKey, f -> reduceFragment(context, f.getValue())));
            return new MapSwitchFragment(switchExpression, node, reduced, reduceFragment(context,endFragment));
        } catch (ProcessException e) {
            return this;
        }
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        List<ConditionalFragment> conditionalFragments = fragmentMap.entrySet().stream()
                .map(e -> new ConditionalFragment(e.getKey(), e.getValue(), null)).toList();
        return visitor.visit(this, switchExpression, conditionalFragments, endFragment);
    }

    @Override
    public int getSize() {
        return fragmentMap.values().stream().mapToInt(Fragment::getSize).sum() + endFragment.getSize() + 1;
    }
}
