package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Node.NodeType;
import ftl.Token.TokenType;
import ftl.ast.BaseNode;
import ftl.ast.CaseInstruction;
import ftl.ast.DefaultInstruction;
import ftl.ast.SwitchInstruction;
import org.freshmarker.api.FeatureSet;
import org.freshmarker.core.SwitchDirectiveFeature;
import org.freshmarker.core.fragment.ConditionalFragment;
import org.freshmarker.core.fragment.ConstantFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.Fragments;
import org.freshmarker.core.fragment.ListSwitchFragment;
import org.freshmarker.core.fragment.MapSwitchFragment;
import org.freshmarker.core.fragment.SwitchFragment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_CONSTANT_CASES;
import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_CONSTANT_ONS;
import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_EQUAL_TYPE_CASES;
import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_EQUAL_TYPE_ONS;

class SwitchFragmentBuilder implements FtlVisitor<SwitchFragment, SwitchFragment> {

    private static final Logger logger = LoggerFactory.getLogger(SwitchFragmentBuilder.class);

    private final FragmentBuilder fragmentBuilder;
    private final InterpolationBuilder interpolationBuilder;
    private final FeatureSet featureSet;
    private final SwitchCollector collector;

    private static class SwitchCollector {
        List<ConditionalFragment> fragments = new ArrayList<>();
        Fragment fragment = ConstantFragment.EMPTY;
        boolean isAllPrimitive;

        public void addFragment(ConditionalFragment fragment) {
            fragments.add(fragment);
            isAllPrimitive |= fragment.conditional().isPrimitive();
        }

        public void addDefaultFragment(Fragment fragment) {
            this.fragment = fragment;
        }
    }

    public SwitchFragmentBuilder(FragmentBuilder fragmentBuilder, InterpolationBuilder interpolationBuilder, FeatureSet featureSet) {
        this.fragmentBuilder = fragmentBuilder;
        this.interpolationBuilder = interpolationBuilder;
        this.featureSet = featureSet;
        this.collector = new SwitchCollector();
    }

    @Override
    public SwitchFragment visit(SwitchInstruction ftl, SwitchFragment input) {
        Node expression = ftl.get(3);
        TemplateObject switchExpression = expression.accept(interpolationBuilder, null);
        Map<NodeType, List<CaseInstruction>> parts = ftl.childrenOfType(CaseInstruction.class).stream().collect(groupingBy(p -> p.get(1).getType()));
        List<CaseInstruction> caseParts = parts.getOrDefault(TokenType.CASE, List.of());
        List<CaseInstruction> switchOnParts = parts.getOrDefault(TokenType.ON, List.of());
        if (!caseParts.isEmpty() && !switchOnParts.isEmpty()) {
            throw new ParsingException("switch directive contains on and case", ftl);
        }
        if (!caseParts.isEmpty()) {
            handle(caseParts, collector, ftl, featureSet.isEnabled(ALLOW_ONLY_CONSTANT_CASES) && featureSet.isEnabled(ALLOW_ONLY_EQUAL_TYPE_CASES));
        }
        if (!switchOnParts.isEmpty()) {
            handle(switchOnParts, collector, ftl, featureSet.isEnabled(ALLOW_ONLY_CONSTANT_ONS) && featureSet.isEnabled(ALLOW_ONLY_EQUAL_TYPE_ONS));
        }
        DefaultInstruction defaultPart = ftl.firstChildOfType(DefaultInstruction.class);
        if (defaultPart != null) {
            defaultPart.accept(this, null);
        }
        if (featureSet.isEnabled(SwitchDirectiveFeature.OPTIMIZE_CONSTANT_SWITCH) && collector.isAllPrimitive) {
            Map<TemplatePrimitive<?>, Fragment> switchMap = collector.fragments.stream()
                    .collect(toMap(c -> (TemplatePrimitive<?>) c.conditional(), ConditionalFragment::content, (a, b) -> a));
            if (switchMap.size() < collector.fragments.size() && featureSet.isEnabled(SwitchDirectiveFeature.ERROR_ON_DUPLICATE_CASE_EXPRESSION)) {
                throw new ParsingException("switch with duplicate conditionals", ftl);
            }
            return new MapSwitchFragment(switchExpression, expression, switchMap, collector.fragment);
        }
        return new ListSwitchFragment(switchExpression, expression, collector.fragments, collector.fragment);
    }

    private <T extends BaseNode> void handle(List<T> switchParts, SwitchCollector switchFragment, SwitchInstruction ftl, boolean isOnlyEqualTypes) {
        switchParts.forEach(part -> part.accept(this, null));
        if (isOnlyEqualTypes && Set.copyOf(switchFragment.fragments).size() > 1) {
            throw new ParsingException("constants with different types", ftl);
        }
    }

    @Override
    public SwitchFragment visit(CaseInstruction ftl, SwitchFragment input) {
        logger.debug("{} {}", ftl.size(), ftl.children());
        int blockIndex = ftl.indexOf(ftl.firstChildOfType(TokenType.CLOSE_TAG)) + 1;
        checkMissingBlock(blockIndex, ftl);
        Node block = ftl.get(blockIndex);
        List<Fragment> fragments = block.accept(fragmentBuilder, new ArrayList<>());
        Fragment caseBlock = Fragments.optimizeWithVariableContext(fragments);
        logger.debug("{}: {} {}", ftl.get(1), block, caseBlock);
        boolean onlyConstantsAllowed = featureSet.isEnabled(ftl.get(1).getType() == TokenType.ON ? ALLOW_ONLY_CONSTANT_ONS : ALLOW_ONLY_CONSTANT_CASES);
        for (int i = 3; i < blockIndex - 1; i +=2) {
            TemplateObject onExpression = ftl.get(i).accept(interpolationBuilder, null);
            if (onlyConstantsAllowed && !onExpression.isPrimitive()) {
                throw new ParsingException("only constant expression allowed", ftl.get(i));
            }
            logger.debug("conditional: {}", ftl.get(i));
            collector.addFragment(new ConditionalFragment(onExpression, caseBlock, ftl.get(i)));
        }
        return input;
    }

    @Override
    public SwitchFragment visit(DefaultInstruction ftl, SwitchFragment input) {
        checkMissingBlock(3, ftl);
        Node block = ftl.get(3);
        List<Fragment> fragments = block.accept(fragmentBuilder, new ArrayList<>());
        collector.addDefaultFragment(Fragments.optimizeWithVariableContext(fragments));
        return input;
    }

    private static void checkMissingBlock(int position, Node ftl) {
        if (ftl.size() == position) {
            throw new ParsingException("missing block", ftl);
        }
    }
}
