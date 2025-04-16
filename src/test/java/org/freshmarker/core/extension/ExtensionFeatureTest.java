package org.freshmarker.core.extension;

import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.api.BuiltIn;
import org.freshmarker.api.FeatureSet;
import org.freshmarker.api.TemplateFeature;
import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.extension.Register;
import org.freshmarker.api.extension.TemplateFeatureProvider;
import org.freshmarker.api.extension.support.BuiltInRegister;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExtensionFeatureTest {
    enum TestFeature implements TemplateFeature {
        ENABLED
    }

    @Test
    void enabled() {
        Configuration configuration = new Configuration();
        configuration.register((TemplateFeatureProvider) () -> Set.of(TestFeature.ENABLED));
        configuration.register(new BuiltInProvider() {
            private boolean enabled;
            @Override
            public void init(FeatureSet featureSet) {
                enabled = featureSet.isEnabled(TestFeature.ENABLED);
            }

            @Override
            public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
                BuiltInRegister register = new BuiltInRegister();
                System.out.println(enabled);
                if (enabled) {
                    register.add(TemplateString.class, "test", ((value, parameters, context) -> new TemplateString("test")));
                }
                return  register;
            }
        });
        Template template = configuration.builder().with(TestFeature.ENABLED).getTemplate("test", "${'gonzo'?test}");
        assertEquals("test", template.process(Map.of()));
    }

    @Test
    void disabled() {
        Configuration configuration = new Configuration();
        configuration.register((TemplateFeatureProvider) () -> Set.of(TestFeature.ENABLED));
        configuration.register(new BuiltInProvider() {
            private boolean enabled;
            @Override
            public void init(FeatureSet featureSet) {
                enabled = featureSet.isEnabled(TestFeature.ENABLED);
            }

            @Override
            public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
                BuiltInRegister register = new BuiltInRegister();
                if (enabled) {
                    register.add(TemplateString.class, "test", ((value, parameters, context) -> new TemplateString("test")));
                }
                return  register;
            }
        });
        Template template = configuration.builder().without(TestFeature.ENABLED).getTemplate("test", "${'gonzo'?test}");
        Map<String, Object> dataModel = Map.of();
        assertThrows(UnsupportedBuiltInException.class, () -> template.process(dataModel));
    }
}
