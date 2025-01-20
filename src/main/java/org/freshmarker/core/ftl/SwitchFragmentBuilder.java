package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token.TokenType;
import ftl.ast.CaseInstruction;
import ftl.ast.DefaultInstruction;
import ftl.ast.SwitchInstruction;
import ftl.ast.SwitchOnInstruction;
import org.freshmarker.core.SwitchDirectiveFeature;
import org.freshmarker.core.features.FeatureSet;
import org.freshmarker.core.fragment.ConditionalFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.Fragments;
import org.freshmarker.core.fragment.SwitchFragment;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_CONSTANT_CASES;
import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_CONSTANT_ONS;
import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_EQUAL_TYPE_CASES;
import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_EQUAL_TYPE_ONS;

class SwitchFragmentBuilder implements FtlVisitor<SwitchFragment, SwitchFragment> {

    private static final Logger logger = LoggerFactory.getLogger(SwitchFragmentBuilder.class);

    private final FragmentBuilder fragmentBuilder;
    private final FeatureSet featureSet;

    public SwitchFragmentBuilder(FragmentBuilder fragmentBuilder, FeatureSet featureSet) {
    this.fragmentBuilder = fragmentBuilder;
        this.featureSet = featureSet;
    }

    @Override
    public SwitchFragment visit(SwitchInstruction ftl, SwitchFragment input) {
        logger.debug("children: {}", ftl.children());
        Node expression = ftl.get(3);
        TemplateObject switchExpression = expression.accept(InterpolationBuilder.INSTANCE, null);
        SwitchFragment switchFragment = new SwitchFragment(switchExpression, expression);
        List<CaseInstruction> caseParts = ftl.childrenOfType(CaseInstruction.class);
        List<SwitchOnInstruction> switchOnParts = ftl.childrenOfType(SwitchOnInstruction.class);
        if (!caseParts.isEmpty() && !switchOnParts.isEmpty()) {
            throw new ParsingException("switch directive contains on and case", ftl);
        }
        if (!caseParts.isEmpty()) {
            handle(caseParts, switchFragment, ftl, featureSet.isEnabled(ALLOW_ONLY_CONSTANT_CASES) && featureSet.isEnabled(ALLOW_ONLY_EQUAL_TYPE_CASES));
        }
        if (!switchOnParts.isEmpty()) {
            handle(switchOnParts, switchFragment, ftl, featureSet.isEnabled(ALLOW_ONLY_CONSTANT_ONS) && featureSet.isEnabled(ALLOW_ONLY_EQUAL_TYPE_ONS));
        }
        DefaultInstruction defaultPart = ftl.firstChildOfType(DefaultInstruction.class);
        if (defaultPart != null) {
            defaultPart.accept(this, switchFragment);
        }
        return switchFragment;
    }

    private <T extends BaseNode> void handle(List<T> switchParts, SwitchFragment switchFragment, SwitchInstruction ftl, boolean isOnlyEqualTypes) {
        switchParts.forEach(part -> part.accept(this, switchFragment));
        if (isOnlyEqualTypes && Set.copyOf(switchFragment.getConditionals()).size() > 1) {
            throw new ParsingException("constants with different types", ftl);
        }
    }

    @Override
    public SwitchFragment visit(CaseInstruction ftl, SwitchFragment input) {
        logger.debug("{} {}", ftl.size(), ftl.children());
        Node expression = ftl.get(3);
        TemplateObject caseExpression = expression.accept(InterpolationBuilder.INSTANCE, null);
        if (featureSet.isEnabled(ALLOW_ONLY_CONSTANT_CASES) && !caseExpression.isPrimitive()) {
            throw new ParsingException("only constant expression allowed", expression);
        }
        checkMissingBlock(5, ftl);
        Node block = ftl.get(5);
        List<Fragment> fragments = block.accept(fragmentBuilder, new ArrayList<>());
        Fragment caseBlock = Fragments.optimize(fragments);
        logger.debug("case {} {}", block, caseBlock);
        input.addFragment(new ConditionalFragment(caseExpression, caseBlock, ftl));
        return input;
    }

    @Override
    public SwitchFragment visit(SwitchOnInstruction ftl, SwitchFragment input) {
        logger.debug("{} {}", ftl.size(), ftl.children());
        int blockIndex = ftl.indexOf(ftl.firstChildOfType(TokenType.CLOSE_TAG)) + 1;
        List<TemplateObject> expressions = new LinkedList<>();
        for (int i = 3; i < blockIndex - 1; i +=2) {
            TemplateObject onExpression = ftl.get(i).accept(InterpolationBuilder.INSTANCE, null);
            if (featureSet.isEnabled(SwitchDirectiveFeature.ALLOW_ONLY_CONSTANT_ONS) && !onExpression.isPrimitive()) {
                throw new ParsingException("only constant expression allowed", ftl.get(i));
            }
            expressions.add(onExpression);
        }
        checkMissingBlock(blockIndex, ftl);
        Node block = ftl.get(blockIndex);
        List<Fragment> fragments = block.accept(fragmentBuilder, new ArrayList<>());
        Fragment caseBlock = Fragments.optimize(fragments);
        logger.debug("on: {} {}", block, caseBlock);
        expressions.stream().map(expression -> new ConditionalFragment(expression, caseBlock, ftl)).forEach(input::addFragment);
        return input;
    }

    @Override
    public SwitchFragment visit(DefaultInstruction ftl, SwitchFragment input) {
        checkMissingBlock(3, ftl);
        Node block = ftl.get(3);
        List<Fragment> fragments = block.accept(fragmentBuilder, new ArrayList<>());
        input.addDefaultFragment(Fragments.optimize(fragments));
        return input;
    }

    private static void checkMissingBlock(int position, Node ftl) {
        if (ftl.size() == position) {
            throw new ParsingException("missing block", ftl);
        }
    }
}
