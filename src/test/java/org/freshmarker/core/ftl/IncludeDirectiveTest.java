package org.freshmarker.core.ftl;

import com.google.common.jimfs.Jimfs;
import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.FileSystemTemplateLoader;
import org.freshmarker.Template;
import org.freshmarker.core.IncludeDirectiveFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.time.Year;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IncludeDirectiveTest {
    private FileSystem fileSystem;
    private TemplateBuilder builder;

    @BeforeEach
    void setUp() {
        Configuration configuration = new Configuration();
        fileSystem = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix());
        configuration.setTemplateLoader(new FileSystemTemplateLoader(fileSystem));
        builder = configuration.builder();
    }

    @Test
    void notEnabled() throws IOException {
        Files.writeString(fileSystem.getPath("copyright.fmi"), "Copyright 2022-${year} ${me}<br>\nAll rights reserved.");
        Template template = builder.getTemplate("template", "<#include 'copyright.fmi'>");
        assertEquals("", template.process(Map.of("me", "Jens Kaiser", "year", Year.now())));
    }

    @Test
    void notFoundInclude() {
        ParsingException exception = assertThrows(ParsingException.class,
                () -> builder.with(IncludeDirectiveFeature.ENABLED).getTemplate("template", "<#include 'invalid.fmi'>"));
        assertEquals("cannot read import: invalid.fmi at template:1:1 '<#include 'invalid.fmi'>'", exception.getMessage());
    }

    @Test
    void simpleInclude() throws IOException {
        Files.writeString(fileSystem.getPath("copyright.fmi"), "Copyright 2022-${year} ${me}<br>\nAll rights reserved.");
        Template template = builder.with(IncludeDirectiveFeature.ENABLED).getTemplate("template", "<#include 'copyright.fmi'>");
        assertEquals("Copyright 2022-2025 Jens Kaiser<br>\nAll rights reserved.", template.process(Map.of("me", "Jens Kaiser", "year", Year.now())));
    }

    @Test
    void notParsedInclude() throws IOException {
        Files.writeString(fileSystem.getPath("copyright.fmi"), "Copyright 2022-${year} ${me}<br>\nAll rights reserved.");
        Template template = builder.with(IncludeDirectiveFeature.ENABLED).getTemplate("template", "<#include 'copyright.fmi' parse=false>");
        assertEquals("Copyright 2022-${year} ${me}<br>\nAll rights reserved.", template.process(Map.of("me", "Jens Kaiser", "year", Year.now())));
    }

    @Test
    void notParsedDefaultedInclude() throws IOException {
        Files.writeString(fileSystem.getPath("copyright.fmi"), "Copyright 2022-${year} ${me}<br>\nAll rights reserved.");
        Template template = builder.with(IncludeDirectiveFeature.ENABLED).without(IncludeDirectiveFeature.PARSE).getTemplate("template", "<#include 'copyright.fmi'>");
        assertEquals("Copyright 2022-${year} ${me}<br>\nAll rights reserved.", template.process(Map.of("me", "Jens Kaiser", "year", Year.now())));
    }

    @Test
    void recursiveInclude() throws IOException {
        Files.writeString(fileSystem.getPath("recursive.fmt"), "recursive <#include 'recursive.fmt'>");
        Template template = builder.with(IncludeDirectiveFeature.ENABLED).getTemplate("template", "<#include 'recursive.fmt'>");
        assertEquals("recursive recursive recursive recursive recursive ", template.process(Map.of()));
    }
}
