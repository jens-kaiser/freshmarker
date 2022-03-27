package org.freshmarker.core.model.file;

import java.io.File;
import java.nio.file.Path;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class TemplatePath extends TemplatePrimitive<Path> {

  public TemplatePath(Path value) {
    super(value);
  }
}
