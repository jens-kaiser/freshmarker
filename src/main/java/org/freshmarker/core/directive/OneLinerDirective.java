package org.freshmarker.core.directive;

import org.freshmarker.api.NamedUserDirective;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * This {@link UserDirective} replaces all newlines by spaces.
 */
public class OneLinerDirective implements NamedUserDirective {

    @Override
    public String name() {
        return "oneliner";
    }

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
        Writer oldWriter = context.getWriter();
        FlattenFilterWriter writer = new FlattenFilterWriter(context.getWriter());
        context.setWriter(writer);
        try {
            body.process(context);
        } finally {
            context.setWriter(oldWriter);
        }
    }
}
