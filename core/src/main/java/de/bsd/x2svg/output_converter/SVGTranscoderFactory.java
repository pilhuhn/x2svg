/**
 *
 */
package de.bsd.x2svg.output_converter;

import java.awt.Color;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.apache.fop.render.ps.EPSTranscoder;
import org.apache.fop.svg.PDFTranscoder;

/**
 * Factory that returns transcoders that can be used to
 * convert from SVG into the respective target format
 * @author hwr@pilhuhn.de
 *
 */
public class SVGTranscoderFactory {

    /**
     * This class should not be instantiated
     */
    private SVGTranscoderFactory() {
        // intentionally left blank
    }

    /**
     * Obtain a transcoder for the passed output type. Only types encoded in
     * {@link OutputType} are supported.
     * @param type Type of output to generate
     * @return a transcoder for the given type
     */
    static SVGAbstractTranscoder getTranscoderForOutputType(OutputType type) {

        SVGAbstractTranscoder tc = null;

        switch (type) {
            case PNG:
                tc = new PNGTranscoder();
                tc.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);
                break;
            case PDF:
                tc = new PDFTranscoder();
                break;
            case TIFF:
                tc = new TIFFTranscoder();
                break;
            case JPG:
                tc = new JPEGTranscoder();
                break;
            case EPS:
                tc = new EPSTranscoder();
                break;
        }

        return tc;
    }


    /**
     * Obtain a transcoder for the passed output type
     * @param type Type of output to generate
     * @return a transcoder for the given type
     */
    protected static SVGAbstractTranscoder getTranscoderForOutputType(String type) {
        OutputType ot = OutputType.valueOf(type.toUpperCase());
        return getTranscoderForOutputType(ot);
    }
}
