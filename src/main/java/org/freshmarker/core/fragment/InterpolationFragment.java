package org.freshmarker.core.fragment;

import java.io.IOException;
import java.io.Writer;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateObject;

public class InterpolationFragment implements Fragment {
  private final TemplateObject expression;

  public InterpolationFragment(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public void process(Environment environment, Writer writer) {
    try {
      writer.write(expression.evaluateToObject(environment).evaluate(environment));
    } catch (IOException e) {
      throw new ProcessException(e.getMessage(), e);
    }
  }
}
