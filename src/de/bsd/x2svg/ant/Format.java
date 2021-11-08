package de.bsd.x2svg.ant;

import java.util.Locale;

import org.apache.tools.ant.types.DataType;

import de.bsd.x2svg.outputConverter.OutputType;

/**
 * An ant data type to wrapper an X2Svg <code>OutputType</code> object.
 * 
 * @author gfloodgate
 * @since 1.1
 */
public class Format extends DataType
{

    /** A locale used for uppercase conversion - en_US is safe, as no non-ASCII characters are expected. */
    private static final Locale US_LOCALE = new Locale("en", "US");
    
    
    /** The type attribute of the format to use. */
    private String type;

    
    /**
     * Get the output format requested.
     * 
     * @return The string type of the requested format.
     */
    public String getType()
    {
        return type;
    }

    
    /**
     * Set the requested output format.
     * 
     * @param type The string type of the requested format.
     */
    public void setType(final String type)
    {
        this.type = type;
    }

    
    /**
     * Get the <code>OutputType</code> instance representing the format.
     * 
     * @return The <code>OutputType</code> representing the requested otuput format,
     *         or null if the specified type is invalid or blank.
     */
    public OutputType getOutputType()
    {
        for (OutputType outputType : OutputType.values())
        {
            if (type != null && outputType.name().equals(type.toUpperCase(US_LOCALE)))
            {
                return outputType;
            }
        }
        
        log("Ignoring unknown output format type - " + type);
        return null;
    }
    
}
