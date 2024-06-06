package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.Token;
import ftl.ast.Assignment;
import ftl.ast.Block;
import ftl.ast.CaseInstruction;
import ftl.ast.DefaultInstruction;
import ftl.ast.ElseBlock;
import ftl.ast.ElseIfBlock;
import ftl.ast.FTLHeader;
import ftl.ast.IfStatement;
import ftl.ast.ImportInstruction;
import ftl.ast.Interpolation;
import ftl.ast.ListInstruction;
import ftl.ast.MacroDefinition;
import ftl.ast.NamedArgsList;
import ftl.ast.NestedInstruction;
import ftl.ast.OutputFormatBlock;
import ftl.ast.ReturnInstruction;
import ftl.ast.Root;
import ftl.ast.SettingInstruction;
import ftl.ast.SwitchInstruction;
import ftl.ast.Text;
import ftl.ast.UserDirective;
import ftl.ast.VarInstruction;

public interface UnaryFtlVisitor<T> extends FtlVisitor<T, T> {

  default T visit(Root ftl, T input) {
    for (Node node : ftl.children(true)) {
      node.accept(this, input);
    }
    return input;
  }

  default T visit(Block ftl, T input) {
    for (Node node : ftl.children(true)) {
      node.accept(this, input);
    }
    return input;
  }
}
