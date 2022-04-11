package org.freshmarker.core.buildin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;

public class MethodBuiltIn implements BuiltIn {

  private final Method method;
  private final boolean withEnvironment;
  private final boolean withVarargs;

  public MethodBuiltIn(Method method, boolean withEnvironment, boolean withVarargs) {
    this.method = method;
    this.withEnvironment = withEnvironment;
    this.withVarargs = withVarargs;
  }

  @Override
  public TemplateObject apply(TemplateObject value, List<TemplateObject> parameter, ProcessContext context) {
    try {
      if (withVarargs) {
        return applyWithDynamicParameter(value, parameter, context);
      }
      return applyWithStaticParameter(value, parameter, context);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new ProcessException("cannot invoke builtIn: " + e.getMessage(), e);
    }
  }

  private TemplateObject applyWithDynamicParameter(TemplateObject value, List<TemplateObject> parameter,
      ProcessContext context) throws InvocationTargetException, IllegalAccessException {
    int parameterCount = method.getParameterCount();
    int firstBuiltInParameter = withEnvironment ? 2 : 1;
    if (parameter.size() + firstBuiltInParameter < parameterCount) {
      throw new ProcessException("wrong parameter count: " + (parameter.size() + 2) + " < " + parameterCount);
    }
    Object[] args = new Object[parameterCount];
    args[0] = value;
    args[1] = context;
    for (int i = 0, j = firstBuiltInParameter, n = Math.min(parameterCount - firstBuiltInParameter,
        parameter.size() - 1); i < n; i++, j++) {
      args[j] = parameter.get(i);
    }
    args[args.length - 1] = parameter.subList(parameterCount - firstBuiltInParameter - 1, parameter.size())
        .toArray(new TemplateObject[0]);
    return (TemplateObject) method.invoke(null, args);
  }

  private TemplateObject applyWithStaticParameter(TemplateObject value, List<TemplateObject> parameter,
      ProcessContext context) throws InvocationTargetException, IllegalAccessException {
    int parameterCount = method.getParameterCount();
    int firstBuiltInParameter = withEnvironment ? 2 : 1;
    if (parameterCount != 1 && parameter.size() + firstBuiltInParameter != parameterCount) {
      throw new ProcessException("wrong parameter count");
    }
    Object[] args = new Object[parameterCount];
    args[0] = value;
    if (parameterCount > 1) {
      args[1] = context;
      for (int i = 0, j = firstBuiltInParameter, n = parameterCount - firstBuiltInParameter; i < n; i++, j++) {
        args[j] = parameter.get(i);
      }
    }
    return (TemplateObject) method.invoke(null, args);
  }
}
