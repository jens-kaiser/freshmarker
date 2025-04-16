package org.freshmarker.core.output;

import org.freshmarker.core.model.primitive.TemplateString;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OutputFormatBuilder {
    private final Map<Character, String> escapes = new HashMap<>();

    private String commentPrefix;
    private String commentSuffix;

    public OutputFormatBuilder withComment(String prefix, String suffix) {
        this.commentPrefix = Objects.requireNonNullElse(prefix, "");
        this.commentSuffix = Objects.requireNonNullElse(suffix, "");
        return this;
    }

    public OutputFormatBuilder withEscape(char meta, String replacement) {
        escapes.put(meta, replacement);
        return this;
    }

    public OutputFormat build() {
        if (escapes.isEmpty()) {
            return new OutputFormat() {
                @Override
                public TemplateString comment(TemplateString value) {
                    return new TemplateString(commentPrefix + value.getValue() + commentSuffix);
                }
            };
        }
        return new EscapingOutputFormat(escapes, commentPrefix, commentSuffix);
    }

    private record EscapingOutputFormat(Map<Character, String> escapes,String commentPrefix, String commentSuffix) implements OutputFormat {

        @Override
            public TemplateString escape(TemplateString value) {
                StringBuilder builder = new StringBuilder();
                for (char c : value.getValue().toCharArray()) {
                    String replacement = escapes.get(c);
                    if (replacement == null) {
                        builder.append(c);
                    } else {
                        builder.append(replacement);
                    }
                }
                return new TemplateString(builder.toString());
            }

            @Override
            public TemplateString comment(TemplateString value) {
                return new TemplateString(commentPrefix + value.getValue() + commentSuffix);
            }
        }
}
