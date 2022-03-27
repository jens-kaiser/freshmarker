package org.freshmarker.core.plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuildInComponent;
import org.freshmarker.core.buildin.BuildInFunction;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.model.file.TemplateFile;
import org.freshmarker.core.model.file.TemplatePath;
import org.freshmarker.core.model.primitive.TemplateObject;

public class FileAndPathPluginProvider implements PluginProvider {

  public void registerBuildIn(Map<String, BuildInComponent> buildIns) {
    register(buildIns, TemplateFile.class, "exists", (x, y, e) -> processFile(x, File::exists, e));
    register(buildIns, TemplateFile.class, "is_directory", (x, y, e) -> processFile(x, File::isDirectory, e));
    register(buildIns, TemplateFile.class, "is_file", (x, y, e) -> processFile(x, File::isFile, e));
    register(buildIns, TemplateFile.class, "can_execute", (x, y, e) -> processFile(x, File::canExecute, e));
    register(buildIns, TemplateFile.class, "can_read", (x, y, e) -> processFile(x, File::canRead, e));
    register(buildIns, TemplateFile.class, "can_write", (x, y, e) -> processFile(x, File::canWrite, e));
    register(buildIns, TemplateFile.class, "size", (x, y, e) -> processFile(x, File::length, e));
    register(buildIns, TemplateFile.class, "name", (x, y, e) -> processFile(x, File::getName, e));
    register(buildIns, TemplateFile.class, "parent", (x, y, e) -> processFile(x, File::getParentFile, e));
    register(buildIns, TemplatePath.class, "exists", (x, y, e) -> processPath(x, File::exists, e));
    register(buildIns, TemplatePath.class, "is_directory", (x, y, e) -> processPath(x, File::isDirectory, e));
    register(buildIns, TemplatePath.class, "is_file", (x, y, e) -> processPath(x, File::isFile, e));
    register(buildIns, TemplatePath.class, "can_execute", (x, y, e) -> processPath(x, File::canExecute, e));
    register(buildIns, TemplatePath.class, "can_read", (x, y, e) -> processPath(x, File::canRead, e));
    register(buildIns, TemplatePath.class, "can_write", (x, y, e) -> processPath(x, File::canWrite, e));
    register(buildIns, TemplatePath.class, "size", (x, y, e) -> processPath(x, File::length, e));
    register(buildIns, TemplatePath.class, "name", (x, y, e) -> processPath(x, File::getName, e));
    register(buildIns, TemplatePath.class, "parent", (x, y, e) -> processPath(x, File::getParentFile, e));
  }

  @Override
  public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
    mapper.put(File.class, o -> new TemplateFile((File) o));
    mapper.put(Path.of(".").getClass(), o -> new TemplatePath((Path) o));
    mapper.put(Path.class, o -> new TemplatePath((Path) o));
  }

  protected <T extends TemplateObject> void register(Map<String, BuildInComponent> buildIns, Class<T> type, String name, BuildInFunction function) {
    buildIns.computeIfAbsent(name, k -> new BuildInComponent()).add(type, new TypedBuildIn(function));
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
