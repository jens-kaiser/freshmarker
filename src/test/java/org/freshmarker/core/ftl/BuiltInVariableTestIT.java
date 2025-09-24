package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.EnabledIfMavenBuild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuiltInVariableTestIT {
    private final TemplateBuilder templateBuilder = new Configuration().builder();

    @Test
    @EnabledIfMavenBuild
    void builtInVariables() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${.version}; ${.version?major}-${.version?minor}-${.version?patch}");
        assertEquals("test: 1.4.3, 1-4-3", template.process(Map.of()));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    void systemEnvironment() {
        Template template = templateBuilder.getTemplate("test", "${.env['CI_PROJECT_VISIBILITY']}");
        assertEquals("public", template.process(Map.of()));
    }
}