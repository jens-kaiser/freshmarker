package org.freshmarker.core.extension;

import org.freshmarker.Configuration;
import org.freshmarker.api.extension.UserDirectiveProvider;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDirectiveProviderTest {
    @Test
    void registerUserDirective() {
        Configuration configuration = new Configuration();
        configuration.register((UserDirectiveProvider) () -> Map.of("test", (context, args, body) -> {
            body.process(context);
            body.process(context);
        }));
        assertEquals("testtest", configuration.builder().getTemplate("test", "<@test>test</@test>").process(Map.of()));
    }
}
