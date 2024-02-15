package org.freshmarker.core.ftl;

import ftl.FTLConstants.TokenType;
import ftl.Node;
import ftl.Token;
import ftl.ast.IDENTIFIER;
import ftl.ast.ParameterList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterListBuilder implements
    ExpressionVisitor<List<ParameterHolder>, List<ParameterHolder>> {

  private static final Logger log = LoggerFactory.getLogger(ParameterListBuilder.class);

  @Override
  public List<ParameterHolder> visit(Token expression, List<ParameterHolder> input) {
    log.debug("parameters: {}", input);
    input.add(new ParameterHolder(expression.getImage(), null));
    return input;
  }

  @Override
  public List<ParameterHolder> visit(ParameterList expression, List<ParameterHolder> input) {
    int index = 0;
    List<Node> children = expression.children().stream().filter(p -> TokenType.COMMA != p.getTokenType()).toList();
    int maxChildren = children.size();
    Set<String> names = new HashSet<>();
    while (index < maxChildren) {
      log.info("index: {}", index);
      IDENTIFIER identifier = (IDENTIFIER) children.get(index);
      String name = identifier.getImage();
      if (names.contains(name)) {
        throw new ParsingException("non unique parameter name", identifier);
      }
      names.add(name);
      index++;
      if (index >= maxChildren) {
        input.add(new ParameterHolder(name, null));
        break;
      }
      if (children.get(index).getTokenType() == TokenType.EQUALS) {
        index++;
        TemplateObject defaultValue = children.get(index).accept(new InterpolationBuilder(), null);
        input.add(new ParameterHolder(name, defaultValue));
        index++;
      } else if (children.get(index).getTokenType() == TokenType.ELLIPSIS) {
        throw new ParsingException("ellipsis not supported", children.get(index));
      } else {
        input.add(new ParameterHolder(name, null));
      }
    }
    log.debug("parameters: {}", input);
    return input;
  }
}
