package org.freshmarker.core;

import java.io.IOException;

public class TemplateNotFoundException extends IOException {

  public TemplateNotFoundException(String message) {
    super(message);
  }
}
