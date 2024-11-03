package org.freshmarker.core.ftl;

import ftl.FreshMarkerParser;
import ftl.Node;
import ftl.Node.NodeType;
import ftl.Node.TerminalNode;
import ftl.Token;
import ftl.Token.TokenType;
import ftl.ast.Assignment;
import ftl.ast.BrickInstruction;
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
import ftl.ast.SettingInstruction;
import ftl.ast.SwitchInstruction;
import ftl.ast.Text;
import ftl.ast.UserDirective;
import ftl.ast.VarInstruction;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.TokenLineNormalizer;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.directive.MacroUserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.fragment.ConstantFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.Fragments;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FragmentBuilder implements UnaryFtlVisitor<List<Fragment>> {

    private static final Logger logger = LoggerFactory.getLogger(FragmentBuilder.class);

    private static final NamedArgsBuilder NAMED_ARGS_BUILDER = new NamedArgsBuilder();
    private static final ParameterListBuilder PARAMETER_LIST_BUILDER = new ParameterListBuilder();

    private static final Map<NodeType, Comparator<String>> COMPARATORS = Map.of(
            TokenType.ASCENDING, Comparator.naturalOrder(), TokenType.DESCENDING, Comparator.reverseOrder());

    private final Template template;
    private final Configuration configuration;
    private final String nameSpace;

    public FragmentBuilder(Template template, Configuration configuration, String nameSpace) {
        this.template = template;
        this.configuration = configuration;
        this.nameSpace = nameSpace;
    }

    @Override
    public List<Fragment> visit(Node ftl, List<Fragment> input) {
        logger.info("unsupported node operation: {}", ftl.getClass());
        return input;
    }

    private static final ConstantFragment ONE_WHITESPACE = new ConstantFragment(" ");

    @Override
    public List<Fragment> visit(Token ftl, List<Fragment> input) {
        if (!input.isEmpty() && input.getLast() instanceof ConstantFragment constantFragment) {
            constantFragment.add(ftl.toString());
            return input;
        }
        String image = ftl.toString();
        if (ftl.getType() == TokenType.PRINTABLE_CHARS) {
            input.add(new ConstantFragment(image));
        } else if (ftl.getType() == TokenType.WHITESPACE) {
            if (" ".equals(image)) {
                input.add(ONE_WHITESPACE);
            } else {
                input.add(new ConstantFragment(image));
            }
        }
        return input;
    }

    @Override
    public List<Fragment> visit(Text ftl, List<Fragment> input) {
        String content = ftl.getAllTokens(false).stream().map(TerminalNode::toString).collect(Collectors.joining());
        input.add(new ConstantFragment(content));
        return input;
    }

    @Override
    public List<Fragment> visit(IfStatement ftl, List<Fragment> input) {
        input.add(ftl.accept(new IfFragmentBuilder(this), null));
        return input;
    }

    @Override
    public List<Fragment> visit(SwitchInstruction ftl, List<Fragment> input) {
        input.add(ftl.accept(new SwitchFragmentBuilder(this), null));
        return input;
    }

    @Override
    public List<Fragment> visit(Interpolation ftl, List<Fragment> input) {
        TemplateObject interpolation = ftl.get(1).accept(InterpolationBuilder.INSTANCE, null);
        input.add(new InterpolationFragment(new TemplateMarkup(interpolation), ftl));
        return input;
    }

    @Override
    public List<Fragment> visit(ListInstruction ftl, List<Fragment> input) {
        TemplateObject list = ftl.get(3).accept(InterpolationBuilder.INSTANCE, null);
        String identifier = ftl.get(5).toString();
        int index = 6;
        Comparator<String> comparator = null;
        if (ftl.get(index).getType() == TokenType.SORTED) {
            comparator = COMPARATORS.get(ftl.get(index + 1).getType());
            index += 2;
        }
        String valueIdentifier = null;
        if (ftl.get(index).getType() == TokenType.COMMA) {
            valueIdentifier = ftl.get(index + 1).toString();
            index += 2;
        }
        String looperIdentifier = null;
        if (ftl.get(index).getType() == TokenType.WITH) {
            looperIdentifier = ((IDENTIFIER) ftl.get(index + 1)).toString();
            index += 2;
        }
        TemplateObject filter = null;
        if (ftl.get(index).getType() == TokenType.FILTER) {
            filter = ftl.get(index + 1).accept(InterpolationBuilder.INSTANCE, null);
            index += 2;
        }
        TemplateObject limit = null;
        if (ftl.get(index).getType() == TokenType.LIMIT) {
            limit = ftl.get(index + 1).accept(InterpolationBuilder.INSTANCE, null);
            index += 2;
        }
        Fragment block = Fragments.optimize(ftl.get(index + 1).accept(this, new ArrayList<>()));
        if (valueIdentifier != null) {
            input.add(new HashListFragment(list, identifier, valueIdentifier, looperIdentifier, block, ftl, comparator, filter, limit));
        } else {
            input.add(new SequenceListFragment(list, identifier, looperIdentifier, block, ftl, filter, limit));
        }
        return input;
    }

    @Override
    public List<Fragment> visit(SettingInstruction ftl, List<Fragment> input) {
        IDENTIFIER identifier = (IDENTIFIER) ftl.get(3);
        TemplateObject expression = ftl.get(5).accept(InterpolationBuilder.INSTANCE, null);
        input.add(new SettingFragment(identifier.toString(), expression, ftl));
        return input;
    }

    @Override
    public List<Fragment> visit(OutputFormatBlock ftl, List<Fragment> input) {
        Fragment block = Fragments.optimize(ftl.get(5).accept(this, new ArrayList<>()));
        String image = ftl.get(3).toString();
        input.add(new OutputFormatFragment(block, image.substring(1, image.length() - 1)));
        return input;
    }

    @Override
    public List<Fragment> visit(UserDirective ftl, List<Fragment> input) {
        int nameIndex;
        String currentNameSpace;
        if (ftl.get(2).getType() == TokenType.DOT) {
            currentNameSpace = ftl.get(1).toString();
            nameIndex = 3;
        } else {
            currentNameSpace = null;
            nameIndex = 1;
        }
        String name = ftl.get(nameIndex).toString();
        HashMap<String, TemplateObject> namedArgs = new HashMap<>();
        ftl.get(nameIndex + 1).accept(NAMED_ARGS_BUILDER, namedArgs);
        logger.debug("user directive: {}.{} {}", currentNameSpace, name, namedArgs);
        Node node = ftl.children().stream().skip(nameIndex + 1L)
                .dropWhile(n -> n.getType() == null || !Set.<NodeType>of(TokenType.GT, TokenType.CLOSE_TAG).contains(n.getType()))
                .skip(1).findFirst().orElse(null);
        Fragment body = null;
        if (node != null) {
            body = Fragments.optimize(node.accept(this, new ArrayList<>()));
        }
        logger.debug("user directive: {} {}", node, body);
        input.add(new UserDirectiveFragment(name, currentNameSpace, namedArgs, Objects.requireNonNullElse(body, ConstantFragment.EMPTY)));
        return input;
    }

    @Override
    public List<Fragment> visit(MacroDefinition ftl, List<Fragment> input) {
        TokenType type = (TokenType) ftl.get(1).getType();
        if (type != TokenType.MACRO) {
            throw new ParsingException("function unsupported", ftl);
        }
        String name = ftl.get(3).toString();
        List<ParameterHolder> parameterList = getParameterHolders(ftl);
        Fragment block = getFragment(ftl);
        logger.debug("macro directive: namespace={}, type={}, name={}, block={}", nameSpace, type, name, block);
        template.getUserDirectives().put(new NameSpaced(nameSpace, name), new MacroUserDirective(block, parameterList));
        return input;
    }

    private Fragment getFragment(MacroDefinition ftl) {
        if (ftl.getLast().getType() == TokenType.CLOSE_EMPTY_TAG || ftl.get(ftl.size() - 2).getType() == TokenType.CLOSE_TAG) {
            return ConstantFragment.EMPTY;
        }
        return Fragments.optimize(ftl.get(ftl.size() - 2).accept(this, new ArrayList<>()));
    }

    private List<ParameterHolder> getParameterHolders(MacroDefinition ftl) {
        int parameterListIndex = getParameterListIndex(ftl);
        if (ftl.get(parameterListIndex).getType() == TokenType.CLOSE_TAG) {
            return Collections.emptyList();
        }
        return ftl.get(parameterListIndex).accept(PARAMETER_LIST_BUILDER, new ArrayList<>());
    }

    private int getParameterListIndex(MacroDefinition ftl) {
        if (ftl.get(4).getType() != TokenType.OPEN_PAREN) {
            return 4;
        }
        if (ftl.get(6).getType() != TokenType.CLOSE_PAREN) {
            throw new ProcessException("missing CLOSE_PAREN at " + ftl.get(6).getLocation());
        }
        return 5;
    }

    @Override
    public List<Fragment> visit(Assignment ftl, List<Fragment> input) {
        TokenType type = (TokenType) ftl.get(1).getType();
        if (type != TokenType.SET) {
            throw new ParsingException("assignment type " + type + " not supported", ftl.get(1));
        }
        String name = ftl.get(3).toString();
        if (ftl.size() != 7) {
            throw new ParsingException("only one assignment supported", ftl);
        }
        input.add(new VariableFragment(name, ftl.get(5).accept(InterpolationBuilder.INSTANCE, null), true, ftl.get(5)));
        return input;
    }

    @Override
    public List<Fragment> visit(VarInstruction ftl, List<Fragment> input) {
        String name = ftl.get(3).toString();
        if (ftl.size() != 7) {
            throw new ParsingException("only one assignment supported", ftl);
        }
        input.add(new VariableFragment(name, ftl.get(5).accept(InterpolationBuilder.INSTANCE, null), false, ftl.get(5)));
        return input;
    }

    @Override
    public List<Fragment> visit(NestedInstruction ftl, List<Fragment> input) {
        input.add(new NestedInstructionFragment());
        return input;
    }

    @Override
    public List<Fragment> visit(ReturnInstruction ftl, List<Fragment> input) {
        input.add(new ReturnInstructionFragment());
        return input;
    }

    @Override
    public List<Fragment> visit(ImportInstruction ftl, List<Fragment> input) {
        String path = ftl.get(3).accept(InterpolationBuilder.INSTANCE, null).toString();
        String namespace = ftl.get(5).toString();
        try {
            FreshMarkerParser parser = new FreshMarkerParser(template.getTemplateLoader().getImport(template.getPath(), path));
            parser.setInputSource(namespace);
            parser.Root();
            Root root = (Root) parser.rootNode();
            new TokenLineNormalizer().normalize(root);
            List<Fragment> fragments = root.accept(new ImportBuilder(template, configuration, namespace), new ArrayList<>());
            fragments.forEach(template.getRootFragment()::addFragment);
            return input;
        } catch (FileNotFoundException e) {
            throw new ParsingException("cannot find import: " + path, ftl);
        } catch (IOException e) {
            throw new ParsingException("cannot read import: " + path, ftl);
        }
    }

    @Override
    public List<Fragment> visit(BrickInstruction ftl, List<Fragment> input) {
        String name = ftl.get(3).toString();
        Fragment optimize = Fragments.optimize(ftl.get(5).accept(this, new ArrayList<>()));
        template.addBrick(name.substring(1, name.length() - 1), optimize);
        input.add(optimize);
        return input;
    }
}
