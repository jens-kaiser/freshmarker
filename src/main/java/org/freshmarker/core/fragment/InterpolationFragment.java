package org.freshmarker.core.fragment;

import java.io.IOException;
import java.io.Writer;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class InterpolationFragment implements Fragment {
  private final TemplateObject expression;

  public InterpolationFragment(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public void process(Environment environment, Writer writer) {
    try {
      TemplateString templateObject = (TemplateString)expression.evaluateToObject(environment);
      writer.write(templateObject.getValue());
    } catch (IOException e) {
      throw new ProcessException(e.getMessage(), e);
    }
  }
}
