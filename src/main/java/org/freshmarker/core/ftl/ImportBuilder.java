package org.freshmarker.core.ftl;

import ftl.FreshMarkerParser;
import ftl.Node;
import ftl.Token;
import ftl.ast.Block;
import ftl.ast.ImportInstruction;
import ftl.ast.MacroDefinition;
import ftl.ast.Root;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.TokenLineNormalizer;
import org.freshmarker.core.fragment.BlockFragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

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
    public BlockFragment visit(Root ftl, BlockFragment input) {
        for (Node node : ftl.children(true)) {
            node.accept(this, input);
        }
        return input;
    }

    @Override
    public BlockFragment visit(Block ftl, BlockFragment input) {
        for (Node node : ftl.children(true)) {
            node.accept(this, input);
        }
        return input;
    }

    @Override
    public BlockFragment visit(MacroDefinition ftl, BlockFragment input) {
        return ftl.accept(new FragmentBuilder(template, configuration, nameSpace), input);
    }
}
