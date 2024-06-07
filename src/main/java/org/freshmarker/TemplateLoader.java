package org.freshmarker;

import java.io.IOException;
import java.nio.charset.Charset;

public interface TemplateLoader {
    String getTemplate(String filename, Charset charset) throws IOException;

    default String getTemplate(String filename) throws IOException {
        return getTemplate(filename, Charset.defaultCharset());
    }

    String getImport(String filename, Charset charset) throws IOException;

    default String getImport(String filename) throws IOException {
        return getImport(filename, Charset.defaultCharset());
    }
}