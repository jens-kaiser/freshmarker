package org.freshmarker.core.directive;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.environment.MacroEnvironment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.TemplateReturnException;
import org.freshmarker.core.ftl.ParameterHolder;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroUserDirective implements UserDirective {

  private static final Logger log = LoggerFactory.getLogger(MacroUserDirective.class);

  private final Fragment block;
  private final List<ParameterHolder> parameterList;

  public MacroUserDirective(Fragment block, List<ParameterHolder> parameterList) {
    this.block = block;
    this.parameterList = parameterList;
  }

  @Override
  public void execute(ProcessContext context, Map<String, TemplateObject> args, Fragment body) {
    Map<String, TemplateObject> values = evaluateParameterValues(args, context);
    Environment environment = context.getEnvironment();
    context.setEnvironment(new MacroEnvironment(environment, values, body));
    try {
      block.process(context);
    } catch (TemplateReturnException e) {
      log.debug("return exception: {}", e.getMessage());
    } finally {
      context.setEnvironment(environment);
    }
  }

  private Map<String, TemplateObject> evaluateParameterValues(Map<String, TemplateObject> args, ProcessContext context) {
    if (parameterList.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, TemplateObject> values = new HashMap<>();
    for (ParameterHolder parameterHolder : parameterList) {
      TemplateObject value = args.get(parameterHolder.name());
      value = value == null ? parameterHolder.defaultValue() : value;
      if (value == null) {
        throw new ProcessException("missing parameter " + parameterHolder.name());
      }
      values.put(parameterHolder.name(), value.evaluateToObject(context));
    }
    return values;
  }
}
