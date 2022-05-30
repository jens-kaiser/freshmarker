package org.freshmarker.core.directive;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.WriterEnvironment;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.model.TemplateObject;

public class OneLinerDirective implements UserDirective {

  private static class FlattenFilterWriter extends FilterWriter {

    FlattenFilterWriter(Writer out) {
      super(out);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
      char[] transformedCbuf = new char[len];
      for (int i = 0; i < len; i++) {
        char c = cbuf[i + off];
        transformedCbuf[i] = c == '\n' ? ' ' : c;
      }
      out.write(transformedCbuf);
    }

    @Override
    public void write(String str) throws IOException {
      out.write(str.replace('\n', ' '));
    }
  }

  @Override
  public void execute(ProcessContext context, Map<String, TemplateObject> args, BlockFragment body) {
    if (body == null) {
      throw new ProcessException("one-liner body missing");
    }
    FlattenFilterWriter writer = new FlattenFilterWriter(context.getWriter());
    body.process(new ProcessContext(new WriterEnvironment(writer, context.getEnvironment()), context));
  }
}
