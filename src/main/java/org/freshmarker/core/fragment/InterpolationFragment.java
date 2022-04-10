package org.freshmarker.core.fragment;

import java.io.IOException;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class InterpolationFragment implements Fragment {
  private final TemplateObject expression;

  public InterpolationFragment(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public void process(ProcessContext context) {
    try {
      TemplateString templateObject = (TemplateString)expression.evaluateToObject(context);
      context.getWriter().write(templateObject.getValue());
    } catch (IOException e) {
      throw new ProcessException(e.getMessage(), e);
    }
  }
}
