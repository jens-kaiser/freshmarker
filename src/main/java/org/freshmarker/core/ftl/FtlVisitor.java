package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token;
import ftl.ast.Block;
import ftl.ast.CaseInstruction;
import ftl.ast.DefaultInstruction;
import ftl.ast.ElseBlock;
import ftl.ast.ElseIfBlock;
import ftl.ast.FTLHeader;
import ftl.ast.IfStatement;
import ftl.ast.Interpolation;
import ftl.ast.ListInstruction;
import ftl.ast.Root;
import ftl.ast.SettingInstruction;
import ftl.ast.SwitchInstruction;
import ftl.ast.Text;

public interface FtlVisitor<I, O> {

  default O visit(Node ftl, I input) {
    return null;
  }

  default O visit(Token ftl, I input) {
    return null;
  }

  default O visit(FTLHeader ftl, I input) {
    return null;
  }

  default O visit(Root ftl, I input) {
    return null;
  }

  default O visit(Block ftl, I input) {
    return null;
  }

  default O visit(Text ftl, I input) {
    return null;
  }

  default O visit(IfStatement ftl, I input) {
    return null;
  }

  default O visit(ElseIfBlock ftl, I input) {
    return null;
  }

  default O visit(ElseBlock ftl, I input) {
    return null;
  }

  default O visit(SwitchInstruction ftl, I input) {
    return null;
  }

  default O visit(CaseInstruction ftl, I input) {
    return null;
  }

  default O visit(DefaultInstruction ftl, I input) {
    return null;
  }

  default O visit(Interpolation ftl, I input) {
    return null;
  }

  default O visit(ListInstruction ftl, I input) {
    return null;
  }

  default O visit (SettingInstruction ftl, I input) { return null; }
}
