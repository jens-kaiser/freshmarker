package org.freshmarker;

import org.freshmarker.api.TemplateLoader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultFileSystemTemplateLoader implements TemplateLoader {
    @Override
    public String getImport(Path path, String filename, Charset charset) throws IOException {
        return Files.readString(path.resolve(filename), charset);
    }
}
