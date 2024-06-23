package org.freshmarker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultFileSystemTemplateLoader implements TemplateLoader {
    @Override
    public String getImport(Path path, String filename, Charset charset) throws IOException {
        System.out.println(path.resolve(filename));
        System.out.println(path.toAbsolutePath().resolve(filename));
        return Files.readString(path.resolve(filename), charset);
    }
}
