package org.freshmarker.core.directive;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.fragment.ConstantFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

/**
 * This {@link UserDirective} writes a comment in the template output.
 * The form of the comment is based on the current output format.
 * The content of the comment comes from the parameter {@code message}
 * The {@code level} parameter modifies the output. For the value {@code warn}, the output is converted to uppercase.
 */
public class LoggingDirective implements UserDirective {

  @Override
  public void execute(ProcessContext context, Map<String, TemplateObject> args, Fragment body) {
    if (body != ConstantFragment.EMPTY) {
      throw new ProcessException("body on log not allowed:");
    }
    try {
      String message = getString(args, "message", context).orElse("");
      String level = getString(args, "level", context).orElse("info");
      if ("warn".equals(level)) {
        message = message.toUpperCase();
      }
      context.getWriter().write(context.getOutputFormat().comment(new TemplateString(message)).getValue());
    } catch (IOException e) {
      throw new ProcessException(e.getMessage(), e);
    }
  }

  private Optional<String> getString(Map<String, TemplateObject> args, String message, ProcessContext context) {
    return Optional.ofNullable(args.get(message)).map(t -> t.evaluate(context, TemplateString.class)).map(TemplateString::getValue);
  }
}
