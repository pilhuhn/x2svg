package de.bsd.x2svg.outputConverter;

import java.util.Locale;

/**
 * An enumeration of output formats that we support.
 * 
 * @author hwr@pilhuhn.de
 */
public enum OutputType 
{
    
	// Vector formats
	PDF,
	EPS,
	// Pixel formats
	PNG,
	TIFF,
	JPG;
 
    
    /** 
     * The locale used to convert a type name to lowercase, en_US is
     * safe here because all the originating characters are ASCII
     * equivalent, so there is no need to consider changing case of
     * special characters.  */
	private static final Locale US_LOCALE = new Locale("en", "US");

    
    /**
     * Get a <code>String</code> that represents the correct filename
     * extension for this type.
     * 
     * @return The appropriate filename extension equivalent for this type.
     */
    public String asFileExtension()
    {
        return this.name().toLowerCase(US_LOCALE);
    }
    
}