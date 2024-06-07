package org.freshmarker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;

public class FileSystemTemplateLoader implements TemplateLoader{
    private final FileSystem fileSystem;

    public FileSystemTemplateLoader(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public FileSystemTemplateLoader() {
        this.fileSystem = FileSystems.getDefault();
    }

    @Override
    public String getTemplate(String filename, Charset charset) throws IOException {
        return getContent(filename, charset);
    }

    @Override
    public String getImport(String filename, Charset charset) throws IOException {
        return getContent(filename, charset);
    }

    private String getContent(String filename, Charset charset) throws IOException {
        return Files.readString(fileSystem.getPath(filename), charset);
    }
}
