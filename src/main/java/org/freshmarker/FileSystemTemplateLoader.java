package org.freshmarker;

import java.io.IOException;
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
    public String getTemplate(String filename) throws IOException {
        return Files.readString(fileSystem.getPath(filename));
    }

    @Override
    public String getImport(String filename) throws IOException {
        return Files.readString(fileSystem.getPath(filename));
    }
}
