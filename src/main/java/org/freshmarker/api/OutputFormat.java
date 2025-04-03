package org.freshmarker.api;

import org.freshmarker.core.model.primitive.TemplateString;

public interface OutputFormat {
    default TemplateString escape(TemplateString value) {
        return value;
    }

    default TemplateString comment(TemplateString value) {
        return TemplateString.EMPTY;
    }
}
