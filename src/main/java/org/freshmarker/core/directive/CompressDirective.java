package org.freshmarker.core.directive;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class CompressDirective implements UserDirective {

    private static class CompressWriter extends FilterWriter {
        private boolean prefix = true;
        char replacement = 0;
        CompressWriter(Writer out) {
            super(out);
        }

        @Override
        public void write(String str) throws IOException {
            if (prefix) {
                str = str.replaceAll("^ +", "");
                prefix = false;
            }
            if (str.indexOf(' ') != -1 || str.indexOf('\n') != -1 || str.indexOf('\t') != -1) {
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
                out.write(builder.toString());
            } else {
                out.write(str);
            }
        }

        @Override
        public void close() throws IOException {
            if (replacement != 0) {
                out.write(replacement);
                replacement = 0;
            }
        }
    }


    @Override
    public void execute(ProcessContext context, Map<String, TemplateObject> args, Fragment body) {
        Writer oldWriter = context.getWriter();
        try (CompressWriter writer = new CompressWriter(context.getWriter())) {
            context.setWriter(writer);
            body.process(context);
        } catch (IOException e) {
            throw new ProcessException("flatten file writer: " + e.getMessage(), e);
        } finally {
            context.setWriter(oldWriter);
        }
    }
}
