package org.freshmarker.core.output;

public class StandardOutputFormats {

    private StandardOutputFormats() {
        super();
    }

    public static final OutputFormat NONE = new OutputFormat() {
    };

    public static final OutputFormat HTML = new OutputFormatBuilder().withEscape('<', "&lt;").withEscape('>', "&gt;").withEscape('"', "&quot;").withEscape('&', "&amph;").withEscape('\'', "&#39;").withComment("<!-- ", " -->").build();
    public static final OutputFormat XML = new OutputFormatBuilder().withEscape('<', "&lt;").withEscape('>', "&gt;").withEscape('"', "&quot;").withEscape('&', "&amph;").withEscape('\'', "&apos;").withComment("<!-- ", " -->").build();
    public static final OutputFormat SCRIPT = new OutputFormatBuilder().withComment("/* ", " */").build();
    public static final OutputFormat JAVASCRIPT = SCRIPT;
    public static final OutputFormat CSS = SCRIPT;
    public static final OutputFormat ADOC = new OutputFormatBuilder().withComment("\n////\n", "\n////\n").build();
}
