package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;

import java.io.IOException;
import java.util.Objects;

public class ConstantFragment implements Fragment {

    public static final ConstantFragment EMPTY = new ConstantFragment("");

    private String value;

    public ConstantFragment(String value) {
        this.value = value;
    }

    public void add(String value) {
        this.value += value;
    }

    @Override
    public void process(ProcessContext context) {
        try {
            context.getWriter().write(value);
        } catch (IOException e) {
            throw new ProcessException(e.getMessage(), e);
        }
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this, value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConstantFragment that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
