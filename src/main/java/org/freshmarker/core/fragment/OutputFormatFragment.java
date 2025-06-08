package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;

public class OutputFormatFragment implements Fragment {

    private final Fragment content;
    private final String format;

    public OutputFormatFragment(Fragment content, String format) {
        this.content = content;
        this.format = format;
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
        return new OutputFormatFragment(reduced, format);
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
