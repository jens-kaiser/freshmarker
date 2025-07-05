package org.freshmarker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemTemplateLoader implements org.freshmarker.api.TemplateLoader {
    private final FileSystem fileSystem;

    public FileSystemTemplateLoader(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String getImport(Path path, String filename, Charset charset) throws IOException {
        return getContent(filename, charset);
    }

    private String getContent(String filename, Charset charset) throws IOException {
        return Files.readString(fileSystem.getPath(filename), charset);
    }
}
