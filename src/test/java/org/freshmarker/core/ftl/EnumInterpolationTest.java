package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumInterpolationTest {
    private Configuration configuration;

    private enum TestEnum {
        ALPHA("Α"),
        BETA("Β"),
        GAMMA("Γ"),
        DELTA("Δ");

        private final String name;

        TestEnum(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${test},test: CREATE",
            "test: ${test?c},test: CREATE",
            "test: ${test?ordinal},test: 4",
    })
    void interpolationExpression(String templateSource, String expected) throws ParseException {
        Template template = configuration.builder().withLocale(Locale.GERMANY).getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("test", StandardOpenOption.CREATE)));
    }

    @ParameterizedTest
    @CsvSource({
            "test: ${test},ALPHA,test: Α",
            "test: ${test?c},ALPHA,test: ALPHA",
            "test: ${test?ordinal},ALPHA,test: 0",
            "test: ${test},BETA,test: Β",
            "test: ${test?c},BETA,test: BETA",
            "test: ${test?ordinal},BETA,test: 1",
            "test: ${test},GAMMA,test: Γ",
            "test: ${test?c},GAMMA,test: GAMMA",
            "test: ${test?ordinal},GAMMA,test: 2",
            "test: ${test},DELTA,test: Δ",
            "test: ${test?c},DELTA,test: DELTA",
            "test: ${test?ordinal},DELTA,test: 3",
    })
    void interpolationExpressionWithCustomToString(String templateSource, TestEnum value, String expected) throws ParseException {
        Template template = configuration.builder().withLocale(Locale.GERMANY).getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("test", value)));
    }
}