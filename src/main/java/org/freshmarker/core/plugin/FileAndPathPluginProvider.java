package org.freshmarker.core.plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInFunction;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.FunctionalBuiltIn;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.file.TemplateFile;
import org.freshmarker.core.model.file.TemplatePath;

public class FileAndPathPluginProvider implements PluginProvider {

  private static final BuiltInKeyBuilder<TemplateFile> FILE_BUILDER = new BuiltInKeyBuilder<>(TemplateFile.class);
  private static final BuiltInKeyBuilder<TemplatePath> PATH_BUILDER = new BuiltInKeyBuilder<>(TemplatePath.class);

  @Override
  public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
    register(builtIns, FILE_BUILDER.of("exists"), (x, y, e) -> processFile(x, File::exists, e));
    register(builtIns, FILE_BUILDER.of("is_directory"), (x, y, e) -> processFile(x, File::isDirectory, e));
    register(builtIns, FILE_BUILDER.of("is_file"), (x, y, e) -> processFile(x, File::isFile, e));
    register(builtIns, FILE_BUILDER.of("can_execute"), (x, y, e) -> processFile(x, File::canExecute, e));
    register(builtIns, FILE_BUILDER.of("can_read"), (x, y, e) -> processFile(x, File::canRead, e));
    register(builtIns, FILE_BUILDER.of("can_write"), (x, y, e) -> processFile(x, File::canWrite, e));
    register(builtIns, FILE_BUILDER.of("size"), (x, y, e) -> processFile(x, File::length, e));
    register(builtIns, FILE_BUILDER.of("name"), (x, y, e) -> processFile(x, File::getName, e));
    register(builtIns, FILE_BUILDER.of("parent"), (x, y, e) -> processFile(x, File::getParentFile, e));
    register(builtIns, PATH_BUILDER.of("exists"), (x, y, e) -> processFilePath(x, File::exists, e));
    register(builtIns, PATH_BUILDER.of("is_directory"), (x, y, e) -> processFilePath(x, File::isDirectory, e));
    register(builtIns, PATH_BUILDER.of("is_file"), (x, y, e) -> processFilePath(x, File::isFile, e));
    register(builtIns, PATH_BUILDER.of("can_execute"), (x, y, e) -> processFilePath(x, File::canExecute, e));
    register(builtIns, PATH_BUILDER.of("can_read"), (x, y, e) -> processFilePath(x, File::canRead, e));
    register(builtIns, PATH_BUILDER.of("can_write"), (x, y, e) -> processFilePath(x, File::canWrite, e));
    register(builtIns, PATH_BUILDER.of("size"), (x, y, e) -> processFilePath(x, File::length, e));
    register(builtIns, PATH_BUILDER.of("name"), (x, y, e) -> processPath(x, Path::getFileName, e));
    register(builtIns, PATH_BUILDER.of("parent"), (x, y, e) -> processPath(x, Path::getParent, e));
  }

  private void register(Map<BuiltInKey, BuiltIn> buildIns, BuiltInKey builtInKey, BuiltInFunction function) {
    buildIns.put(builtInKey, new FunctionalBuiltIn(function));
  }

  @Override
  public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
    mapper.put(File.class, o -> new TemplateFile((File) o));
    mapper.put(Path.of(".").getClass(), o -> new TemplatePath((Path) o));
    mapper.put(Path.class, o -> new TemplatePath((Path) o));
  }

  private TemplateObject processFile(TemplateObject value, Function<File, ?> function, ProcessContext context) {
    File input = ((TemplateFile) value).getValue();
    return context.getEnvironment().mapObject(function.apply(input));
  }

  private TemplateObject processFilePath(TemplateObject value, Function<File, ?> function, ProcessContext context) {
    Path input = ((TemplatePath) value).getValue();
    Function<Path, File> convert = Path::toFile;
    return context.getEnvironment().mapObject(convert.andThen(function).apply(input));
  }
  private TemplateObject processPath(TemplateObject value, Function<Path, ?> function, ProcessContext context) {
    Path input = ((TemplatePath) value).getValue();
    return context.getEnvironment().mapObject(function.apply(input));
  }
}
