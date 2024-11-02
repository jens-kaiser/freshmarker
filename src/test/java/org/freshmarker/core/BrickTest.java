package org.freshmarker.core;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BrickTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @Test
    void alreadyExisting() {
        assertThrows(IllegalArgumentException.class, () -> configuration.builder().getTemplate("test", """
                <#brick 'test'>Test</#brick>
                <#brick 'test'>Test</#brick>
                """));
    }

    @Test
    void unknownBrick() {
        Template template = configuration.builder().getTemplate("test", "test");
        assertThrows(ProcessException.class, () -> template.processBrick("subject", Map.of()));
    }

    @Test
    void emailBricks() throws ParseException {
        Template template = configuration.builder().getTemplate("test", """
                <#-- Dies ist eine Test Email -->
                <#brick 'subject'>My husband, ${husband}, has passed away</#brick>
                <#brick 'body'>
                Dear ${boss},
                
                I am writing to inform you with great sorrow that my husband, ${husband}, has passed away.
                This is a difficult time for our family, and I wanted to ensure you were aware of his passing.
                
                Sincerely,
                ${wife}
                </#brick>
                """);

        Map<String, Object> model = Map.of("boss", "Mr. Howard Wagner", "husband", "Willy Loman", "wife", "Linda Loman");

        assertEquals("""
                My husband, Willy Loman, has passed away
                Dear Mr. Howard Wagner,
                
                I am writing to inform you with great sorrow that my husband, Willy Loman, has passed away.
                This is a difficult time for our family, and I wanted to ensure you were aware of his passing.
                
                Sincerely,
                Linda Loman
                """, template.process(model));

        assertEquals("My husband, Willy Loman, has passed away", template.processBrick("subject", model));
        assertEquals("""
                Dear Mr. Howard Wagner,
                
                I am writing to inform you with great sorrow that my husband, Willy Loman, has passed away.
                This is a difficult time for our family, and I wanted to ensure you were aware of his passing.
                
                Sincerely,
                Linda Loman
                """, template.processBrick("body", model));
    }
}
