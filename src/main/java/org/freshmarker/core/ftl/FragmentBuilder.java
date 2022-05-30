package org.freshmarker.core.ftl;

import ftl.FTLConstants.TokenType;
import ftl.Node;
import ftl.Token;
import ftl.ast.Block;
import ftl.ast.FTLHeader;
import ftl.ast.IDENTIFIER;
import ftl.ast.IfStatement;
import ftl.ast.Interpolation;
import ftl.ast.ListInstruction;
import ftl.ast.OutputFormatBlock;
import ftl.ast.Root;
import ftl.ast.STRING_LITERAL;
import ftl.ast.SettingInstruction;
import ftl.ast.SwitchInstruction;
import ftl.ast.Text;
import ftl.ast.UserDirective;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.ConstantFragment;
import org.freshmarker.core.fragment.IfFragment;
import org.freshmarker.core.fragment.InterpolationFragment;
import org.freshmarker.core.fragment.ListFragment;
import org.freshmarker.core.fragment.OutputFormatFragment;
import org.freshmarker.core.fragment.SettingFragment;
import org.freshmarker.core.fragment.SwitchFragment;
import org.freshmarker.core.fragment.UserDirectiveFragment;
import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FragmentBuilder implements FtlVisitor<BlockFragment, BlockFragment> {

  private static final Logger logger = LoggerFactory.getLogger(FragmentBuilder.class);

  private final InterpolationBuilder interpolationBuilder = new InterpolationBuilder();

  @Override
  public BlockFragment visit(Node ftl, BlockFragment input) {
    logger.info("unsupported node operation: {}", ftl.getClass());
    return input;
  }

  private static final ConstantFragment ONE_WHITESPACE = new ConstantFragment(" ");

  @Override
  public BlockFragment visit(Token ftl, BlockFragment input) {
    if (ftl.getType() == TokenType.PRINTABLE_CHARS) {
      input.addFragment(new ConstantFragment(ftl.getImage()));
    } else if (ftl.getType() == TokenType.WHITESPACE) {
      if (" ".equals(ftl.getImage())) {
        input.addFragment(ONE_WHITESPACE);
      } else {
        input.addFragment(new ConstantFragment(ftl.getImage()));
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
    logger.debug("root: {}", ftl);
    for (Node node : ftl.children(true)) {
      node.accept(this, input);
    }
    return input;
  }

  @Override
  public BlockFragment visit(Block ftl, BlockFragment input) {
    logger.debug("block: {}", ftl);
    for (Node node : ftl.children(true)) {
      node.accept(this, input);
    }
    return input;
  }

  @Override
  public BlockFragment visit(Text ftl, BlockFragment input) {
    logger.debug("text: {}", ftl);
    ftl.getAllTokens(false).stream().map(Token::getImage).map(ConstantFragment::new)
        .forEach(input::addFragment);
    return input;
  }

  @Override
  public BlockFragment visit(IfStatement ftl, BlockFragment input) {
    logger.debug("if: {}", ftl);
    input.addFragment(ftl.<Object, IfFragment>accept(new IfFragmentBuilder(), null));
    return input;
  }

  @Override
  public BlockFragment visit(SwitchInstruction ftl, BlockFragment input) {
    logger.debug("switch: {}", ftl);
    input.addFragment(ftl.<Object, SwitchFragment>accept(new SwitchFragmentBuilder(), null));
    return input;
  }

  @Override
  public BlockFragment visit(Interpolation ftl, BlockFragment input) {
    logger.debug("interpolation: {}", ftl);
    TemplateObject interpolation = ftl.getChild(1).accept(interpolationBuilder, null);
    input.addFragment(new InterpolationFragment(new TemplateMarkup(interpolation), ftl));
    return input;
  }

  @Override
  public BlockFragment visit(ListInstruction ftl, BlockFragment input) {
    logger.debug("list: {}", ftl);
    TemplateObject list = ftl.getChild(3).accept(interpolationBuilder, null);
    IDENTIFIER identifier = (IDENTIFIER) ftl.getChild(5);
    BlockFragment block = ftl.getChild(7).accept(this, new BlockFragment());
    input.addFragment(new ListFragment(list, identifier.getImage(), block));
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
    logger.debug("user directive: {}", ftl.children().stream().map(Object::getClass).collect(Collectors.toList()));
    IDENTIFIER directive = (IDENTIFIER) ftl.getChild(1);
    HashMap<String, TemplateObject> namedArgs = new HashMap<>();
    ftl.getChild(2).accept(new NamedArgsBuilder(), namedArgs);
    logger.debug("user directive: {} {}", directive, namedArgs);
    Node node =  ftl.children().stream().skip(2)
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
}
