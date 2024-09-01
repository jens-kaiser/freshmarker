package org.freshmarker;

import java.io.Writer;

public class StringBuilderWriter extends Writer {
    private final StringBuilder builder = new StringBuilder();

    @Override
    public void write(char[] cbuf, int off, int len) {
        builder.append(cbuf, off, len);
    }

    @Override
    public void write(String s) {
        builder.append(s);
    }

    @Override
    public void flush() {
        // intentionally left blank
    }

    @Override
    public void close() {
        // intentionally left blank
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
