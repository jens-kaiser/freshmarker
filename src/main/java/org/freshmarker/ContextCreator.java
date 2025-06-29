package org.freshmarker;

import org.freshmarker.api.FeatureSet;
import org.freshmarker.api.UserDirective;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.environment.NameSpaced;

import java.io.Writer;
import java.util.Map;

interface ContextCreator {
    ProcessContext createContext(StaticContext context, Map<String, Object> dataModel, Writer writer, Map<NameSpaced, UserDirective> userDirectives, FeatureSet featureSet);
}
