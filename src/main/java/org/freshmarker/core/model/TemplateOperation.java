package org.freshmarker.core.model;

import ftl.FTLConstants.TokenType;
import java.util.Optional;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateOperation implements TemplateObject {

  private final TokenType op;
  private final TemplateObject left;
  private final TemplateObject right;

  public TemplateOperation(TokenType op, TemplateObject left, TemplateObject right) {
    this.op = op;
    this.left = left;
    this.right = right;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    TemplateObject leftValue = left.evaluateToObject(environment);
    TemplateObject rightValue = right.evaluateToObject(environment);
    if (op == TokenType.PLUS) {
      Optional<TemplateString> leftString = leftValue.asString();
      Optional<TemplateString> rightString = rightValue.asString();
      if (leftString.isPresent() && rightString.isPresent()) {
        return leftString.get().concat(rightString.get());
      }
    }
    TemplateNumber leftNumber = leftValue.asNumber().orElse(null);
    TemplateNumber rightNumber = rightValue.asNumber().orElse(null);
    if (leftNumber == null || rightNumber == null) {
      throw new ProcessException("wrong operand types: " + leftValue.getClass() + " " + rightValue.getClass());
    }
    switch (op) {
      case PLUS:
        return leftNumber.add(rightNumber);
      case MINUS:
        return leftNumber.subtract(rightNumber);
      case TIMES:
        return leftNumber.multiply(rightNumber);
      case DIVIDE:
        return leftNumber.divide(rightNumber);
      case PERCENT:
        return leftNumber.modulo(rightNumber);
      default:
        throw new ProcessException("unsupported operation: " + op);
    }
  }
}
