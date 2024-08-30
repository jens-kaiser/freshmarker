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
        return new OutputFormatFragment(content.reduce(context), format);
    }

    @Override
    public int getSize() {
        return content.getSize() + 1;
    }
}
