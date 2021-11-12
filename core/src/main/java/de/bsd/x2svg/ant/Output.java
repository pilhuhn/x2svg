package de.bsd.x2svg.ant;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.types.DataType;

import de.bsd.x2svg.outputConverter.OutputType;

/**
 * The output type, that acts as a wrapper for a collection of output formats.
 *
 * @author gfloodgate
 * @since 1.1
 */
public class Output extends DataType {

    /**
     * The collection of formats to output.
     */
    private final Set<OutputType> types = new HashSet<OutputType>();


    /**
     * Add an output format to the format collection.
     *
     * @param type The <code>format</code> ant type to add.
     */
    public void addConfiguredFormat(final Format type) {
        final OutputType outputType = type.getOutputType();
        if (outputType != null) {
            types.add(outputType);
        }
    }


    /**
     * Get the set of output formats in this collection.
     *
     * @return A read-only set of output format types in this conversion collection.
     */
    public Set<OutputType> getOutputTypes() {
        return Collections.unmodifiableSet(types);
    }

}
