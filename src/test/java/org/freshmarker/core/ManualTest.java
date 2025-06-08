package org.freshmarker.core;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManualTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @Test
    void range() throws ParseException {
        Template template = configuration.builder().getTemplate("test", """
                <#list 1..5 as s>
                ${s} * ${s} = ${s*s}
                </#list>
                """);
        assertEquals("""
                1 * 1 = 1
                2 * 2 = 4
                3 * 3 = 9
                4 * 4 = 16
                5 * 5 = 25
                """, template.process(Map.of("a", 3, "b", 1)));
    }

    @Test
    void variableRange() throws ParseException {
        Template template = configuration.builder().getTemplate("test", """
                <#list a..b as s>
                ${s} * ${s} = ${s*s}
                </#list>
                """);
        assertEquals("""
                3 * 3 = 9
                2 * 2 = 4
                1 * 1 = 1
                """, template.process(Map.of("a", 3, "b", 1)));
    }

    @Test
    void reduce1() {
        Template template = configuration.builder().with(ReductionFeature.MERGE_CONSTANT_FRAGMENTS).getTemplate("test", """
                Name:   ${firstname} ${lastname}
                <#if email??>
                E-Mail: ${email?lower_case}
                <#else>
                E-Mail: ●●●
                </#if>
                Company: ${company}
                """);
        Template reduced = template.reduce(Map.of("company", "ACME", "email",  "Wile.E.Coyote@acme.com"));
        assertEquals("""
                Name:   \s
                E-Mail: wile.e.coyote@acme.com
                Company: ACME
                """, reduced.process(Map.of("firstname", "", "lastname", "")));
    }

    @Test
    void reduce2() {
        Template template = configuration.builder().with(ReductionFeature.UNROLL_LIST).with(ReductionFeature.MERGE_CONSTANT_FRAGMENTS).getTemplate("test", """
                ${''?right_pad(32, '•●⬤●')}
                <#list employees as e with l>
                Name:   ${e.firstname} ${e.lastname}
                <#if e.email??>
                E-Mail: ${e.email?lower_case}
                <#else>
                E-Mail: ●●●
                </#if>
                Company: ${company}
                <#if l?has_next>
                ${''?right_pad(32, '•●')}
                </#if>
                </#list>
                ${''?right_pad(32, '•●⬤●')}
                """);
        List<Map<String, String>> employees = List.of(Map.of("email", "Wile.E.Coyote@acme.com"), Map.of("email", "Elmar.J.Fudd@acme.com"));
        Map<String, Object> dataModel = Map.of("company", "ACME", "employees", employees);
        Template reduced = template.reduce(dataModel);
        List<Map<String, String>> employeesNames = List.of(
                Map.of("firstname", "Wile E.", "lastname", "Coyote"),
                Map.of("firstname", "Elmar J.", "lastname", "Fudd"));
        String content = reduced.process(Map.of("employees", employeesNames));
        assertEquals("""
                •●⬤●•●⬤●•●⬤●•●⬤●•●⬤●•●⬤●•●⬤●•●⬤●
                Name:   Wile E. Coyote
                E-Mail: wile.e.coyote@acme.com
                Company: ACME
                •●•●•●•●•●•●•●•●•●•●•●•●•●•●•●•●
                Name:   Elmar J. Fudd
                E-Mail: elmar.j.fudd@acme.com
                Company: ACME
                •●⬤●•●⬤●•●⬤●•●⬤●•●⬤●•●⬤●•●⬤●•●⬤●
                """, content);
    }

    @Test
    void reduce3() {
        Template template = configuration.builder().with(ReductionFeature.UNROLL_LIST).with(ReductionFeature.MERGE_CONSTANT_FRAGMENTS).getTemplate("test", """
                <#list bean as key sorted desc, value>
                ${key?upper_case} ${value?lower_case}
                </#list>
                """);
        Map<String, String> bean = Map.of("email", "Wile.E.Coyote@acme.com", "company", "ACME");
        Map<String, Object> dataModel = Map.of("bean", bean);
        Template reduced = template.reduce(dataModel);
        assertEquals("""
            EMAIL wile.e.coyote@acme.com
            COMPANY acme
            """,reduced.process(Map.of("bean", bean)));
    }
}
