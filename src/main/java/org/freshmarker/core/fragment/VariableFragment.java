package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;

public class VariableFragment implements Fragment {

  private final String name;
  private final TemplateObject expression;

  private final boolean exists;

  public VariableFragment(String name, TemplateObject expression, boolean exists) {
    this.name = name;
    this.expression = expression;
    this.exists = exists;
  }

  @Override
  public void process(ProcessContext context) {
    TemplateObject value = context.getEnvironment().getValue(name);
    if (exists == (value == TemplateNull.NULL)) {
      throw new ProcessException("variable " + name + (exists ? " already exists" : " does not exists"));
    }
    context.getEnvironment().setVariable(name, expression.evaluateToObject(context));
  }
}
