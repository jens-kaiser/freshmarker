package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.ast.CaseInstruction;
import ftl.ast.DefaultInstruction;
import ftl.ast.SwitchInstruction;
import java.util.List;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.ConditionalFragment;
import org.freshmarker.core.fragment.ConstantFragment;
import org.freshmarker.core.fragment.SwitchFragment;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SwitchFragmentBuilder implements FtlVisitor<SwitchFragment, SwitchFragment> {

  private static final Logger logger = LoggerFactory.getLogger(SwitchFragmentBuilder.class);

  private final InterpolationBuilder interpolationBuilder = new InterpolationBuilder();

  @Override
  public SwitchFragment visit(SwitchInstruction ftl, SwitchFragment input) {
    logger.info("children: {}", ftl.children());
    Node expression = ftl.getChild(3);
    TemplateObject switchExpression = expression.accept(interpolationBuilder, null);
    SwitchFragment switchFragment = new SwitchFragment(switchExpression, expression);
    List<CaseInstruction> caseParts = ftl.childrenOfType(CaseInstruction.class);
    caseParts.forEach(elseIfPart -> elseIfPart.accept(this, switchFragment));
    DefaultInstruction defaultPart = ftl.firstChildOfType(DefaultInstruction.class);
    if (defaultPart != null) {
      defaultPart.accept(this, switchFragment);
    } else {
      switchFragment.addDefaultFragment(ConstantFragment.EMPTY);
    }
    return switchFragment;
  }

  @Override
  public SwitchFragment visit(CaseInstruction ftl, SwitchFragment input) {
    logger.info("{} {}", ftl.getChildCount(), ftl.children());
    Node expression = ftl.getChild(3);
    TemplateObject caseExpression = expression.accept(interpolationBuilder, null);
    Node block = ftl.getChild(5);
    BlockFragment caseBlock = block.accept(new FragmentBuilder(), new BlockFragment());
    logger.info("{} {}", block, caseBlock);
    input.addFragment(new ConditionalFragment(caseExpression, caseBlock, expression));
    return input;
  }

  @Override
  public SwitchFragment visit(DefaultInstruction ftl, SwitchFragment input) {
    Node block = ftl.getChild(3);
    BlockFragment defaultBlock = block.accept(new FragmentBuilder(), new BlockFragment());
    input.addDefaultFragment(defaultBlock);
    return input;
  }
}
