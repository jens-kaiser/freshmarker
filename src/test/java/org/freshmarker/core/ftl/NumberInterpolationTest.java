package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberInterpolationTest {
    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42};test: 42",
            "test: ${42.23};test: 42,23",
            "test: ${(-42)?abs};test: 42",
            "test: ${42?abs};test: 42",
            "test: ${(-0)?abs};test: 0",
            "test: ${0?abs};test: 0",
            "test: ${42?sign};test: 1",
            "test: ${(-42)?sign};test: -1",
            "test: ${(-0)?sign};test: 0",
            "test: ${0?sign};test: 0",
    }, delimiterString = ";")
    void interpolationConstant(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42*x};test: 1.764",
            "test: ${42.0*x};test: 1.764",
            "test: ${42*10-0.5};test: 419,5",
            "test: ${42.23*10};test: 422,3",
            "test: ${x*x};test: -28",
            "test: ${y*y};test: 1.764",
            "test: ${x % 4};test: 2",
            "test: ${-x};test: -42",
            "test: ${+x};test: 42",
    }, delimiterString = ";")
    void interpolationByteExpression(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", (byte) 42, "y", 42)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${42*x};test: 1.764",
            "test: ${42.0*x};test: 1.764",
            "test: ${42*10-0.5};test: 419,5",
            "test: ${42.23*10};test: 422,3",
            "test: ${x*x};test: 1.764",
            "test: ${y*y};test: 1.764",
            "test: ${x % 4};test: 2",
            "test: ${-x};test: -42",
            "test: ${+x};test: 42",
    }, delimiterString = ";")
    void interpolationShortExpression(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("x", (short) 42, "y", 42)));
    }
}