package org.freshmarker.core.ftl;

import ftl.ast.ElseBlock;
import ftl.ast.ElseIfBlock;
import ftl.ast.IfStatement;
import java.util.List;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.ConditionalFragment;
import org.freshmarker.core.fragment.IfFragment;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.TemplateObject;

class IfFragmentBuilder implements FtlVisitor<IfFragment, IfFragment> {
  private final InterpolationBuilder interpolationBuilder = new InterpolationBuilder();

  @Override
  public IfFragment visit(IfStatement ftl, IfFragment input) {
    IfFragment ifFragment = new IfFragment();
    TemplateObject ifExpression = ftl.getChild(3).accept(interpolationBuilder, null);
    BlockFragment ifBlock = ftl.getChild(5).accept(new FragmentBuilder(), new BlockFragment());
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
    TemplateObject ifExpression = ftl.getChild(3).accept(interpolationBuilder, null);
    BlockFragment ifBlock = ftl.getChild(5).accept(new FragmentBuilder(), new BlockFragment());
    input.addFragment(new ConditionalFragment(ifExpression, ifBlock));
    return input;
  }

  @Override
  public IfFragment visit(ElseBlock ftl, IfFragment input) {
    BlockFragment ifBlock = ftl.getChild(3).accept(new FragmentBuilder(), new BlockFragment());
    input.addFragment(new ConditionalFragment(TemplateBoolean.TRUE, ifBlock));
    return input;
  }
}
