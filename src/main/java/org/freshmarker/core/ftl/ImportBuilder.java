package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token;
import ftl.ast.Block;
import ftl.ast.MacroDefinition;
import ftl.ast.Root;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.fragment.BlockFragment;

public class ImportBuilder implements FtlVisitor<BlockFragment, BlockFragment> {
    private final Template template;
    private final Configuration configuration;
    private final String nameSpace;

    public ImportBuilder(Template template, Configuration configuration, String nameSpace) {
        this.template = template;
        this.configuration = configuration;
        this.nameSpace = nameSpace;
    }

    @Override
    public BlockFragment handleWithException(Node node) {
        throw new ParsingException("unsupported import operation", node);
    }

    @Override
    public BlockFragment visit(Token ftl, BlockFragment input) {
        return input;
    }

    @Override
    public BlockFragment visit(MacroDefinition ftl, BlockFragment input) {
        return ftl.accept(new FragmentBuilder(template, configuration, nameSpace), input);
    }
}
