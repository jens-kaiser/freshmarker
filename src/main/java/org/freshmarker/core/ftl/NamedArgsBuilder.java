package org.freshmarker.core.ftl;

import ftl.FTLConstants.TokenType;
import ftl.Token;
import ftl.ast.IDENTIFIER;
import ftl.ast.NamedArgsList;
import java.util.Map;
import org.freshmarker.core.model.TemplateObject;

public class NamedArgsBuilder implements FtlVisitor<Map<String, TemplateObject>, Void> {
  private final InterpolationBuilder interpolationBuilder;

  public NamedArgsBuilder( InterpolationBuilder interpolationBuilder) {
    this.interpolationBuilder = interpolationBuilder;
  }

  @Override
  public Void visit(Token ftl, Map<String, TemplateObject> input) {
    return null;
  }

  @Override
  public Void visit(NamedArgsList ftl, Map<String, TemplateObject> input) {
    int i = 0;
    while (i < ftl.getChildCount()) {
      if (ftl.getChild(i).getTokenType() == TokenType.COMMA) {
        i++;
      }
      IDENTIFIER key = (IDENTIFIER) ftl.getChild(i);
      TemplateObject value = ftl.getChild(i + 2).accept(interpolationBuilder, null);
      input.put(key.getImage(), value);
      i += 3;
    }
    return null;
  }
}
