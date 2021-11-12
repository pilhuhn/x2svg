/**
 *
 */
package de.bsd.x2svg.outputConverter;

/**
 * This exception is thrown if the conversion to the
 * desired output format failed
 * @author hwr@pilhuhn.de
 *
 */
public class ConversionException extends Exception {
    /** serial Version UUID	 */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param reason why did the conversion fail?
     */
    ConversionException(String reason) {
        super(reason);
    }
}
