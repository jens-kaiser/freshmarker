package org.freshmarker.core.model;

import ftl.Token.TokenType;
import java.util.Optional;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateOperation implements TemplateExpression {

  private final TokenType op;
  private final TemplateObject left;
  private final TemplateObject right;

  public TemplateOperation(TokenType op, TemplateObject left, TemplateObject right) {
    this.op = op;
    this.left = left;
    this.right = right;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext processContext) {
    TemplateObject leftValue = left.evaluateToObject(processContext);
    TemplateObject rightValue = right.evaluateToObject(processContext);
    if (op == TokenType.PLUS) {
      Optional<TemplateString> leftString = leftValue.asString();
      Optional<TemplateString> rightString = rightValue.asString();
      if (leftString.isPresent() && rightString.isPresent()) {
        return leftString.get().concat(rightString.get());
      }
    }
    TemplateNumber leftNumber = leftValue.evaluate(processContext, TemplateNumber.class);
    TemplateNumber rightNumber = rightValue.evaluate(processContext, TemplateNumber.class);
      return switch (op) {
          case PLUS -> leftNumber.add(rightNumber);
          case MINUS -> leftNumber.subtract(rightNumber);
          case TIMES -> leftNumber.multiply(rightNumber);
          case DIVIDE -> leftNumber.divide(rightNumber);
          case PERCENT -> leftNumber.modulo(rightNumber);
          default -> throw new ProcessException("unsupported operation: " + op);
      };
  }
}
