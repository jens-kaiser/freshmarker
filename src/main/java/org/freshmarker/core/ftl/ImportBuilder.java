package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token;
import ftl.ast.MacroDefinition;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.features.FeatureSet;
import org.freshmarker.core.fragment.Fragment;

import java.util.List;
import java.util.Objects;

public class ImportBuilder implements UnaryFtlVisitor<List<Fragment>> {
    private final Template template;
    private final Configuration configuration;
    private final String nameSpace;
    private final FeatureSet featureSet;
    private FragmentBuilder fragmentBuilder;

    public ImportBuilder(Template template, Configuration configuration, String nameSpace, FeatureSet featureSet) {
        this.template = template;
        this.configuration = configuration;
        this.nameSpace = nameSpace;
        this.featureSet = featureSet;
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
        fragmentBuilder = Objects.requireNonNullElseGet(fragmentBuilder, () -> new FragmentBuilder(template, configuration, nameSpace, featureSet));
        return ftl.accept(fragmentBuilder, input);
    }
}
