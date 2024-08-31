package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.ast.CaseInstruction;
import ftl.ast.DefaultInstruction;
import ftl.ast.SwitchInstruction;
import org.freshmarker.core.fragment.ConditionalFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.Fragments;
import org.freshmarker.core.fragment.SwitchFragment;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
        caseParts.forEach(elseIfPart -> elseIfPart.accept(this, switchFragment));
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
        Node block = ftl.get(5);
        List<Fragment> fragments = block.accept(fragmentBuilder, new ArrayList<>());
        Fragment caseBlock = Fragments.optimize(fragments);
        logger.debug("{} {}", block, caseBlock);
        input.addFragment(new ConditionalFragment(caseExpression, caseBlock, expression));
        return input;
    }

    @Override
    public SwitchFragment visit(DefaultInstruction ftl, SwitchFragment input) {
        Node block = ftl.get(3);
        List<Fragment> fragments = block.accept(fragmentBuilder, new ArrayList<>());
        input.addDefaultFragment(Fragments.optimize(fragments));
        return input;
    }
}
