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

  private static final Set<TokenType> NON_TAG_TOKEN_TYPES = Set.of(TokenType.WHITESPACE, TokenType.INTERPOLATE,
      TokenType.PRINTABLE_CHARS);

  private boolean containNonTag;
  private boolean firstNonTag;
  private Token first;

  private void clean() {
    first = null;
    containNonTag = false;
    firstNonTag = false;
  }

  public void normalize(Root root) {
    List<Token> allTokens = root.getAllTokens(false);
    allTokens.stream().map(this::normalizeWhitespaces).flatMap(List::stream).forEach(this::cleanUpLine);
  }

  private void addToken(Token token) {
    if (first == null) {
      first = token;
      firstNonTag = token.getType() == TokenType.PRINTABLE_CHARS || token.getType() == TokenType.INTERPOLATE;
      return;
    }
    containNonTag = containNonTag || NON_TAG_TOKEN_TYPES.contains(token.getType());
  }

  private Optional<Token> getFirstAsWhitespace() {
    return Optional.ofNullable(first).filter(f -> f.getType() == TokenType.WHITESPACE);
  }

  private void cleanUpLine(Token token) {
    if (token.getType() != TokenType.WHITESPACE || !token.getImage().endsWith("\n")) {
      addToken(token);
      return;
    }
    if (first == null || firstNonTag || containNonTag) {
      clean();
      return;
    }
    getFirstAsWhitespace().ifPresent(f -> f.getParent().removeChild(f));
    token.getParent().removeChild(token);
    clean();
  }

  private List<Token> normalizeWhitespaces(Token token) {
    if (token.getType() != TokenType.WHITESPACE) {
      return List.of(token);
    }
    String image = token.getImage();
    int index = image.indexOf("\n");
    if (index == -1 || index == image.length() - 1) {
      return List.of(token);
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
    return list;
  }

  private Token newToken(Token token, int beginOffset, int endOffset) {
    return Token.newToken(TokenType.WHITESPACE, token.getTokenSource(), beginOffset, endOffset);
  }
}
