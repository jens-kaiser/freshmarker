package org.freshmarker.core.output;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateString;

public interface OutputFormat {

  default TemplateString escape(Environment environment, String value) {
    return new TemplateString(value);
  }

  default TemplateString comment(Environment environment, String value) {
    return new TemplateString("");
  }

}
