package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.ast.ElseBlock;
import ftl.ast.ElseIfBlock;
import ftl.ast.IfStatement;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.ConditionalFragment;
import org.freshmarker.core.fragment.IfFragment;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;

class IfFragmentBuilder implements FtlVisitor<IfFragment, IfFragment> {

    private final FragmentBuilder fragmentBuilder;

    IfFragmentBuilder(FragmentBuilder fragmentBuilder) {
        this.fragmentBuilder = fragmentBuilder;
    }

    @Override
    public IfFragment visit(IfStatement ftl, IfFragment input) {
        Node expression = ftl.getChild(3);
        TemplateObject ifExpression = expression.accept(InterpolationBuilder.INSTANCE, null);
        BlockFragment ifBlock = indexAfterIfBlock(ftl) == 5 ? new BlockFragment() : ftl.getChild(5).accept(fragmentBuilder, new BlockFragment());
        IfFragment ifFragment = new IfFragment();
        ifFragment.addFragment(new ConditionalFragment(ifExpression, ifBlock, expression));
        ftl.childrenOfType(ElseIfBlock.class).forEach(elseIfPart -> elseIfPart.accept(this, ifFragment));
        ElseBlock elsePart = ftl.firstChildOfType(ElseBlock.class);
        if (elsePart != null) {
            elsePart.accept(this, ifFragment);
        }
        return ifFragment;
    }

    private static Integer indexAfterIfBlock(IfStatement ftl) {
        return ftl.children().stream().skip(5).filter(n -> List.of(ElseIfBlock.class, ElseBlock.class).contains(n.getClass())).map(ftl::indexOf)
                .findFirst().orElse(ftl.getChildCount());
    }

    @Override
    public IfFragment visit(ElseIfBlock ftl, IfFragment input) {
        Node expression = ftl.getChild(3);
        TemplateObject ifExpression = expression.accept(InterpolationBuilder.INSTANCE, null);
        BlockFragment ifBlock = ftl.getChildCount() == 5 ? new BlockFragment() : ftl.getChild(5).accept(fragmentBuilder, new BlockFragment());
        input.addFragment(new ConditionalFragment(ifExpression, ifBlock, expression));
        return input;
    }

    @Override
    public IfFragment visit(ElseBlock ftl, IfFragment input) {
        if (ftl.getChildCount() != 3) {
            Node expression = ftl.getChild(3);
            input.addElseFragment(expression.accept(fragmentBuilder, new BlockFragment()));
        }
        return input;
    }
}
