package org.freshmarker.core.fragment;

import java.io.IOException;
import java.io.Writer;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;

public class InterpolationFragment implements Fragment {
  private final TemplateObject expression;

  public InterpolationFragment(TemplateObject expression) {
    this.expression = expression;
  }

  @Override
  public void process(Environment environment, Writer writer) {
    try {
      TemplateObject templateObject = expression;
      do {
        templateObject = templateObject.evaluateToObject(environment);
      } while (!templateObject.isPrimitive());
      writer.write(environment.getFormatter(templateObject.getClass()).format(templateObject, environment.getLocale()));
    } catch (IOException e) {
      throw new ProcessException(e.getMessage(), e);
    }
  }
}
