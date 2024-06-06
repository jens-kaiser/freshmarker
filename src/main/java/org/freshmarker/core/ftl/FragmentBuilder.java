package org.freshmarker.core.ftl;

import ftl.FreshMarkerParser;
import ftl.Node.TerminalNode;
import ftl.Token.TokenType;
import ftl.Node;
import ftl.Token;
import ftl.ast.Assignment;
import ftl.ast.Block;
import ftl.ast.FTLHeader;
import ftl.ast.IDENTIFIER;
import ftl.ast.IfStatement;
import ftl.ast.ImportInstruction;
import ftl.ast.Interpolation;
import ftl.ast.ListInstruction;
import ftl.ast.MacroDefinition;
import ftl.ast.NestedInstruction;
import ftl.ast.OutputFormatBlock;
import ftl.ast.ReturnInstruction;
import ftl.ast.Root;
import ftl.ast.STRING_LITERAL;
import ftl.ast.SettingInstruction;
import ftl.ast.SwitchInstruction;
import ftl.ast.Text;
import ftl.ast.UserDirective;
import ftl.ast.VarInstruction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.TokenLineNormalizer;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.directive.MacroUserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.ConstantFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.HashListFragment;
import org.freshmarker.core.fragment.InterpolationFragment;
import org.freshmarker.core.fragment.NestedInstructionFragment;
import org.freshmarker.core.fragment.OutputFormatFragment;
import org.freshmarker.core.fragment.ReturnInstructionFragment;
import org.freshmarker.core.fragment.SequenceListFragment;
import org.freshmarker.core.fragment.SettingFragment;
import org.freshmarker.core.fragment.UserDirectiveFragment;
import org.freshmarker.core.fragment.VariableFragment;
import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FragmentBuilder implements UnaryFtlVisitor<BlockFragment> {

    private static final Logger logger = LoggerFactory.getLogger(FragmentBuilder.class);

    private final Template template;
    private final Configuration configuration;
    private final String nameSpace;

    public FragmentBuilder(Template template, Configuration configuration, String nameSpace) {
        this.template = template;
        this.configuration = configuration;
        this.nameSpace = nameSpace;
    }

    @Override
    public BlockFragment visit(Node ftl, BlockFragment input) {
        logger.info("unsupported node operation: {}", ftl.getClass());
        return input;
    }

    private static final ConstantFragment ONE_WHITESPACE = new ConstantFragment(" ");

    @Override
    public BlockFragment visit(Token ftl, BlockFragment input) {
        String image = ftl.toString();
        if (ftl.getType() == TokenType.PRINTABLE_CHARS) {
            input.addFragment(new ConstantFragment(image));
        } else if (ftl.getType() == TokenType.WHITESPACE) {
            if (" ".equals(image)) {
                input.addFragment(ONE_WHITESPACE);
            } else {
                input.addFragment(new ConstantFragment(image));
            }
        }
        return input;
    }

    @Override
    public BlockFragment visit(FTLHeader ftl, BlockFragment input) {
        return input;
    }

    @Override
    public BlockFragment visit(Text ftl, BlockFragment input) {
        ftl.getAllTokens(false).stream().map(TerminalNode::toString).map(ConstantFragment::new).forEach(input::addFragment);
        return input;
    }

    @Override
    public BlockFragment visit(IfStatement ftl, BlockFragment input) {
        input.addFragment(ftl.accept(new IfFragmentBuilder(this), null));
        return input;
    }

    @Override
    public BlockFragment visit(SwitchInstruction ftl, BlockFragment input) {
        input.addFragment(ftl.accept(new SwitchFragmentBuilder(this), null));
        return input;
    }

    @Override
    public BlockFragment visit(Interpolation ftl, BlockFragment input) {
        TemplateObject interpolation = ftl.getChild(1).accept(InterpolationBuilder.INSTANCE, null);
        input.addFragment(new InterpolationFragment(new TemplateMarkup(interpolation), ftl));
        return input;
    }

    @Override
    public BlockFragment visit(ListInstruction ftl, BlockFragment input) {
        TemplateObject list = ftl.getChild(3).accept(InterpolationBuilder.INSTANCE, null);
        int looperIndex = ftl.getChild(6).getType() == TokenType.COMMA ? 9 : 7;
        int blockIndex = looperIndex;
        String looperIdentifier = null;
        if (ftl.getChild(looperIndex - 1).getType() == TokenType.WITH) {
            looperIdentifier = ((IDENTIFIER) ftl.getChild(looperIndex)).toString();
            blockIndex += 2;
        }
        BlockFragment block = ftl.getChild(blockIndex).accept(this, new BlockFragment());
        if (ftl.getChild(6).getType() == TokenType.COMMA) {
            String keyIdentifier = ftl.getChild(5).toString();
            String valueIdentifier = ftl.getChild(7).toString();
            input.addFragment(new HashListFragment(list, keyIdentifier, valueIdentifier, looperIdentifier, block, ftl));
        } else {
            String identifier = ftl.getChild(5).toString();
            input.addFragment(new SequenceListFragment(list, identifier, looperIdentifier, block, ftl));
        }
        return input;
    }

    @Override
    public BlockFragment visit(SettingInstruction ftl, BlockFragment input) {
        IDENTIFIER identifier = (IDENTIFIER) ftl.getChild(3);
        TemplateObject expression = ftl.getChild(5).accept(InterpolationBuilder.INSTANCE, null);
        input.addFragment(new SettingFragment(identifier.toString(), expression, ftl));
        return input;
    }

    @Override
    public BlockFragment visit(OutputFormatBlock ftl, BlockFragment input) {
        STRING_LITERAL format = (STRING_LITERAL) ftl.getChild(3);
        BlockFragment block = ftl.getChild(5).accept(this, new BlockFragment());
        String image = format.toString();
        input.addFragment(new OutputFormatFragment(block, image.substring(1, image.length() - 1)));
        return input;
    }

    @Override
    public BlockFragment visit(UserDirective ftl, BlockFragment input) {
        int nameIndex;
        String nameSpace;
        if (ftl.get(2).getType() == TokenType.DOT) {
            nameSpace = ftl.get(1).toString();
            nameIndex = 3;
        } else {
            nameSpace = null;
            nameIndex = 1;
        }
        String name = ftl.get(nameIndex).toString();
        HashMap<String, TemplateObject> namedArgs = new HashMap<>();
        ftl.getChild(nameIndex + 1).accept(NamedArgsBuilder.INSTANCE, namedArgs);
        logger.debug("user directive: {}.{} {}", nameSpace, name, namedArgs);
        Node node = ftl.children().stream().skip(nameIndex + 1)
                .dropWhile(n -> n.getType() == null || !Set.of(TokenType.GT, TokenType.CLOSE_TAG).contains((TokenType) n.getType()))
                .skip(1).findFirst().orElse(null);
        BlockFragment body = null;
        if (node != null) {
            body = node.accept(this, new BlockFragment());
        }
        logger.debug("user directive: {} {}", node, body);
        input.addFragment(new UserDirectiveFragment(name, nameSpace, namedArgs, body));
        return input;
    }

    @Override
    public BlockFragment visit(MacroDefinition ftl, BlockFragment input) {
        TokenType type = (TokenType) ftl.getChild(1).getType();
        if (type != TokenType.MACRO) {
            return input;
        }
        String name = getName(ftl.getChild(3));
        List<ParameterHolder> parameterList = getParameterHolders(ftl);
        Fragment block = getFragment(ftl);
        logger.debug("macro directive: namespace={}, type={}, name={}, block={}", nameSpace, type, name, block);
        template.getUserDirectives().put(new NameSpaced(nameSpace, name), new MacroUserDirective(block, parameterList));
        return input;
    }

    private Fragment getFragment(MacroDefinition ftl) {
        if (ftl.getChild(ftl.getChildCount() - 1).getType() == TokenType.CLOSE_EMPTY_TAG
                || ftl.getChild(ftl.getChildCount() - 2).getType() == TokenType.CLOSE_TAG) {
            return ConstantFragment.EMPTY;
        }
        return ftl.getChild(ftl.getChildCount() - 2).accept(this, new BlockFragment());
    }

    private List<ParameterHolder> getParameterHolders(MacroDefinition ftl) {
        int parameterListIndex = getParameterListIndex(ftl);
        if (ftl.getChild(parameterListIndex).getType() == TokenType.CLOSE_TAG) {
            return Collections.emptyList();
        }
        return ftl.getChild(parameterListIndex).accept(ParameterListBuilder.INSTANCE, new ArrayList<>());
    }

    private int getParameterListIndex(MacroDefinition ftl) {
        if (ftl.getChild(4).getType() != TokenType.OPEN_PAREN) {
            return 4;
        }
        if (ftl.getChild(6).getType() != TokenType.CLOSE_PAREN) {
            throw new ProcessException("missing CLOSE_PAREN at " + ftl.getChild(6).getLocation());
        }
        return 5;
    }

    private String getName(Node node) {
        if (node.getType() == TokenType.IDENTIFIER) {
            return node.toString();
        }
        if (node.getType() == TokenType.STRING_LITERAL) {
            String image = node.getImage();
            return image.substring(1, image.length() - 1);
        }
        throw new ParsingException("missing identifier or string literal", node.get(6));
    }


    @Override
    public BlockFragment visit(Assignment ftl, BlockFragment input) {
        TokenType type = (TokenType) ftl.getChild(1).getType();
        if (type != TokenType.SET) {
            throw new ParsingException("assignment type " + type + " not supported", ftl.getChild(1));
        }
        String name = getName(ftl.getChild(3));
        if (ftl.getChildCount() != 7) {
            throw new ParsingException("only one assignment supported", ftl);
        }
        input.addFragment(new VariableFragment(name, ftl.getChild(5).accept(InterpolationBuilder.INSTANCE, null), true, ftl.getChild(5)));
        return input;
    }

    @Override
    public BlockFragment visit(VarInstruction ftl, BlockFragment input) {
        String name = getName(ftl.getChild(3));
        if (ftl.getChildCount() != 7) {
            throw new ParsingException("only one assignment supported", ftl);
        }
        input.addFragment(new VariableFragment(name, ftl.getChild(5).accept(InterpolationBuilder.INSTANCE, null), false, ftl.getChild(5)));
        return input;
    }

    @Override
    public BlockFragment visit(NestedInstruction ftl, BlockFragment input) {
        input.addFragment(new NestedInstructionFragment());
        return input;
    }

    @Override
    public BlockFragment visit(ReturnInstruction ftl, BlockFragment input) {
        input.addFragment(new ReturnInstructionFragment());
        return input;
    }

    @Override
    public BlockFragment visit(ImportInstruction ftl, BlockFragment input) {
        String path = ftl.get(3).accept(InterpolationBuilder.INSTANCE, null).toString();
        String namespace = ftl.get(5).toString();
        try {
            FreshMarkerParser parser = new FreshMarkerParser(Files.readString(configuration.getFileSystem().getPath(path)));
            parser.setInputSource(namespace);
            parser.Root();
            Root root = (Root) parser.rootNode();
            new TokenLineNormalizer().normalize(root);
            root.accept(new ImportBuilder(template, configuration, namespace), template.getRootFragment());
        } catch (FileNotFoundException e) {
            throw new ParsingException("cannot find import: " + path, ftl);
        } catch (IOException e) {
            throw new ParsingException("cannot read import: " + path, ftl);
        }
        return input;
    }
}
