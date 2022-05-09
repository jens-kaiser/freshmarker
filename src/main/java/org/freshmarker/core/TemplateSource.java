package org.freshmarker.core;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

public interface TemplateSource extends Closeable {

  Reader getReader( Charset charset);

  default void close() throws IOException {

  }

  String getName();
}
