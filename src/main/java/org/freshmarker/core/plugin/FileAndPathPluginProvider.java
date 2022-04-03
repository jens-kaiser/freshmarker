package org.freshmarker.core.plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInFunction;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuiltIn;
import org.freshmarker.core.model.file.TemplateFile;
import org.freshmarker.core.model.file.TemplatePath;
import org.freshmarker.core.model.TemplateObject;

public class FileAndPathPluginProvider implements PluginProvider {

  private static final BuiltInKeyBuilder<TemplateFile> FILE_BUILDER = new BuiltInKeyBuilder<>(TemplateFile.class);
  private static final BuiltInKeyBuilder<TemplatePath> PATH_BUILDER = new BuiltInKeyBuilder<>(TemplatePath.class);

  @Override
  public void registerBuildIn(Map<BuildInKey, BuiltIn> builtIns) {
    register(builtIns, FILE_BUILDER.of("exists"), (x, y, e) -> processFile(x, File::exists, e));
    register(builtIns, FILE_BUILDER.of("is_directory"), (x, y, e) -> processFile(x, File::isDirectory, e));
    register(builtIns, FILE_BUILDER.of("is_file"), (x, y, e) -> processFile(x, File::isFile, e));
    register(builtIns, FILE_BUILDER.of("can_execute"), (x, y, e) -> processFile(x, File::canExecute, e));
    register(builtIns, FILE_BUILDER.of("can_read"), (x, y, e) -> processFile(x, File::canRead, e));
    register(builtIns, FILE_BUILDER.of("can_write"), (x, y, e) -> processFile(x, File::canWrite, e));
    register(builtIns, FILE_BUILDER.of("size"), (x, y, e) -> processFile(x, File::length, e));
    register(builtIns, FILE_BUILDER.of("name"), (x, y, e) -> processFile(x, File::getName, e));
    register(builtIns, FILE_BUILDER.of("parent"), (x, y, e) -> processFile(x, File::getParentFile, e));
    register(builtIns, PATH_BUILDER.of("exists"), (x, y, e) -> processPath(x, File::exists, e));
    register(builtIns, PATH_BUILDER.of("is_directory"), (x, y, e) -> processPath(x, File::isDirectory, e));
    register(builtIns, PATH_BUILDER.of("is_file"), (x, y, e) -> processPath(x, File::isFile, e));
    register(builtIns, PATH_BUILDER.of("can_execute"), (x, y, e) -> processPath(x, File::canExecute, e));
    register(builtIns, PATH_BUILDER.of("can_read"), (x, y, e) -> processPath(x, File::canRead, e));
    register(builtIns, PATH_BUILDER.of("can_write"), (x, y, e) -> processPath(x, File::canWrite, e));
    register(builtIns, PATH_BUILDER.of("size"), (x, y, e) -> processPath(x, File::length, e));
    register(builtIns, PATH_BUILDER.of("name"), (x, y, e) -> processPath(x, File::getName, e));
    register(builtIns, PATH_BUILDER.of("parent"), (x, y, e) -> processPath(x, File::getParentFile, e));
  }

  private void register(Map<BuildInKey, BuiltIn> buildIns, BuildInKey buildInKey, BuiltInFunction function) {
    buildIns.put(buildInKey, new TypedBuiltIn(function));
  }

  @Override
  public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
    mapper.put(File.class, o -> new TemplateFile((File) o));
    mapper.put(Path.of(".").getClass(), o -> new TemplatePath((Path) o));
    mapper.put(Path.class, o -> new TemplatePath((Path) o));
  }

  private TemplateObject processFile(TemplateObject value, Function<File, ?> function, Environment environment) {
    File input = ((TemplateFile) value).getValue();
    return environment.mapObject(function.apply(input));
  }

  private TemplateObject processPath(TemplateObject value, Function<File, ?> function, Environment environment) {
    Path input = ((TemplatePath) value).getValue();
    Function<Path, File> convert = Path::toFile;
    return environment.mapObject(convert.andThen(function).apply(input));
  }
}
