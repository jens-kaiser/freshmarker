package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReducingLoopVariableEnvironment extends WrapperEnvironment {
    private final List<String> identifiers;

    public ReducingLoopVariableEnvironment(Environment wrapped, String... identifiers) {
        super(wrapped);
        this.identifiers = Arrays.stream(identifiers).filter(Objects::nonNull).toList();
    }

    @Override
    public TemplateObject getValue(String name) {
        return identifiers.contains(name) ? TemplateNull.NULL : wrapped.getValue(name);
    }
}