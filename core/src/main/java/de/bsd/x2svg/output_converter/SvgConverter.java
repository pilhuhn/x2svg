/**
 *
 */
package de.bsd.x2svg.output_converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import de.bsd.x2svg.Messages;
import de.bsd.x2svg.util.SantasLittleHelper;

/**
 * Convert SVG files into various output file types. The supported
 * types are encoded in {@link OutputType}
 * @author hwr@pilhuhn.de
 * @since 1.1
 * @see SVGTranscoderFactory
 * @see OutputType
 */
public class SvgConverter {

    /**
     * Convert the passed SVG input file into the passed output file
     * @param type A valid OutputType
     * @param input A valid input file containing SVG data
     * @param output An output file to write the result to
     * @see OutputType
     * @throws ConversionException if anything in the conversion process goes wrong
     */
    public void convert(OutputType type, File input, File output) throws ConversionException {
        try (InputStream inputStream = new FileInputStream(input);
             OutputStream outputStream = new FileOutputStream(output);)
        {
            final SVGAbstractTranscoder tc = SVGTranscoderFactory.getTranscoderForOutputType(type);
            final TranscoderInput ti = new TranscoderInput(inputStream);
            final TranscoderOutput to = new TranscoderOutput(outputStream);
            tc.transcode(ti, to);
        } catch (TranscoderException e) {
            throw new ConversionException(e.getLocalizedMessage());
        } catch (IOException e) {
            throw new ConversionException(e.getLocalizedMessage());
        }
    }

    /**
     * Convert the passed SVG input file into the passed output file
     * @param type a string representing a valid OutputType
     * @param input A valid input file containing SVG data
     * @param output An output file to write the result to
     * @see OutputType
     * @throws ConversionException if anything in the conversion process goes wrong
     */
    public void convert(String type, File input, File output) throws ConversionException {
        OutputType ot = OutputType.valueOf(type.toUpperCase());
        this.convert(ot, input, output);
    }


    /**
     * Convert the SVG file passed in as the second argument into
     * an output file given in the third argument and of the type
     * passed in the first argument. An output directory can be set
     * in the optional fourth parameter
     * @param args Input parameters: type(s) input.svg output[.type] [output dir]
     * @throws Exception if anything goes wrong
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println(Messages.getString("SvgConverter.0")); //$NON-NLS-1$
            System.out.println(Messages.getString("SvgConverter.1")); //$NON-NLS-1$
            System.out.print(Messages.getString("SvgConverter.2") + " "); //$NON-NLS-1$
            for (OutputType type : OutputType.values()) {
                System.out.print(type.toString().toLowerCase() + " "); //$NON-NLS-1$
            }
            return;
        }
        String types = args[0].toUpperCase();
        SvgConverter sc = new SvgConverter();
        String dir = null;
        if (args.length == 4) {
            dir = args[3];
            dir += "/"; //$NON-NLS-1$
        }
        String[] typeArray = types.split(":"); //$NON-NLS-1$
        File input = new File(args[1]);
        for (String type : typeArray) {
            String fileName = dir;
            fileName += args[2];
            fileName = SantasLittleHelper.attachSuffixToFileName(fileName, type.toLowerCase());
            File output = new File(fileName);
            sc.convert(type, input, output);
        }
    }
}
