package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.ReduceException;
import org.freshmarker.core.WrongTypeException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class TryFragment implements Fragment {
    private final Fragment block;
    private final Fragment except;

    public TryFragment(Fragment block, Fragment except) {
        this.block = block;
        this.except = except;
    }

    @Override
    public void process(ProcessContext context) {
        Writer oldWriter = context.getWriter();
        StringWriter writer = new StringWriter();
        context.setWriter(writer);
        try {
            block.process(context);
            oldWriter.write(writer.toString());
            return;
        } catch (RuntimeException e) {
            // ignore
        } catch (IOException e) {
            throw new ProcessException(e.getMessage(), e);
        } finally {
            context.setWriter(oldWriter);
        }
        except.process(context);
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        Fragment newBlock = reduce(block, context);
        Fragment newExcept = reduce(except, context);
        if (newBlock == block && newExcept == except) {
            return this;
        }
        return new TryFragment(newBlock, newExcept);
    }

    private Fragment reduce(Fragment fragment, ReduceContext context) {
        try {
            return fragment.reduce(context);
        } catch (WrongTypeException e) {
            throw new ReduceException(e.getMessage(), e);
        } catch (ProcessException e) {
            return fragment;
        }
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
