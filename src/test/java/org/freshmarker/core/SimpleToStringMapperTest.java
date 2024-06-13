package org.freshmarker.core;

import de.schegge.leitweg.LeitwegId;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleToStringMapperTest {
    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Leitweg-Id: 04011000-1234512345-06,04011000-1234512345-06",
            "Leitweg-Id: 05711-06001-79,05711-06001-79",
    })
    void renderLeitwegIdAsString(String expected, String leitwegId) {
        configuration.registerSimpleMapping(LeitwegId.class);
        Template template = configuration.getTemplate("test", "Leitweg-Id: ${id}");
        assertEquals(expected, template.process(Map.of("id", LeitwegId.parse(leitwegId))));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Leitweg-Id: <<04011000-1234512345-06>>,04011000-1234512345-06",
            "Leitweg-Id: <<05711-06001-79>>,05711-06001-79",
    })
    void renderLeitwegIdAsStringWithExplizitMapper(String expected, String leitwegId) {
        configuration.registerSimpleMapping(LeitwegId.class, x -> "<<" + x.toString() + ">>");
        Template template = configuration.getTemplate("test", "Leitweg-Id: ${id}");
        assertEquals(expected, template.process(Map.of("id", LeitwegId.parse(leitwegId))));
    }

        @ParameterizedTest
    @CsvSource(value = {
            "https://schegge.de,${url}",
            "https://schegge.de,${uri}",
            "40ba6fe6-3032-490c-b3e4-84c0961202e5,${uuid}",
            "builder,${builder}",
            "buffer,${buffer}",
    })
    void renderDefaultSimpleToStringMapper(String expected, String input) throws MalformedURLException {
        Template template = configuration.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of(
                "uri", URI.create("https://schegge.de"),
                "url", URI.create("https://schegge.de").toURL(),
                "uuid", UUID.fromString("40ba6fe6-3032-490c-b3e4-84c0961202e5"),
                "builder", new StringBuilder("builder"),
                "buffer", new StringBuffer("buffer")
        )));
    }
}
