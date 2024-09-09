package org.freshmarker.core.output;

public class StandardOutputFormats {
    private StandardOutputFormats() {
        super();
    }

    public final static OutputFormat NONE = new OutputFormat() {
    };

    public final static OutputFormat HTML = new OutputFormatBuilder().withEscape('<', "&lt;").withEscape('>', "&gt;").withEscape('"', "&quot;").withEscape('&', "&amph;").withEscape('\'', "&#39;").withComment("<!-- ", " -->").build();
    public final static OutputFormat XML = new OutputFormatBuilder().withEscape('<', "&lt;").withEscape('>', "&gt;").withEscape('"', "&quot;").withEscape('&', "&amph;").withEscape('\'', "&apos;").withComment("<!-- ", " -->").build();
    public final static OutputFormat SCRIPT = new OutputFormatBuilder().withComment("/* ", " */").build();
    public final static OutputFormat CSS = new OutputFormatBuilder().withComment("/* ", " */").build();
    public final static OutputFormat ADOC = new OutputFormatBuilder().withComment("\n////\n", "\n////\n").build();
}
