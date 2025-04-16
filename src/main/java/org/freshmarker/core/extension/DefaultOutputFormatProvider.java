package org.freshmarker.core.extension;

import org.freshmarker.api.OutputFormat;
import org.freshmarker.api.extension.OutputFormatProvider;
import org.freshmarker.core.output.StandardOutputFormats;

import java.util.Map;

public class DefaultOutputFormatProvider implements OutputFormatProvider {
    @Override
    public Map<String, OutputFormat> provideOutputFormats() {
        return Map.of(
                "HTML", StandardOutputFormats.HTML, "XHTML", StandardOutputFormats.HTML,"XML", StandardOutputFormats.XML,
                "plainText", StandardOutputFormats.NONE, "JavaScript", StandardOutputFormats.JAVASCRIPT, "JSON", StandardOutputFormats.NONE,
                "CSS", StandardOutputFormats.CSS, "ADOC", StandardOutputFormats.ADOC);
    }
}
