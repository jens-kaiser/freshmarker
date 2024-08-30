package org.freshmarker.core;

import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;

import java.util.Optional;

public interface Environment {

    TemplateObject getValue(String name);

    boolean checkVariable(String name);

    Optional<Fragment> getNestedContent();

    void createVariable(String name, TemplateObject value);

    void setVariable(String name, TemplateObject value);

    TemplateObject getVariable(String name);
}
