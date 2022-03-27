package org.freshmarker.core.ftl;

import ftl.ast.PositionalArgsList;
import java.util.List;
import org.freshmarker.core.model.primitive.TemplateObject;

public class ParameterListBuilder implements
    ExpressionVisitor<List<TemplateObject>, List<TemplateObject>> {

  @Override
  public List<TemplateObject> visit(PositionalArgsList expression, List<TemplateObject> input) {
    for (int i = 0; i < expression.getChildCount(); i += 2) {
      System.out.println(i + " " + expression.getChildCount()+ " " + expression.getChild(i));
      input.add(expression.getChild(i).accept(new InterpolationBuilder(), null));
    }
    return input;
  }
}
