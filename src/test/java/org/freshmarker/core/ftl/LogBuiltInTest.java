package org.freshmarker.core.ftl;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class LogBuiltInTest {
    ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger)LoggerFactory.getLogger("builtin.logging");
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void logNull(TemplateBuilder builder) {
        Template template = builder.getTemplate("empty", "${value?log!'-'}");
        assertEquals("-",  template.process(Map.of()));
        assertEquals(1, listAppender.list.size());
        assertArrayEquals(new Object[] { "empty:1:3 'value'", TemplateNull.NULL }, listAppender.list.getFirst().getArgumentArray());
    }

    @Test
    void logVariable(TemplateBuilder builder) {
        Template template = builder.getTemplate("empty", "${value?log!'-'}");
        assertEquals("VALUE",  template.process(Map.of("value", "VALUE")));
        assertEquals(1, listAppender.list.size());
        assertArrayEquals(new Object[] { "empty:1:3 'value'", new TemplateString("VALUE")}, listAppender.list.getFirst().getArgumentArray());
    }

    @Test
    void logBuiltIn(TemplateBuilder builder) {
        Template template = builder.getTemplate("empty", "${value?lower_case?log!'-'}");
        assertEquals("value",  template.process(Map.of("value", "VALUE")));
        assertEquals(1, listAppender.list.size());
        assertArrayEquals(new Object[] {"empty:1:3 'value?lower_case'", new TemplateString("value")}, listAppender.list.getFirst().getArgumentArray());
    }

    @Test
    void logBuiltInAndLog(TemplateBuilder builder) {
        Template template = builder.getTemplate("empty", "${value?log?kebab_case?log?upper_case?log!'-'}");
        assertEquals("VALUE-VALUE",  template.process(Map.of("value", "valueValue")));
        List<ILoggingEvent> list = listAppender.list;
        assertEquals(3, list.size());
        assertArrayEquals(new Object[] {"empty:1:3 'value'", new TemplateString("valueValue")}, list.getFirst().getArgumentArray());
        assertArrayEquals(new Object[] {"empty:1:3 'value?log?kebab_case'", new TemplateString("value-value")}, list.get(1).getArgumentArray());
        assertArrayEquals(new Object[] {"empty:1:3 'value?log?kebab_case?log?upper_case'", new TemplateString("VALUE-VALUE")}, list.get(2).getArgumentArray());
    }

    @Test
    void logIfDirective(TemplateBuilder builder) {
        Template template = builder.getTemplate("empty", "<#if value?log == 42>the answer</#if>");
        assertEquals("the answer",  template.process(Map.of("value", 42)));
        assertEquals(1, listAppender.list.size());
        assertArrayEquals(new Object[] {"empty:1:6 'value'", TemplateNumber.of(42)}, listAppender.list.getFirst().getArgumentArray());
    }
}
