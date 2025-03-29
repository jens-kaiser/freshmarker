package org.freshmarker.core.extension;

import org.freshmarker.Configuration;
import org.freshmarker.api.NamedUserDirective;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NamedUserDirectiveTest {
    @Test
    void registerUserDirective() {
        Configuration configuration = new Configuration();
        configuration.register(new NamedUserDirective() {
            @Override
            public String name() {
                return "test";
            }

            @Override
            public void execute(ProcessContext context, Map<String, TemplateObject> args, Fragment body) {
                body.process(context);
                body.process(context);
            }
        });
        assertEquals("testtest", configuration.builder().getTemplate("test", "<@test>test</@test>").process(Map.of()));
    }
}
