package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileAndPathInterpolationTest {
    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${path},test: src\\main\\ccc\\FreshMarker\\FEL.ccc",
            "test: ${path?name},test: FEL.ccc",
            "test: ${path?parent},test: src\\main\\ccc\\FreshMarker",
            "test: ${path?size > 0},test: yes",
            "test: ${path?exists},test: yes",
            "test: ${path?is_file},test: yes",
            "test: ${path?is_directory},test: no",
            "test: ${path?parent?is_directory},test: yes",
            "test: ${path?parent?is_file},test: no",
            "test: ${path?can_read},test: yes",
            "test: ${path?can_write},test: yes",
            //"test: ${path?can_execute},test: yes",
            "test: ${missingPath},test: src\\main\\ccc\\FreshMarker\\FEL2.ccc",
            "test: ${missingPath?name},test: FEL2.ccc",
            "test: ${missingPath?parent},test: src\\main\\ccc\\FreshMarker",
            "test: ${missingPath?size},test: 0",
            "test: ${missingPath?exists},test: no",
            "test: ${missingPath?is_file},test: no",
            "test: ${missingPath?is_directory},test: no",
            "test: ${missingPath?parent?is_directory},test: yes",
            "test: ${missingPath?parent?is_file},test: no",
            "test: ${file},test: src\\main\\ccc\\FreshMarker\\FEL.ccc",
            "test: ${file?name},test: FEL.ccc",
            "test: ${file?parent},test: src\\main\\ccc\\FreshMarker",
            "test: ${file?size > 0},test: yes",
            "test: ${file?exists},test: yes",
            "test: ${file?is_file},test: yes",
            "test: ${file?is_directory},test: no",
            "test: ${file?parent?is_directory},test: yes",
            "test: ${file?parent?is_file},test: no",
            "test: ${file?can_read},test: yes",
            "test: ${file?can_write},test: yes",
            //"test: ${file?can_execute},test: yes",
            "test: ${missingFile},test: src\\main\\ccc\\FreshMarker\\FEL2.ccc",
            "test: ${missingFile?name},test: FEL2.ccc",
            "test: ${missingFile?parent},test: src\\main\\ccc\\FreshMarker",
            "test: ${missingFile?size},test: 0",
            "test: ${missingFile?exists},test: no",
            "test: ${missingFile?is_file},test: no",
            "test: ${missingFile?is_directory},test: no",
            "test: ${missingFile?parent?is_directory},test: yes",
            "test: ${missingFile?parent?is_file},test: no",
    })
    void interpolationConstant(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        Map<String, Object> dataModel = Map.of(
                "path", Path.of("src/main/ccc/FreshMarker/FEL.ccc"),
                "missingPath", Path.of("src/main/ccc/FreshMarker/FEL2.ccc"),
                "file", Path.of("src/main/ccc/FreshMarker/FEL.ccc").toFile(),
                "missingFile", Path.of("src/main/ccc/FreshMarker/FEL2.ccc").toFile()
        );
        assertEquals(expected, template.process(dataModel).replace('/', '\\'));
    }
}