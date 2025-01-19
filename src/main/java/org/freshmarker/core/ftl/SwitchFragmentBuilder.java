package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token.TokenType;
import ftl.ast.CaseInstruction;
import ftl.ast.DefaultInstruction;
import ftl.ast.SwitchInstruction;
import ftl.ast.SwitchOnInstruction;
import org.freshmarker.core.fragment.ConditionalFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.Fragments;
import org.freshmarker.core.fragment.SwitchFragment;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class SwitchFragmentBuilder implements FtlVisitor<SwitchFragment, SwitchFragment> {

  private static final Logger logger = LoggerFactory.getLogger(SwitchFragmentBuilder.class);

  private final FragmentBuilder fragmentBuilder;

  public SwitchFragmentBuilder(FragmentBuilder fragmentBuilder) {
    this.fragmentBuilder = fragmentBuilder;
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
        (caseParts.isEmpty() ? switchOnParts : caseParts).forEach(part -> part.accept(this, switchFragment));
        DefaultInstruction defaultPart = ftl.firstChildOfType(DefaultInstruction.class);
        if (defaultPart != null) {
            defaultPart.accept(this, switchFragment);
        }
        return switchFragment;
    }

    @Override
    public SwitchFragment visit(CaseInstruction ftl, SwitchFragment input) {
        logger.debug("{} {}", ftl.size(), ftl.children());
        Node expression = ftl.get(3);
        TemplateObject caseExpression = expression.accept(InterpolationBuilder.INSTANCE, null);
        checkMissingBlock(5, ftl);
        Node block = ftl.get(5);
        List<Fragment> fragments = block.accept(fragmentBuilder, new ArrayList<>());
        Fragment caseBlock = Fragments.optimize(fragments);
        logger.debug("case {} {}", block, caseBlock);
        input.addFragment(new ConditionalFragment(caseExpression, caseBlock, expression));
        return input;
    }

    @Override
    public SwitchFragment visit(SwitchOnInstruction ftl, SwitchFragment input) {
        logger.debug("{} {}", ftl.size(), ftl.children());
        System.out.println(ftl.stream().map(Node::getType).toList());
        int blockIndex = ftl.indexOf(ftl.firstChildOfType(TokenType.CLOSE_TAG)) + 1;
        Node expression = ftl.get(3);
        TemplateObject onExpression = expression.accept(InterpolationBuilder.INSTANCE, null);
        List<Object> expressions = new LinkedList<>();
        expressions.add(onExpression);
        for (int i = 3; i < blockIndex - 1; i +=2) {
           expressions.add(ftl.get(i).accept(InterpolationBuilder.INSTANCE, null));
        }
        checkMissingBlock(blockIndex, ftl);
        Node block = ftl.get(blockIndex);
        List<Fragment> fragments = block.accept(fragmentBuilder, new ArrayList<>());
        Fragment caseBlock = Fragments.optimize(fragments);
        logger.debug("on: {} {}", block, caseBlock);
        input.addFragment(new ConditionalFragment(new TemplateListSequence(expressions), caseBlock, expression));
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
