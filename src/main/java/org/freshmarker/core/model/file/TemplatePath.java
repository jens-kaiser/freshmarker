package org.freshmarker.core.model.file;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.nio.file.Path;

public class TemplatePath extends TemplatePrimitive<Path> {

    public TemplatePath(Path value) {
        super(value);
    }
}
