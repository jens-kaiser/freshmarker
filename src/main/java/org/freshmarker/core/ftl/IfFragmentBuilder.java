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
    private final InterpolationBuilder interpolationBuilder;

    IfFragmentBuilder(FragmentBuilder fragmentBuilder, InterpolationBuilder interpolationBuilder) {
        this.fragmentBuilder = fragmentBuilder;
        this.interpolationBuilder = interpolationBuilder;
    }

    @Override
    public IfFragment visit(IfStatement ftl, IfFragment input) {
        Node expression = ftl.get(3);
        TemplateObject ifExpression = expression.accept(interpolationBuilder, null);
        Fragment ifBlock;
        if (indexAfterIfBlock(ftl) == 5) {
            ifBlock = ConstantFragment.EMPTY;
        } else {
            ifBlock = Fragments.optimizeWithVariableContext(ftl.get(5).accept(fragmentBuilder, new ArrayList<>()));
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
                .findFirst().orElse(ftl.size());
    }

    @Override
    public IfFragment visit(ElseIfBlock ftl, IfFragment input) {
        Node expression = ftl.get(3);
        TemplateObject ifExpression = expression.accept(interpolationBuilder, null);
        Fragment ifBlock;
        if (ftl.size() == 5) {
            ifBlock = ConstantFragment.EMPTY;
        } else {
            ifBlock = Fragments.optimizeWithVariableContext(ftl.get(5).accept(fragmentBuilder, new ArrayList<>()));
        }
        input.addFragment(new ConditionalFragment(ifExpression, ifBlock, expression));
        return input;
    }

    @Override
    public IfFragment visit(ElseBlock ftl, IfFragment input) {
        if (ftl.size() != 3) {
            Node expression = ftl.get(3);
            List<Fragment> fragments = expression.accept(fragmentBuilder, new ArrayList<>());
            input.addElseFragment(Fragments.optimizeWithVariableContext(fragments));
        }
        return input;
    }
}
