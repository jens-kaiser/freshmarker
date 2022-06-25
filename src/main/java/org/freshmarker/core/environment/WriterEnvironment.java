package org.freshmarker.core.environment;

import java.io.Writer;
import org.freshmarker.core.Environment;

public class WriterEnvironment extends WrapperEnvironment {

  private final Writer writer;

  public WriterEnvironment(Writer writer, Environment environment) {
    super(environment);
    this.writer = writer;
  }

  @Override
  public Writer getWriter() {
    return writer;
  }
}
