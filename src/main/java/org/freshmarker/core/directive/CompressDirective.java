package org.freshmarker.core.directive;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * This {@link UserDirective} replaces consecutive spaces and line breaks with a single space or newline.
 * Spaces at the beginning or end are truncated.
 */
public class CompressDirective implements UserDirective {

    private static class CompressWriter extends FilterWriter {
        private boolean prefix = true;
        char replacement = 0;
        CompressWriter(Writer out) {
            super(out);
        }

        @Override
        public void write(String str) throws IOException {
            if (str.indexOf(' ') == -1 && str.indexOf('\n') == -1 && str.indexOf('\r') == -1) {
                if (replacement != 0) {
                    out.write(replacement);
                    replacement = 0;
                }
                out.write(str);
                prefix = false;
                return;
            }
            if (prefix) {
                str = str.replaceAll("^ +", "");
                prefix = false;
            }
            out.write(handleWhitespaces(str));
        }

        private String handleWhitespaces(String str) {
            StringBuilder builder = new StringBuilder();
            replacement = 0;
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                switch (c) {
                    case ' ' ->  replacement = replacement == 0 ? ' ' : replacement;
                    case '\n', '\r' -> replacement = '\n';
                    default -> {
                        if (replacement != 0) {
                            builder.append(replacement);
                            replacement = 0;
                        }
                        builder.append(c);
                    }
                }
            }
            return builder.toString();
        }

        @Override
        public void close() throws IOException {
            if (replacement == '\n') {
                out.write(replacement);
                replacement = 0;
            }
            out.close();
        }
    }

    @Override
    public void execute(ProcessContext context, Map<String, TemplateObject> args, Fragment body) {
        Writer oldWriter = context.getWriter();
        try (CompressDirective.CompressWriter writer = new CompressWriter(context.getWriter())) {
            context.setWriter(writer);
            body.process(context);
        } catch (IOException e) {
            throw new ProcessException("flatten file writer: " + e.getMessage(), e);
        } finally {
            context.setWriter(oldWriter);
        }
    }
}
