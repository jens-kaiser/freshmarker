package org.freshmarker;

import ftl.FTLConstants.TokenType;
import ftl.Node;
import ftl.Token;
import ftl.ast.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

class TokenLineNormalizer {

  private static final Set<TokenType> NON_TAG_TOKEN_TYPES = Set.of(TokenType.INTERPOLATE, TokenType.PRINTABLE_CHARS);

  private boolean containNonTag;
  private Token first;

  public void normalize(Root root) {
    root.getAllTokens(false).forEach(this::normalizeWhitespaces);
  }

  private void cleanUpLine(Token whitespaceToken) {
    if (!whitespaceToken.getImage().endsWith("\n")) {
      addWhitespaceTokenToLine(whitespaceToken);
      return;
    }
    if (first == null || containNonTag) {
      clearLine();
      return;
    }
    getFirstAsWhitespace().ifPresent(f -> f.getParent().removeChild(f));
    whitespaceToken.getParent().removeChild(whitespaceToken);
    clearLine();
  }

  private void normalizeWhitespaces(Token token) {
    if (token.getType() != TokenType.WHITESPACE) {
      addNonWhitespaceTokenToLine(token);
      return;
    }
    String image = token.getImage();
    int index = image.indexOf("\n");
    if (index == -1 || index == image.length() - 1) {
      cleanUpLine(token);
      return;
    }
    int beginOffset = token.getBeginOffset();
    int endOffset = token.getEndOffset();
    Node parent = token.getParent();
    int tokenIndex = parent.indexOf(token);
    parent.removeChild(token);
    int start = beginOffset;
    List<Token> list = new ArrayList<>();
    for (int i = 0; i < image.length(); i++) {
      char c = image.charAt(i);
      if (c == '\n') {
        Token newToken = newToken(token, start, beginOffset + i + 1);
        list.add(newToken);
        parent.addChild(tokenIndex, newToken);
        tokenIndex++;
        start = beginOffset + i + 1;
      }
    }
    if (start <= endOffset) {
      Token newToken = newToken(token, start, endOffset);
      list.add(newToken);
      parent.addChild(tokenIndex, newToken);
    }
    list.forEach(this::cleanUpLine);
  }

  private void clearLine() {
    first = null;
    containNonTag = false;
  }

  private void addWhitespaceTokenToLine(Token token) {
    containNonTag = first != null;
    first = first == null ? token : first;
  }

  private void addNonWhitespaceTokenToLine(Token token) {
    containNonTag = containNonTag || NON_TAG_TOKEN_TYPES.contains(token.getType());
    first = first == null ? token : first;
  }

  private Optional<Token> getFirstAsWhitespace() {
    return Optional.ofNullable(first).filter(f -> f.getType() == TokenType.WHITESPACE);
  }

  private Token newToken(Token token, int beginOffset, int endOffset) {
    return Token.newToken(TokenType.WHITESPACE, token.getTokenSource(), beginOffset, endOffset);
  }
}
