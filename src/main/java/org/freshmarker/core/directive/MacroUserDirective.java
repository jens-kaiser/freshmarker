package org.freshmarker.core.directive;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.WrapperEnvironment;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.Fragment;
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
  public void execute(ProcessContext context, Map<String, TemplateObject> args, BlockFragment body) {
    Map<String, TemplateObject> values = evaluateParameterValues(args);
    log.info("macro parameter values: {}", values);
    context.setEnvironment(new WrapperEnvironment(context.getEnvironment()) {
      @Override
      public TemplateObject getValue(String name) {
        TemplateObject value = values.get(name);
        return value != null ? value : super.getValue(name);
      }

      @Override
      public Optional<Fragment> getNestedContent() {
        return Optional.ofNullable(body);
      }
    });
    block.process(context);
  }

  private Map<String, TemplateObject> evaluateParameterValues(Map<String, TemplateObject> args) {
    if (parameterList.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, TemplateObject> values = new HashMap<>();
    for (ParameterHolder parameterHolder : parameterList) {
      TemplateObject value = args.get(parameterHolder.getName());
      log.debug("macro parameter value: {} {}", parameterHolder.getName(), value);
      value = value == null ? parameterHolder.getDefaultValue() : value;
      if (value == null) {
        throw new ProcessException("missing parameter " + parameterHolder.getName());
      }
      values.put(parameterHolder.getName(), value);
    }
    return values;
  }
}
