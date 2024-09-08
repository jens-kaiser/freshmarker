package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.test.util.EnabledIfMavenBuild;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuiltInVariableTestIT {

    @Test
    @EnabledIfMavenBuild
    void builtInVariables() throws ParseException {
        Configuration configuration = new Configuration();
        Template template = configuration.builder().getTemplate("test", "test: ${.version}; ${.version?major}-${.version?minor}-${.version?patch}");
        assertEquals("test: 1.4.3, 1-4-3", template.process(Map.of()));
    }
}