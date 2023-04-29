package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import org.freshmarker.core.StringTemplateLoader;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FileAndPathInterpolationTest {
  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    configuration.setLocale(Locale.GERMANY);
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @ParameterizedTest
  @CsvSource({
      "test: ${path},test: src\\main\\resources\\javacc\\FEL.javacc",
      "test: ${path?name},test: FEL.javacc",
      "test: ${path?parent},test: src\\main\\resources\\javacc",
      "test: ${path?size > 0},test: yes",
      "test: ${path?exists},test: yes",
      "test: ${path?is_file},test: yes",
      "test: ${path?is_directory},test: no",
      "test: ${path?parent?is_directory},test: yes",
      "test: ${path?parent?is_file},test: no",
      "test: ${missingPath},test: src\\main\\resources\\javacc\\FEL2.javacc",
      "test: ${missingPath?name},test: FEL2.javacc",
      "test: ${missingPath?parent},test: src\\main\\resources\\javacc",
      "test: ${missingPath?size},test: 0",
      "test: ${missingPath?exists},test: no",
      "test: ${missingPath?is_file},test: no",
      "test: ${missingPath?is_directory},test: no",
      "test: ${missingPath?parent?is_directory},test: yes",
      "test: ${missingPath?parent?is_file},test: no",
      "test: ${file},test: src\\main\\resources\\javacc\\FEL.javacc",
      "test: ${file?name},test: FEL.javacc",
      "test: ${file?parent},test: src\\main\\resources\\javacc",
      "test: ${file?size > 0},test: yes",
      "test: ${file?exists},test: yes",
      "test: ${file?is_file},test: yes",
      "test: ${file?is_directory},test: no",
      "test: ${file?parent?is_directory},test: yes",
      "test: ${file?parent?is_file},test: no",
      "test: ${missingFile},test: src\\main\\resources\\javacc\\FEL2.javacc",
      "test: ${missingFile?name},test: FEL2.javacc",
      "test: ${missingFile?parent},test: src\\main\\resources\\javacc",
      "test: ${missingFile?size},test: 0",
      "test: ${missingFile?exists},test: no",
      "test: ${missingFile?is_file},test: no",
      "test: ${missingFile?is_directory},test: no",
      "test: ${missingFile?parent?is_directory},test: yes",
      "test: ${missingFile?parent?is_file},test: no",
  })
  void interpolationConstant(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    Template template = configuration.getTemplate("test");
    Map<String, Object> dataModel = Map.of(
        "path", Path.of("src/main/resources/javacc/FEL.javacc"),
        "missingPath", Path.of("src/main/resources/javacc/FEL2.javacc"),
        "file", Path.of("src/main/resources/javacc/FEL.javacc").toFile(),
        "missingFile", Path.of("src/main/resources/javacc/FEL2.javacc").toFile()
    );
    assertEquals(expected, template.process(dataModel).replace('/', '\\'));
  }
}