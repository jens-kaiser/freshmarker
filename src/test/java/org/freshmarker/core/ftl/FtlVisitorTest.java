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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FtlVisitorTest {

    private FtlVisitor<String, String> visitor;

    @BeforeEach
    void setUp() {
        visitor = new FtlVisitor<>() {
            @Override
            public String handleWithException(Node node) {
                return "";
            }
        };
    }

    @Test
    void visitNode() {
        assertEquals("", visitor.visit((Node) null, ""));
    }

    @Test
    void visitToken() {
        assertEquals("", visitor.visit((Token) null, ""));
    }

    @Test
    void visitFTLHeader() {
        assertEquals("", visitor.visit((FTLHeader) null, ""));
    }

    @Test
    void visitRoot() {
        assertEquals("", visitor.visit((Root) null, ""));
    }

    @Test
    void visitBlock() {
        assertEquals("", visitor.visit((Block) null, ""));
    }

    @Test
    void visitText() {
        assertEquals("", visitor.visit((Text) null, ""));
    }

    @Test
    void visitIfStatement() {
        assertEquals("", visitor.visit((IfStatement) null, ""));
    }

    @Test
    void visitElseIfBlock() {
        assertEquals("", visitor.visit((ElseIfBlock) null, ""));
    }

    @Test
    void visitElseBlock() {
        assertEquals("", visitor.visit((ElseBlock) null, ""));
    }

    @Test
    void visitSwitchInstruction() {
        assertEquals("", visitor.visit((SwitchInstruction) null, ""));
    }

    @Test
    void visitCaseInstruction() {
        assertEquals("", visitor.visit((CaseInstruction) null, ""));
    }

    @Test
    void visitDefaultInstruction() {
        assertEquals("", visitor.visit((DefaultInstruction) null, ""));
    }

    @Test
    void visitInterpolation() {
        assertEquals("", visitor.visit((Interpolation) null, ""));
    }

    @Test
    void visitListInstruction() {
        assertEquals("", visitor.visit((ListInstruction) null, ""));
    }

    @Test
    void visitSettingInstruction() {
        assertEquals("", visitor.visit((SettingInstruction) null, ""));
    }

    @Test
    void visitOutputFormatBlock() {
        assertEquals("", visitor.visit((OutputFormatBlock) null, ""));
    }

    @Test
    void visitUserDirective() {
        assertEquals("", visitor.visit((UserDirective) null, ""));
    }

    @Test
    void visitNamedArgsList() {
        assertEquals("", visitor.visit((NamedArgsList) null, ""));
    }

    @Test
    void visitMacroDefinition() {
        assertEquals("", visitor.visit((MacroDefinition) null, ""));
    }

    @Test
    void visitNestedInstruction() {
        assertEquals("", visitor.visit((NestedInstruction) null, ""));
    }

    @Test
    void visitReturnInstruction() {
        assertEquals("", visitor.visit((ReturnInstruction) null, ""));
    }

    @Test
    void visitAssignment() {
        assertEquals("", visitor.visit((Assignment) null, ""));
    }

    @Test
    void visitVarInstruction() {
        assertEquals("", visitor.visit((VarInstruction) null, ""));
    }

    @Test
    void visitImportInstruction() {
        assertEquals("", visitor.visit((ImportInstruction) null, ""));
    }
}