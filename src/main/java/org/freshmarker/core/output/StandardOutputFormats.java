package org.freshmarker.core.output;

import org.freshmarker.api.OutputFormat;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.Map;

public class StandardOutputFormats {

    private StandardOutputFormats() {
        super();
    }

    public static final OutputFormat NONE = new OutputFormat() {
    };

    private record EscapingOutputFormat(Map<Character, String> escapes, String commentPrefix, String commentSuffix) implements OutputFormat {

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

    private record NonEscapingOutputFormat(String commentPrefix, String commentSuffix) implements OutputFormat {

        @Override
        public TemplateString comment(TemplateString value) {
            return new TemplateString(commentPrefix + value.getValue() + commentSuffix);
        }
    }

    private static final Map<Character, String> htmlEscapes = Map.of('<', "&lt;", '>', "&gt;", '"', "&quot;", '&', "&amph;", '\'', "&#39;");
    private static final Map<Character, String> xmlEscapes = Map.of('<', "&lt;", '>', "&gt;", '"', "&quot;", '&', "&amph;", '\'', "&apos;");

    public static final OutputFormat HTML = new EscapingOutputFormat(htmlEscapes, "<!-- ", " -->");
    public static final OutputFormat XML = new EscapingOutputFormat(xmlEscapes, "<!-- ", " -->");
    public static final OutputFormat SCRIPT = new NonEscapingOutputFormat("/* ", " */");
    public static final OutputFormat JAVASCRIPT = SCRIPT;
    public static final OutputFormat CSS = SCRIPT;
    public static final OutputFormat ADOC = new NonEscapingOutputFormat("\n////\n", "\n////\n");
}
