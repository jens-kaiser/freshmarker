package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.ast.ElseBlock;
import ftl.ast.ElseIfBlock;
import ftl.ast.IfStatement;
import org.freshmarker.core.fragment.ConditionalFragment;
import org.freshmarker.core.fragment.ConstantFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.Fragments;
import org.freshmarker.core.fragment.IfFragment;
import org.freshmarker.core.model.TemplateObject;

import java.util.ArrayList;
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
        Fragment ifBlock;
        if (indexAfterIfBlock(ftl) == 5) {
            ifBlock = ConstantFragment.EMPTY;
        } else {
            ifBlock = Fragments.optimize(ftl.getChild(5).accept(fragmentBuilder, new ArrayList<>()));
        }
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
        Fragment ifBlock;
        if (ftl.getChildCount() == 5) {
            ifBlock = ConstantFragment.EMPTY;
        } else {
            List<Fragment> fragments = ftl.getChild(5).accept(fragmentBuilder, new ArrayList<>());
            ifBlock = Fragments.optimize(fragments);
        }
        input.addFragment(new ConditionalFragment(ifExpression, ifBlock, expression));
        return input;
    }

    @Override
    public IfFragment visit(ElseBlock ftl, IfFragment input) {
        if (ftl.getChildCount() != 3) {
            Node expression = ftl.getChild(3);
            List<Fragment> fragments = expression.accept(fragmentBuilder, new ArrayList<>());
            input.addElseFragment(Fragments.optimize(fragments));
        }
        return input;
    }
}
