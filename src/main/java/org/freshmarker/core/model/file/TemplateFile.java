package org.freshmarker.core.model.file;

import java.io.File;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplateFile extends TemplatePrimitive<File> {

  public TemplateFile(File value) {
    super(value);
  }
}
