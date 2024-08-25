package org.freshmarker;

import java.io.IOException;
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

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
