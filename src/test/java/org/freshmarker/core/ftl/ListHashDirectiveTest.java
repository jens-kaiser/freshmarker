package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListHashDirectiveTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @Test
    void output() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${sequence}");
        Map<String, Object> model = Map.of("sequence", Map.of("a", 1, "b", 2, "c", 3, "d", 4));
        assertThrows(ProcessException.class, () -> template.process(model));
    }

    @Test
    void loopIndex() throws ParseException {
        Template template = configuration.getTemplate("test",
                "test: <#list sequence as k sorted asc, v with l>${l?index}. ${k} ${v}\n</#list>");
        Map<String, Object> model = Map.of("sequence", Map.of("a", 1, "b", 2, "c", 3, "d", 4));
        assertEquals("test: 0. a 1\n1. b 2\n2. c 3\n3. d 4\n", template.process(model));
    }

    @Test
    void loopRoman() throws ParseException {
        Template template = configuration.getTemplate("test",
                "test: <#list sequence as k sorted asc, v with l>${l?roman}. ${k} ${v}\n</#list>");
        Map<String, Object> model = Map.of("sequence", Map.of("a", 1, "b", 2, "c", 3, "d", 4));
        assertEquals("test: I. a 1\nII. b 2\nIII. c 3\nIV. d 4\n", template.process(model));
    }

    @Test
    void emptyList() throws ParseException {
        Template template = configuration.getTemplate("test",
                "test:\n<#list sequence as k, v with l>\n${l?index}. ${k} ${v}\n</#list>");
        assertEquals("test:\n", template.process(Map.of("sequence", Map.of())));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "${l?item_parity};test: odd even odd even ",
            "${l?item_parity_cap};test: Odd Even Odd Even ",
            "${l?item_cycle(1, 2, 3)};test: 1 2 3 1 ",
            "${l?is_first};test: yes no no no ",
            "${l?is_last};test: no no no yes ",
            "${l?has_next};test: yes yes yes no "
    }, ignoreLeadingAndTrailingWhitespace = false, delimiterString = ";")
    void looperBuildIns(String interpolation, String expected) throws ParseException {
        Template template = configuration.getTemplate("test",
                "test: <#list sequence as k, v with l>" + interpolation + " </#list>");
        Map<String, Integer> sequence = Map.of("a", 1, "b", 2, "c", 3, "d", 4);
        assertEquals(expected, template.process(Map.of("sequence", sequence)));
    }

    public record Complex(String key, String value) {

    }

    @Test
    void sortedHashList() {
        Template template = configuration.getTemplate("test", "<#list hash as k sorted asc, v>${k} ${v},</#list>");
        Map<String, String> map = Map.of("c", "C", "b", "B", "a", "A");
        assertEquals("a A,b B,c C,", template.process(Map.of("hash", map)));
    }

    @Test
    void sortedDescendingHashList() {
        Template template = configuration.getTemplate("test", "<#list hash as k sorted desc, v>${k} ${v},</#list>");
        Map<String, String> map = Map.of("a", "A", "b", "B", "c", "C");
        assertEquals("c C,b B,a A,", template.process(Map.of("hash", map)));
    }

    public static class HashBean {
        final String d;
        final String a;
        final String c;
        final String b;

        public HashBean(String a, String b, String c, String d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public String getD() {
            return d;
        }

        public String getA() {
            return a;
        }

        public String getC() {
            return c;
        }

        public String getB() {
            return b;
        }
    }

    @Test
    void sortedDescendingBeanHashList() {
        Template template = configuration.getTemplate("test", """
                <#list hash as k, v with l>${k} ${v}<#if l?has_next>,</#if></#list>
                <#list hash as k sorted desc, v with l>${k} ${v}<#if l?has_next>,</#if></#list>
                """);
        HashBean bean = new HashBean("1", "2", "3", "4");
        assertEquals("""
                a 1,b 2,c 3,d 4
                d 4,c 3,b 2,a 1
                """, template.process(Map.of("hash", bean)));
    }

    @Test
    void sortedRecordHashList() {
        Template template = configuration.getTemplate("test", "<#list hash as k sorted asc, v>${k} ${v},</#list>");
        HashRecord recordHash = new HashRecord("1", "2", "3");
        assertEquals("a 2,b 3,c 1,", template.process(Map.of("hash", recordHash)));
    }

    public record HashRecord(String c, String a, String b) {
    }

    @Test
    void sortedDescendingRecordHashList() {
        Template template = configuration.getTemplate("test", "<#list hash as k sorted desc, v>${k} ${v},</#list>");
        HashRecord recordHash = new HashRecord("1", "2", "3");
        assertEquals("c 1,b 3,a 2,", template.process(Map.of("hash", recordHash)));
    }

    @Test
    void hashListWithLooper() {
        Template template = configuration.getTemplate("test", "<#list hash as k, v with l>${l?counter} ${k} ${v},</#list>");
        Map<String, String> map = Stream.of("a", "b", "c").collect(Collectors.toMap(Function.identity(), String::toUpperCase, (a, b) -> a, LinkedHashMap::new));
        assertEquals("1 a A,2 b B,3 c C,", template.process(Map.of("hash", map)));
    }
}