package org.freshmarker.core.output;

import org.freshmarker.core.model.primitive.TemplateString;

public interface OutputFormat {

  TemplateString escape(String value);
}
