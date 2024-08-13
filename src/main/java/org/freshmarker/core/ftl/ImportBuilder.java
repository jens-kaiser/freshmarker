package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token;
import ftl.ast.MacroDefinition;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.fragment.Fragment;

import java.util.List;

public class ImportBuilder implements UnaryFtlVisitor<List<Fragment>> {
    private final Template template;
    private final Configuration configuration;
    private final String nameSpace;

    public ImportBuilder(Template template, Configuration configuration, String nameSpace) {
        this.template = template;
        this.configuration = configuration;
        this.nameSpace = nameSpace;
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
        return ftl.accept(new FragmentBuilder(template, configuration, nameSpace), input);
    }
}
