package org.freshmarker.core.fragment;

import ftl.ast.Interpolation;
import java.io.IOException;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class InterpolationFragment implements Fragment {

  private final TemplateObject expression;
  private final Interpolation ftl;

  public InterpolationFragment(TemplateObject expression, Interpolation ftl) {
    this.expression = expression;
    this.ftl = ftl;
  }

  @Override
  public void process(ProcessContext context) {
    try {
      TemplateString templateObject = (TemplateString) expression.evaluateToObject(context);
      context.getWriter().write(templateObject.getValue());
    } catch (IOException e) {
      throw new ProcessException(e.getMessage(), e);
    } catch (UnsupportedBuiltInException e) {
      throw new UnsupportedBuiltInException(e.getMessage(), ftl, e);
    } catch (WrongTypeException e) {
      throw new WrongTypeException(e.getMessage(), ftl, e);
    }
  }
}
