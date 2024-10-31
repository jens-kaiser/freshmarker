package org.freshmarker.core;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BrickTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
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

        assertEquals("My husband, Willy Loman, has passed away", template.process("subject", model));
        assertEquals("""
                Dear Mr. Howard Wagner,
                
                I am writing to inform you with great sorrow that my husband, Willy Loman, has passed away.
                This is a difficult time for our family, and I wanted to ensure you were aware of his passing.
                
                Sincerely,
                Linda Loman
                """, template.process("body", model));
    }
}
