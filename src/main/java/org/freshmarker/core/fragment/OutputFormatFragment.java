package org.freshmarker.core.fragment;

import ftl.ast.OutputFormatBlock;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;

public class OutputFormatFragment implements Fragment {

    private final Fragment content;
    private final String format;
    private final OutputFormatBlock ftl;

    public OutputFormatFragment(Fragment content, String format, OutputFormatBlock ftl) {
        this.content = content;
        this.format = format;
        this.ftl = ftl;
    }

    @Override
    public void process(ProcessContext context) {
        context.pushOutputFormat(context.getOutputFormat(format));
        try {
            content.process(context);
        } finally {
            context.pullOutputFormat();
        }
    }

    @Override
    public OutputFormatFragment reduce(ReduceContext context) {
        Fragment reduced = content.reduce(context);
        if (content == reduced) {
            return this;
        }
        context.getStatus().replaced().incrementAndGet();
        return new OutputFormatFragment(reduced, format, ftl);
    }

    @Override
    public int getSize() {
        return content.getSize() + 1;
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this, format, content);
    }
}
