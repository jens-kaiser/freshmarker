package org.freshmarker.api;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

public interface TemplateLoader {
    String getImport(Path path, String filename, Charset charset) throws IOException;

    default String getImport(Path path, String filename) throws IOException {
        return getImport(path, filename, Charset.defaultCharset());
    }
}