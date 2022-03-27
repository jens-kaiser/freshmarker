package org.freshmarker.core;

import java.util.Optional;

public interface TemplateLoader {

  Optional<TemplateSource> getTemplate(String name);
}
