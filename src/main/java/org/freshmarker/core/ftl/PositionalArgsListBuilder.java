package org.freshmarker.core.ftl;

import ftl.ast.PositionalArgsList;
import java.util.List;
import org.freshmarker.core.InterpolationListener;
import org.freshmarker.core.model.TemplateObject;

public class PositionalArgsListBuilder implements
    ExpressionVisitor<List<TemplateObject>, List<TemplateObject>> {
  private final InterpolationListener listener;

  public PositionalArgsListBuilder(InterpolationListener listener) {
    this.listener = listener;
  }

  @Override
  public List<TemplateObject> visit(PositionalArgsList expression, List<TemplateObject> input) {
    for (int i = 0; i < expression.getChildCount(); i += 2) {
      input.add(expression.getChild(i).accept(new InterpolationBuilder(listener), null));
    }
    return input;
  }
}
