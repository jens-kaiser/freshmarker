package org.freshmarker;

import java.io.IOException;

public interface TemplateLoader {
    String getTemplate(String filename) throws IOException;
    String getImport(String filename) throws IOException;
}