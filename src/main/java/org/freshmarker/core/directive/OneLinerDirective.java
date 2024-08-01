package org.freshmarker.core.directive;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.environment.WriterEnvironment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class OneLinerDirective implements UserDirective {

    private static class FlattenFilterWriter extends FilterWriter {

        FlattenFilterWriter(Writer out) {
            super(out);
        }

        @Override
        public void write(String str) throws IOException {
            out.write(str.replace('\n', ' '));
        }
    }

    @Override
    public void execute(ProcessContext context, Map<String, TemplateObject> args, Fragment body) {
        if (body == null) {
            throw new ProcessException("one-liner body missing");
        }
        FlattenFilterWriter writer = new FlattenFilterWriter(context.getWriter());
        Environment environment = context.getEnvironment();
        context.setEnvironment(new WriterEnvironment(writer, environment));
        try {
            body.process(context);
        } finally {
            context.setEnvironment(environment);
        }
    }
}
