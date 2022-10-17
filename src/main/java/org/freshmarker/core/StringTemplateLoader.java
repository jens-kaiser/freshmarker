package org.freshmarker.core;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StringTemplateLoader implements TemplateLoader {

  private final Map<String, TemplateSource> cache = new HashMap<>();

  @Override
  public Optional<TemplateSource> getTemplate(String name) {
    return Optional.ofNullable(cache.get(name));
  }

  public void putTemplate(String name, String content) {
    cache.put(name, new StringTemplateSource(name, content));
  }

  private record StringTemplateSource(String name, String content) implements TemplateSource {

    @Override
      public Reader getReader(Charset encoding) {
        return new StringReader(content);
      }
    }
}
