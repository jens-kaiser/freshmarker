package org.freshmarker.core;

import de.schegge.leitweg.LeitwegId;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleToStringMapperTest {
    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
        configuration.registerSimpleMapping(LeitwegId.class);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Leitweg-Id: 04011000-1234512345-06,04011000-1234512345-06",
            "Leitweg-Id: 05711-06001-79,05711-06001-79",
    })
    void parseError(String expected, String leitwegId) {
        Template template = configuration.getTemplate("test", "Leitweg-Id: ${id}");
        assertEquals(expected, template.process(Map.of("id", LeitwegId.parse(leitwegId))));
    }
}
