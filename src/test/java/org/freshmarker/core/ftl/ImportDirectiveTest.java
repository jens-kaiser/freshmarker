package org.freshmarker.core.ftl;

import com.google.common.jimfs.Jimfs;
import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.FileSystemTemplateLoader;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImportDirectiveTest {
    private Configuration configuration;
    private FileSystem fileSystem;
    private TemplateBuilder builder;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        fileSystem = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix());
        configuration.setTemplateLoader(new FileSystemTemplateLoader(fileSystem));
        builder = configuration.builder();
    }

    @Test
    void invalidImport() throws IOException {
        Files.writeString(fileSystem.getPath("invalid.ftm"), "<#var test=42>");
        ParsingException exception = assertThrows(ParsingException.class,
                () -> builder.getTemplate("template", "<#import 'invalid.ftm' as i>"));
        assertEquals("unsupported import operation at i:1:1 '<#var test=42>'", exception.getMessage());
    }

    @Test
    void notFoundImport() {
        ParsingException exception = assertThrows(ParsingException.class,
                () -> builder.getTemplate("template", "<#import 'invalid.ftm' as i>"));
        assertEquals("cannot read import: invalid.ftm at template:1:1 '<#import 'invalid.ftm' as i>'", exception.getMessage());
    }

    @Test
    void macroImport() throws IOException {
        Files.writeString(fileSystem.getPath("macro.ftm"), "<#macro test>ABC<#return/>DEF</#macro>");
        Template template = builder.getTemplate("template", "<#import 'macro.ftm' as m><@m.test/>");
        assertEquals("ABC", template.process(Map.of()));
    }

    @Test
    void macroImportFromZip() throws IOException {
        configuration.setTemplateLoader(new FileSystemTemplateLoader(FileSystems.newFileSystem(Path.of("src/test/resources/macros.zip"))));
        Template template = configuration.builder().getTemplate("template", "<#import 'macros.fmt' as m><@m.test/>");
        assertEquals("ABC", template.process(Map.of()));
    }
}
