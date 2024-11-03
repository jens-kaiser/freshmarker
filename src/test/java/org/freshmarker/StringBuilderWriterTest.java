package org.freshmarker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringBuilderWriterTest {
    @Test
    public void write() {
        try (StringBuilderWriter writer = new StringBuilderWriter()) {
            writer.write("test");
            writer.write("test".toCharArray(), 0, 4);
            writer.flush();
            assertEquals("testtest", writer.toString());
        }
    }
}