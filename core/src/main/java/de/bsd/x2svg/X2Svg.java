/*******************************************************************************
 * Copyright (c) 2007 Heiko W. Rupp. 	All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package de.bsd.x2svg;

import java.util.Arrays;

import de.bsd.x2svg.output_converter.OutputFormat;
import de.bsd.x2svg.output_converter.OutputType;
import de.bsd.x2svg.util.SantasLittleHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Main class of X2Svg. This is called from the command line.
 *
 * @author hwr@pilhuhn.de
 */
public class X2Svg {

    private final Log log = LogFactory.getLog(X2Svg.class);
    /**
     * Main - to be called to start the show
     *
     * @param args command line arguments
     * @throws Exception if anything goes wrong
     */
    public static void main(String[] args) throws Exception {
        X2Svg x2svg = new X2Svg();

        if (args.length == 0) { // no args? Bail out early
            x2svg.usage();
            return;
        }

        RuntimeParameters params = x2svg.parseCommandline(args);
        if (params != null) {
            Runner d = new Runner();
            d.run(params);
        }
    }

    /**
     * Parse the passed command line
     *
     * @param args The command line arguments
     * @return an initialized Parameter object
     */
    private RuntimeParameters parseCommandline(String[] args) {

        /** Characters that denote options with no arg */
        final char[] SIMPLE_OPTS = {'M', 'a', 'd', 'h', 'l', 'm'};

        int aSize = args.length;
        RuntimeParameters p = new RuntimeParameters();
        int pos = 0;
        ParserLoader parserLoader = ParserLoader.getLoader();
        String arg = null;


        // loop over options
        while (pos < aSize && (args[pos].startsWith("-") || args[pos].startsWith("+")))   //$NON-NLS-1$ $NON-NLS-2$
        {
            if (args[pos].length() == 1) {
                usage();
                return null;
            }
            char opt = args[pos].charAt(1);

            if (Arrays.binarySearch(SIMPLE_OPTS, opt) < 0) {
                pos++;
                arg = args[pos];
            }
            switch (opt) {
                case 'a':
                    p.setWithAttributes(args[pos].startsWith("+"));
                    break;
                case 'M':
                    p.setWithElementComments(args[pos].startsWith("+"));
                    break;
                case 'c':
                    handleOutputConversion(p, arg);
                    break;
                case 'd':
                    p.setDebug(true);
                    break;
                case 'h':
                    usage();
                    return null;
                case 'p':
                    parserLoader.load();
                    log.info(parserLoader.getHelpForMode(arg));
                    return null;
                case 'o':
                    p.setSvgOutputFile(arg);
                    break;
                case 'm':
                    p.setOpMode(arg);
                    break;
                case 'C':
                    p.setComment(arg);
                    break;
                case 'P':
                    p.setPropertiesLocation(arg);
                    break;
                case 'l':
                    parserLoader.load();
                    log.info(Messages.getString("X2Svg.0")); //$NON-NLS-1$
                    log.info(parserLoader.listParsers());
                    return null;
                default:
                    log.info(Messages.getString("X2Svg.1") + opt); //$NON-NLS-1$
                    usage();
                    return null;
            }
            pos++;
        } // while (arg ..)

        // get the input file
        if (pos < aSize)
            p.setInputFileName(args[pos]);
        else {
            usage();
            return null;
        }

        // set name of svg output file
        setSvgOutputFileName(p);
        pos++;

        // pull in parser specific options
        if (pos < aSize) {
            int size = aSize - pos;
            String[] options = new String[size];
            System.arraycopy(args, pos, options, 0, aSize - pos);
            p.setParserSpecificOptions(options);
        }

        // look at the output conversions if they only specified a type, but
        // no file name and fill that in
        setConversionFileNames(p);
        return p;
    }

    private void setConversionFileNames(RuntimeParameters p) {
        for (OutputFormat format : p.getOutputFormats()) {
            if (format.getFileName() == null) {
                String type = format.getType().toString().toLowerCase();
                String name = p.getInputFileName();
                name = SantasLittleHelper.getFileNameFromPath(name);
                name = SantasLittleHelper.attachSuffixToFileName(name, type);
                format.updateFileName(name);
            }
        }
    }

    private void setSvgOutputFileName(RuntimeParameters p) {
        if (p.getSvgOutputFile() == null) {
            String tmp = p.getInputFileName();
            p.setSvgOutputFile(SantasLittleHelper.attachSuffixToFileName(tmp, "svg")); //$NON-NLS-1$
        }
    }

    /**
     * Handle option -c [filename.suffix][:type] on the command line
     * parameters. Input is one combination of filename and type.
     * It is also allowed that the filename is a directory only in which
     * case the output files are relative to that directory.
     *
     * @param p   parameters object to store the result
     * @param arg the passed argument of option -c
     */
    private void handleOutputConversion(RuntimeParameters p, String arg) {
        int cpos = arg.indexOf(':');
        OutputFormat of = null;
        if (cpos > -1) { // Type is explicitly given
            String name = arg.substring(0, cpos);
            String outputTypes = arg.substring(cpos + 1).toUpperCase();
            String[] typesArray = outputTypes.split(":"); //$NON-NLS-1$
            for (String typ : typesArray) {
                OutputType oType = OutputType.valueOf(typ);

                // now lets see if the filename is given or only the type
                if (cpos == 0) {
                    of = new OutputFormat(oType);
                } else {
                    // see if we have a filename or a directory
                    if (name.endsWith("/")) //$NON-NLS-1$
                        of = new OutputFormat(name, oType, true); // Directory
                    else
                        of = new OutputFormat(name, oType, false); // File
                }
            }
        } else {
            of = new OutputFormat(arg);
        }
        if (of != null)
            p.addOutputFormat(of);
    }

    /**
     * Display usage of the program.
     */
    private void usage() {
        log.info(Messages.getString("X2Svg.2")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.3")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.4")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.5")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.6")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.7")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.8")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.13")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.14")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.15")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.16")); //$NON-NLS-1$
        log.info(Messages.getString("X2Svg.9")); //$NON-NLS-1$
    }
}
