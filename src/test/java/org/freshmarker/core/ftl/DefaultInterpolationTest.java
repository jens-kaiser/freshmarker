package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultInterpolationTest {
    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${test1!'empty'},test: empty",
            "test: ${test2!'empty'},test: test",
            "test: ${test3!42},test: 42",
            "test: ${test4!},'test: '",
    })
    void exists(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("test2", "test")));
    }
}