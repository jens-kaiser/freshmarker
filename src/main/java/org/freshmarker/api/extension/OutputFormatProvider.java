package org.freshmarker.api.extension;

import org.freshmarker.api.OutputFormat;

import java.util.Map;

/**
 * An {@link Extension} to add new output formats.
 */
public interface OutputFormatProvider extends Extension {
    /**
     * Returns a map of output formats
     * @return a map of output formats
     */
    Map<String, OutputFormat> provideOutputFormats();
}
