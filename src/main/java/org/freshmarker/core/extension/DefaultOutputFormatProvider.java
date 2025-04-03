package org.freshmarker.core.extension;

import org.freshmarker.api.OutputFormat;
import org.freshmarker.api.extension.OutputFormatProvider;
import org.freshmarker.core.output.StandardOutputFormats;

import java.util.HashMap;
import java.util.Map;

public class DefaultOutputFormatProvider implements OutputFormatProvider {
    @Override
    public Map<String, OutputFormat> provideOutputFormats() {
        Map<String, OutputFormat> outputs = new HashMap<>();
        outputs.put("HTML", StandardOutputFormats.HTML);
        outputs.put("XHTML", StandardOutputFormats.HTML);
        outputs.put("XML", StandardOutputFormats.XML);
        outputs.put("plainText", StandardOutputFormats.NONE);
        outputs.put("JavaScript", StandardOutputFormats.JAVASCRIPT);
        outputs.put("JSON", StandardOutputFormats.NONE);
        outputs.put("CSS", StandardOutputFormats.CSS);
        outputs.put("ADOC", StandardOutputFormats.ADOC);
        return outputs;
    }
}
