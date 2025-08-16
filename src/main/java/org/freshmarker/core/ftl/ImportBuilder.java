package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token;
import ftl.ast.MacroDefinition;
import org.freshmarker.Template;
import org.freshmarker.api.FeatureSet;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.fragment.Fragment;

import java.util.List;
import java.util.Objects;

public class ImportBuilder implements UnaryFtlVisitor<List<Fragment>> {
    private final Template template;
    private final String nameSpace;
    private final FeatureSet featureSet;
    private FragmentBuilder fragmentBuilder;
    private final int includeLevel;
    private final StaticContext templateContext;

    public ImportBuilder(Template template, String nameSpace, FeatureSet featureSet, int includeLevel, StaticContext templateContext) {
        this.template = template;
        this.nameSpace = nameSpace;
        this.featureSet = featureSet;
        this.includeLevel = includeLevel;
        this.templateContext = templateContext;
    }

    @Override
    public List<Fragment> handleWithException(Node node) {
        throw new ParsingException("unsupported import operation", node);
    }

    @Override
    public List<Fragment> visit(Token ftl, List<Fragment> input) {
        return input;
    }

    @Override
    public List<Fragment> visit(MacroDefinition ftl, List<Fragment> input) {
        fragmentBuilder = Objects.requireNonNullElseGet(fragmentBuilder, () -> new FragmentBuilder(template, nameSpace, featureSet, includeLevel + 1, templateContext));
        return ftl.accept(fragmentBuilder, input);
    }
}
