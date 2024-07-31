package org.freshmarker.core.fragment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.environment.SettingEnvironment;
import org.freshmarker.core.environment.Settings;

import java.util.Map;

public class OutputFormatFragment implements Fragment {

    private final BlockFragment content;
    private final String format;

    public OutputFormatFragment(BlockFragment content, String format) {
        this.content = content;
        this.format = format;
    }

    @Override
    public void process(ProcessContext context) {
        Environment environment = context.getEnvironment();
        context.setEnvironment(new SettingEnvironment(environment, new Settings(null, null, context.getOutputFormat(format), Map.of())));
        try {
            content.process(context);
        } finally {
            context.setEnvironment(environment);
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
