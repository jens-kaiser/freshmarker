package org.freshmarker.core;

import de.schegge.leitweg.LeitwegId;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.BitSet;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleToStringMapperTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Leitweg-Id: 04011000-1234512345-06,04011000-1234512345-06",
            "Leitweg-Id: 05711-06001-79,05711-06001-79",
    })
    void renderLeitwegIdAsString(String expected, String leitwegId) {
        configuration.registerSimpleMapping(LeitwegId.class);
        Template template = configuration.builder().withLocale(Locale.GERMANY).getTemplate("test", "Leitweg-Id: ${id}");
        assertEquals(expected, template.process(Map.of("id", LeitwegId.parse(leitwegId))));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Leitweg-Id: <<04011000-1234512345-06>>,04011000-1234512345-06",
            "Leitweg-Id: <<05711-06001-79>>,05711-06001-79",
    })
    void renderLeitwegIdAsStringWithExplicitMapper(String expected, String leitwegId) {
        configuration.registerSimpleMapping(LeitwegId.class, x -> "<<" + x.toString() + ">>");
        Template template = configuration.builder().withLocale(Locale.GERMANY).getTemplate("test", "Leitweg-Id: ${id}");
        assertEquals(expected, template.process(Map.of("id", LeitwegId.parse(leitwegId))));
    }

    @Test
    void renderBitSetAsStringWithExplicitMapper() {
        configuration.registerSimpleMapping(BitSet.class, x -> {
            BitSet bitSet = (BitSet)x;
            StringBuilder builder = new StringBuilder();
            for (int i = 0, n = bitSet.length(); i < n; i++) {
                builder.append(bitSet.get(i) ? "●\u2009" : "○\u2009");
            }
            return builder.toString();
        });
        Template template = configuration.builder().getTemplate("test", "BitSet: ${bitSet}");
        BitSet bitSet = new BitSet(10);
        IntStream.of(1,3,4,7,8).forEach(bitSet::set);
        assertEquals("BitSet: ○\u2009●\u2009○\u2009●\u2009●\u2009○\u2009○\u2009●\u2009●\u2009", template.process(Map.of("bitSet", bitSet)));
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
        Template template = configuration.builder().withLocale(Locale.GERMANY).getTemplate("test", input);
        assertEquals(expected, template.process(Map.of(
                "uri", URI.create("https://schegge.de"),
                "url", URI.create("https://schegge.de").toURL(),
                "uuid", UUID.fromString("40ba6fe6-3032-490c-b3e4-84c0961202e5"),
                "builder", new StringBuilder("builder"),
                "buffer", new StringBuffer("buffer")
        )));
    }
}
