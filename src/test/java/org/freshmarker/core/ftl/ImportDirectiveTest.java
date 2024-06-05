package org.freshmarker.core.ftl;

import com.google.common.jimfs.Jimfs;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImportDirectiveTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        configuration.setFileSystem(Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix()));
    }

    @Test
    void invalidImport() throws IOException {
        Files.writeString(configuration.getFileSystem().getPath("invalid.ftm"), "<#var test=42>");
        ParsingException exception = assertThrows(ParsingException.class,
                () -> configuration.getTemplate("template", "<#import 'invalid.ftm' as i>"));
        assertEquals("unsupported import operation at i:1:1 '<#var test=42>'", exception.getMessage());
    }

    @Test
    void notFoundImport() {
        ParsingException exception = assertThrows(ParsingException.class,
                () -> configuration.getTemplate("template", "<#import 'invalid.ftm' as i>"));
        assertEquals("cannot read import: invalid.ftm at template:1:1 '<#import 'invalid.ftm' as i>'", exception.getMessage());
    }

    @Test
    void macroImport() throws IOException {
        Files.writeString(configuration.getFileSystem().getPath("macro.ftm"), "<#macro test>ABC<#return/>DEF</#macro>");
        Template template = configuration.getTemplate("template", "<#import 'macro.ftm' as m><@m.test/>");
        assertEquals("ABC", template.process(Map.of()));
    }
}
