package org.freshmarker.core.ftl;

import ftl.FTLConstants.TokenType;
import ftl.Node;
import ftl.Token;
import ftl.ast.Assignment;
import ftl.ast.Block;
import ftl.ast.FTLHeader;
import ftl.ast.IDENTIFIER;
import ftl.ast.IfStatement;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.directive.MacroUserDirective;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.ConstantFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.IfFragment;
import org.freshmarker.core.fragment.InterpolationFragment;
import org.freshmarker.core.fragment.ListFragment;
import org.freshmarker.core.fragment.NestedInstructionFragment;
import org.freshmarker.core.fragment.OutputFormatFragment;
import org.freshmarker.core.fragment.ReturnInstructionFragment;
import org.freshmarker.core.fragment.SettingFragment;
import org.freshmarker.core.fragment.SwitchFragment;
import org.freshmarker.core.fragment.UserDirectiveFragment;
import org.freshmarker.core.fragment.VariableFragment;
import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FragmentBuilder implements FtlVisitor<BlockFragment, BlockFragment> {

  private static final Logger logger = LoggerFactory.getLogger(FragmentBuilder.class);

  private final InterpolationBuilder interpolationBuilder = new InterpolationBuilder();

  private final Template template;

  public FragmentBuilder(Template template) {
    this.template = template;
  }

  @Override
  public BlockFragment visit(Node ftl, BlockFragment input) {
    logger.info("unsupported node operation: {}", ftl.getClass());
    return input;
  }

  private static final ConstantFragment ONE_WHITESPACE = new ConstantFragment(" ");

  @Override
  public BlockFragment visit(Token ftl, BlockFragment input) {
    String image = ftl.getImage();
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
  public BlockFragment visit(Text ftl, BlockFragment input) {
    ftl.getAllTokens(false).stream().map(Token::getImage).map(ConstantFragment::new).forEach(input::addFragment);
    return input;
  }

  @Override
  public BlockFragment visit(IfStatement ftl, BlockFragment input) {
    input.addFragment(ftl.<Object, IfFragment>accept(new IfFragmentBuilder(this), null));
    return input;
  }

  @Override
  public BlockFragment visit(SwitchInstruction ftl, BlockFragment input) {
    input.addFragment(ftl.<Object, SwitchFragment>accept(new SwitchFragmentBuilder(this), null));
    return input;
  }

  @Override
  public BlockFragment visit(Interpolation ftl, BlockFragment input) {
    TemplateObject interpolation = ftl.getChild(1).accept(interpolationBuilder, null);
    input.addFragment(new InterpolationFragment(new TemplateMarkup(interpolation), ftl));
    return input;
  }

  @Override
  public BlockFragment visit(ListInstruction ftl, BlockFragment input) {
    TemplateObject list = ftl.getChild(3).accept(interpolationBuilder, null);
    String identifier = ((IDENTIFIER) ftl.getChild(5)).getImage();
    int blockIndex = ftl.getChild(6).getTokenType() == TokenType.COMMA ? 9 : 7;
    String looperIdentifier = blockIndex == 9 ? ((IDENTIFIER) ftl.getChild(7)).getImage() : null;
    BlockFragment block = ftl.getChild(blockIndex).accept(this, new BlockFragment());
    input.addFragment(new ListFragment(list, identifier, looperIdentifier, block));
    return input;
  }

  @Override
  public BlockFragment visit(SettingInstruction ftl, BlockFragment input) {
    IDENTIFIER identifier = (IDENTIFIER) ftl.getChild(3);
    TemplateObject expression = ftl.getChild(5).accept(interpolationBuilder, null);
    input.addFragment(new SettingFragment(identifier.getImage(), expression));
    return input;
  }

  @Override
  public BlockFragment visit(OutputFormatBlock ftl, BlockFragment input) {
    STRING_LITERAL format = (STRING_LITERAL) ftl.getChild(3);
    BlockFragment block = ftl.getChild(5).accept(this, new BlockFragment());
    String image = format.getImage();
    input.addFragment(new OutputFormatFragment(block, image.substring(1, image.length() - 1)));
    return input;
  }

  @Override
  public BlockFragment visit(UserDirective ftl, BlockFragment input) {
    IDENTIFIER directive = (IDENTIFIER) ftl.getChild(1);
    HashMap<String, TemplateObject> namedArgs = new HashMap<>();
    ftl.getChild(2).accept(new NamedArgsBuilder(), namedArgs);
    logger.debug("user directive: {} {}", directive, namedArgs);
    Node node = ftl.children().stream().skip(2)
        .dropWhile(
            n -> n.getTokenType() == null || !Set.of(TokenType.GT, TokenType.CLOSE_TAG).contains(n.getTokenType()))
        .skip(1).findFirst().orElse(null);
    BlockFragment body = null;
    if (node != null) {
      body = node.accept(this, new BlockFragment());
    }
    logger.debug("user directive: {} {}", node, body);
    input.addFragment(new UserDirectiveFragment(directive.getImage(), namedArgs, body));
    return input;
  }

  @Override
  public BlockFragment visit(MacroDefinition ftl, BlockFragment input) {
    TokenType type = ftl.getChild(1).getTokenType();
    if (type != TokenType.MACRO) {
      return input;
    }
    String name = getName(ftl.getChild(3));
    List<ParameterHolder> parameterList = getParameterHolders(ftl);
    Fragment block = getFragment(ftl);
    logger.debug("type={}, name={}, block={}", type, name, block);
    template.getUserDirectives().put(name, new MacroUserDirective(block, parameterList));
    return input;
  }

  private Fragment getFragment(MacroDefinition ftl) {
    if (ftl.getChild(ftl.getChildCount() - 1).getTokenType() == TokenType.CLOSE_EMPTY_TAG
        || ftl.getChild(ftl.getChildCount() - 2).getTokenType() == TokenType.CLOSE_TAG) {
      return ConstantFragment.EMPTY;
    }
    return ftl.getChild(ftl.getChildCount() - 2).accept(this, new BlockFragment());
  }

  private List<ParameterHolder> getParameterHolders(MacroDefinition ftl) {
    int parameterListIndex = getParameterListIndex(ftl);
    if (ftl.getChild(parameterListIndex).getTokenType() == TokenType.CLOSE_TAG) {
      return Collections.emptyList();
    }
    return ftl.getChild(parameterListIndex).accept(new ParameterListBuilder(), new ArrayList<ParameterHolder>());
  }

  private int getParameterListIndex(MacroDefinition ftl) {
    if (ftl.getChild(4).getTokenType() != TokenType.OPEN_PAREN) {
      return 4;
    }
    if (ftl.getChild(6).getTokenType() != TokenType.CLOSE_PAREN) {
      throw new ProcessException("missing CLOSE_PAREN at " + ftl.getChild(6).getLocation());
    }
    return 5;
  }

  private String getName(Node node) {
    if (node.getTokenType() == TokenType.IDENTIFIER) {
      return ((IDENTIFIER) node).getImage();
    }
    if (node.getTokenType() == TokenType.STRING_LITERAL) {
      String image = ((STRING_LITERAL) node).getImage();
      return image.substring(1, image.length() - 1);
    }
    throw new ParsingException("missing identifier or string literal at " + node.getChild(6).getLocation());
  }


  @Override
  public BlockFragment visit(Assignment ftl, BlockFragment input) {
    TokenType type = ftl.getChild(1).getTokenType();
    if (type != TokenType.SET) {
      throw new ParsingException("assignment type " + type + " not supported");
    }
    String name = getName(ftl.getChild(3));
    if (ftl.getChildCount() != 7) {
      throw new ParsingException("only one assignment supported");
    }
    input.addFragment(new VariableFragment(name, ftl.getChild(5).accept(interpolationBuilder, null), true));
    return input;
  }

  @Override
  public BlockFragment visit(VarInstruction ftl, BlockFragment input) {
    String name = getName(ftl.getChild(3));
    if (ftl.getChildCount() != 7) {
      throw new ParsingException("only one assignment supported");
    }
    input.addFragment(new VariableFragment(name, ftl.getChild(5).accept(interpolationBuilder, null), false));
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
}
