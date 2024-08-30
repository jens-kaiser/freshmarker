package org.freshmarker.core.directive;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class LoggingDirective implements UserDirective {

  @Override
  public void execute(ProcessContext context, Map<String, TemplateObject> args, Fragment body) {
    if (body != null) {
      throw new ProcessException("body on log not allowed");
    }
    try {
      String message = getString(args, "message", context).orElse("");
      String level = getString(args, "level", context).orElse("info");
      if ("warn".equals(level)) {
        message = message.toUpperCase();
      }
      Environment environment = context.getEnvironment();
      context.getWriter().write(context.getOutputFormat().comment(environment, message).getValue());
    } catch (IOException e) {
      throw new ProcessException(e.getMessage(), e);
    }
  }

  private Optional<String> getString(Map<String, TemplateObject> args, String message, ProcessContext context) {
    return Optional.ofNullable(args.get(message)).map(t -> t.evaluate(context, TemplateString.class)).map(TemplateString::getValue);
  }
}
