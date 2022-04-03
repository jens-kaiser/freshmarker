package org.freshmarker.core.buildin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.freshmarker.core.Environment;
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
  public void validate(List<TemplateObject> parameters) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameters.size() + 2 < parameterTypes.length) {
      throw new ProcessException(
          "invalid parameter count:" + parameters.size() + " expected: " + (parameterTypes.length - 2));
    }
    for (int i = 0, j = withEnvironment ? 2 : 1; j < parameterTypes.length; i++, j++) {
      if (!parameterTypes[i].isAssignableFrom(parameters.get(i).getClass())) {
        throw new ProcessException("invalid parameter type: " + parameters.get(i) + " expected: " + parameterTypes[i]);
      }
    }
  }

  @Override
  public TemplateObject apply(TemplateObject value, List<TemplateObject> parameter, Environment environment) {
    try {
      if (withVarargs) {
        return applyWithDynamicParameter(value, parameter, environment);
      }
      return applyWithStaticParameter(value, parameter, environment);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new ProcessException("cannot invoke builtIn: " + e.getMessage());
    }
  }

  private TemplateObject applyWithDynamicParameter(TemplateObject value, List<TemplateObject> parameter,
      Environment environment) throws InvocationTargetException, IllegalAccessException {
    int parameterCount = method.getParameterCount();
    if (parameter.size() + 2 < parameterCount) {
      throw new ProcessException("wrong parameter count: ");
    }
    int firstBuiltInParameter = withEnvironment ? 2 : 1;
    Object[] args = new Object[parameterCount];
    args[0] = value;
    args[1] = environment;
    for (int i = 0, j = firstBuiltInParameter, n = Math.min(parameterCount - firstBuiltInParameter,
        parameter.size() - 1); i < n; i++, j++) {
      args[j] = parameter.get(i);
    }
    args[args.length - 1] = parameter.subList(parameterCount - firstBuiltInParameter - 1, parameter.size())
        .toArray(new TemplateObject[0]);
    return (TemplateObject) method.invoke(null, args);
  }

  private TemplateObject applyWithStaticParameter(TemplateObject value, List<TemplateObject> parameter,
      Environment environment) throws InvocationTargetException, IllegalAccessException {
    int parameterCount = method.getParameterCount();
    int firstBuiltInParameter = withEnvironment ? 2 : 1;
    if (parameterCount != 1 && parameter.size() + firstBuiltInParameter != parameterCount) {
      throw new ProcessException("wrong parameter count");
    }
    Object[] args = new Object[parameterCount];
    args[0] = value;
    if (parameterCount > 1) {
      args[1] = environment;
      for (int i = 0, j = firstBuiltInParameter, n = parameterCount - firstBuiltInParameter; i < n; i++, j++) {
        args[j] = parameter.get(i);
      }
    }
    return (TemplateObject) method.invoke(null, args);
  }
}
