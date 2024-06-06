package org.freshmarker.core.ftl;

import ftl.Node;
import ftl.ast.Block;
import ftl.ast.Root;

public interface UnaryFtlVisitor<T> extends FtlVisitor<T, T> {

    @Override
    default T visit(Root ftl, T input) {
        for (Node node : ftl.children(true)) {
            node.accept(this, input);
        }
        return input;
    }

    @Override
    default T visit(Block ftl, T input) {
        for (Node node : ftl.children(true)) {
            node.accept(this, input);
        }
        return input;
    }
}
