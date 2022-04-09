package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.ast.ElseBlock;
import ftl.ast.ElseIfBlock;
import ftl.ast.IfStatement;
import java.util.List;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.ConditionalFragment;
import org.freshmarker.core.fragment.IfFragment;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class IfFragmentBuilder implements FtlVisitor<IfFragment, IfFragment> {

  private static final Logger logger = LoggerFactory.getLogger(IfFragmentBuilder.class);

  private final InterpolationBuilder interpolationBuilder = new InterpolationBuilder();

  @Override
  public IfFragment visit(IfStatement ftl, IfFragment input) {
    IfFragment ifFragment = new IfFragment();
    Node expression = ftl.getChild(3);
    TemplateObject ifExpression = expression.accept(interpolationBuilder, null);
    Node block = ftl.getChild(5);
    BlockFragment ifBlock = block.accept(new FragmentBuilder(), new BlockFragment());
    ConditionalFragment ifPart = new ConditionalFragment(ifExpression, ifBlock);
    ifFragment.addFragment(ifPart);
    List<ElseIfBlock> elseIfParts = ftl.childrenOfType(ElseIfBlock.class);
    elseIfParts.forEach(elseIfPart -> elseIfPart.accept(this, ifFragment));
    ElseBlock elsePart = ftl.firstChildOfType(ElseBlock.class);
    if (elsePart != null) {
      elsePart.accept(this, ifFragment);
    }
    return ifFragment;
  }

  @Override
  public IfFragment visit(ElseIfBlock ftl, IfFragment input) {
    Node expression = ftl.getChild(3);
    TemplateObject ifExpression = expression.accept(interpolationBuilder, null);
    Node block = ftl.getChild(5);
    BlockFragment ifBlock = block.accept(new FragmentBuilder(), new BlockFragment());
    input.addFragment(new ConditionalFragment(ifExpression, ifBlock));
    return input;
  }

  @Override
  public IfFragment visit(ElseBlock ftl, IfFragment input) {
    Node block = ftl.getChild(3);
    BlockFragment ifBlock = block.accept(new FragmentBuilder(), new BlockFragment());
    input.addFragment(new ConditionalFragment(TemplateBoolean.TRUE, ifBlock));
    return input;
  }
}
