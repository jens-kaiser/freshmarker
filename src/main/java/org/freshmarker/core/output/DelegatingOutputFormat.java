package org.freshmarker.core.output;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateString;

public class DelegatingOutputFormat implements OutputFormat {

    @Override
    public TemplateString escape(Environment environment, String value) {
        return environment.getOutputFormat().escape(environment, value);
    }

    @Override
    public TemplateString comment(Environment environment, String value) {
        return environment.getOutputFormat().comment(environment, value);
    }
}
