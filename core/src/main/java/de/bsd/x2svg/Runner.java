/*******************************************************************************
 * Copyright (c) 2007,2008 Heiko W. Rupp. 	All rights reserved.
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

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import de.bsd.x2svg.outputConverter.ConversionException;
import de.bsd.x2svg.outputConverter.OutputFormat;
import de.bsd.x2svg.outputConverter.SvgConverter;
import de.bsd.x2svg.parsers.InputParser;
import de.bsd.x2svg.parsers.ParserProblemException;
import de.bsd.x2svg.util.IOUtil;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class Runner {

    private static final String DOTS = "................................................"; //$NON-NLS-1$
    private static SVGGraphics2D svgGenerator;
    private static final boolean debug = false;
    private final RuntimeProperties rProps = RuntimeProperties.getInstance();

    /**
     * Main code block, runs the whole show.
     *
     * @param params RuntimeParameters that have e.g. been on the command line.
     * @throws Exception if anything goes wrong internally
     */
    public void run(RuntimeParameters params) throws Exception {
        rProps.loadProperties(params.getPropertiesLocation(), debug);
        rProps.setFont(Font.decode(rProps.getFontName()));
        setupSvg();
        rProps.setFontMetrics(svgGenerator.getFontMetrics(rProps.getFont()));
        rProps.setFontHeight(rProps.getFontMetrics().getHeight());
        rProps.setFontSmall(Font.decode(rProps.getFontSmallName()));


        // Load parser plugins
        ParserLoader parserLoader = ParserLoader.getLoader();
        parserLoader.load();

        InputParser ip;
        try {
            if (params.getOpMode() != null)
                ip = parserLoader.getParserByMode(params.getOpMode());
            else
                ip = parserLoader.getParserForFilename(params.getInputFileName());
        } catch (NoParserException e) {
            System.out.println(Messages.getString("Runner.3")); //$NON-NLS-1$
            System.out.println(e.getMessage());
            return;
        }

        ip.setInputFile(new File(params.getInputFileName()));
        String[] parserSpecificOptions = params.getParserSpecificOptions();
        if (parserSpecificOptions != null && parserSpecificOptions.length>0) {
            ip.setParserOptions(parserSpecificOptions);
        }
        if (debug)
            ip.setDebug();
        // set with attr and with comment if appropriate
        if (params.getWithAttributes() != null) // command line overrides
            ip.setWithAttributes(params.getWithAttributes());
        else
            ip.setWithAttributes(rProps.isWithAttributes());

        if (params.getWithElementComments() != null) // command line overrides
            ip.setWithElementComments(params.getWithElementComments());
        else
            ip.setWithElementComments(rProps.isWithElementComments());

        Container rootCont;

        try {
            rootCont = ip.parseInput();
        } catch (ParserProblemException ppe) {
            System.out.println(Messages.getString("Runner.4")); //$NON-NLS-1$
            System.out.println("  " + ppe.getMessage()); //$NON-NLS-1$
            return;
        }

        // parsers failed, so exit here
        if (rootCont == null) {
            System.out.println(Messages.getString("Runner.6")); //$NON-NLS-1$
            return;
        }

        // build the tree with its containers

        // Fill in the sizes in the tree
        computeTreeSize(rootCont);
        rootCont.totalWidth += 5; // see below
        rootCont.totalHeight += Constants.BORDER_HEIGHT;

        if (debug) {
            System.out.println();
            printContainer(rootCont, "  "); //$NON-NLS-1$
        }

        drawContainer(rootCont, 5, 0);

        /*
         * Now add a comment if defined in properties
         * or on the command line. Command line overrides properties
         * To add the comment, enlarge the canvas TODO
         */
        String comment = null;
        if (params.getComment() != null) {
            comment = params.getComment();
        } else if (rProps.isCommentTextOn()) {
            comment = rProps.getCommentText();
        }
        if (comment != null && !comment.equals("")) {
            svgGenerator.setFont(rProps.getCommentTextFont());
            svgGenerator.setColor(rProps.getCommentTextColor());
            Date now = new Date();
            comment = comment.replace("{t}", now.toString());
            comment = comment.replace("{i}", params.getInputFileName());
            comment = comment.replace("{u}", System.getProperty("user.name"));

            // we use the comment text font. Get metrics from it
            FontMetrics cfm = svgGenerator.getFontMetrics(rProps.getCommentTextFont());

            rootCont.totalHeight += 2; // add some space above the comment. Looks better
            /*
             * Find out if the comment is wider than the graphics width and break
             * the comment in multiple lines accordingly.
             * TODO try to find word boundaries to break the line or try to fill
             *    the line(s) up to the max and then do a back off until a space is
             *    reached.
             */
            int lineWidth = cfm.stringWidth(comment);
            int lines = lineWidth / (rootCont.totalWidth - 5) + 1;
            int substringLength = comment.length() / lines;
            int start = 0;
            for (int i = 0; i < lines; i++) {
                String substring = comment.substring(start, substringLength * (i + 1));
                svgGenerator.drawString(substring, 5, rootCont.totalHeight + cfm.getAscent());
                rootCont.totalHeight += cfm.getHeight();
                start += substringLength;
            }
        }


        // define a bounding box
        svgGenerator.setSVGCanvasSize(new Dimension(rootCont.totalWidth, rootCont.totalHeight + 20)); // TODO height is bogus

        File svgFile = new File(params.getSvgOutputFile());
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(svgFile), StandardCharsets.UTF_8); //$NON-NLS-1$
            svgGenerator.stream(out, false); // 2nd arg=use Css
        } finally {
            IOUtil.close(out);
        }

        /*
         * Loop over the desired output formats and convert
         * the SVG
         */
        SvgConverter outputConverter = new SvgConverter();
        for (OutputFormat output : params.getOutputFormats()) {
            File outFile = new File(output.getFileName());
            try {
                outputConverter.convert(output.getType(), svgFile, outFile);
            } catch (ConversionException ce) {
                System.out.println("Problem converting to " + output.getType() + ": " + ce.getLocalizedMessage());
            }
        }
    }

    /**
     * Set up the SVG routines
     */
    private void setupSvg() {
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg"; //$NON-NLS-1$

        // Create a context
        Document document = domImpl.createDocument(svgNS, "svg", null); //$NON-NLS-1$

        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
        ctx.setComment("Generated by x2svg with the help of Batik SVG Generator"); //$NON-NLS-1$

        // Create an instance of the SVG Generator.
        svgGenerator = new SVGGraphics2D(ctx, false);
    }

    /**
     * Print the passed container with its children to stdout.
     * Mostly a debugging help
     *
     * @param rootCont The root element of the Container tree
     * @param indent   how much spaces should this be indended?
     */
    private void printContainer(Container rootCont, String indent) {
        System.out.print(indent + "--<" + rootCont.name + ">-"); //$NON-NLS-1$ //$NON-NLS-2$
        String out;
        switch (rootCont.content) {
            case MIXED:
                out = "M"; //$NON-NLS-1$
                break;
            case SEQUENCE:
                out = "S"; //$NON-NLS-1$
                break;
            case CHOICE:
                out = "C"; //$NON-NLS-1$
                break;
            default:
                out = "-"; //$NON-NLS-1$
        }
        String info = "   tw=" + rootCont.totalWidth + ", th=" //$NON-NLS-1$ //$NON-NLS-2$
                + rootCont.totalHeight;
        System.out.println(out + info);
        for (Container cont : rootCont.children) {
            String newIndent = indent + ".." //$NON-NLS-1$
                    + DOTS.substring(0, rootCont.name.length());
            printContainer(cont, newIndent);
        }
        // System.out.println();

    }

    /**
     * Draw the passed container into a SVG Canvas by invoking the containers
     * paint() method
     *
     * @param rootCont The root of the container tree to show
     * @param startX   The top left corner of the drawing area
     * @param startY   The top left corner of the drawing area
     * @return the y coordinate of the little tail on the left side of the
     */
    private int drawContainer(Container rootCont, int startX, int startY) {
        if (debug) {
            System.out.println("dc: " + rootCont.name + " ,x=" + startX + ",y=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + startY + ",ca=" + rootCont.cardinality.getFrom()); //$NON-NLS-1$
        }
        int myY = startY + (rootCont.totalHeight / 2);
        // container itself
        int ret = rootCont.paint(svgGenerator, startX, myY);

        /*
         * our container is rootCont.totalHeight high
         */
        // children
        int newX = startX + rootCont.localWidth + Constants.X_OFFSET;
        int yOffset = 0; //Constants.BORDER_HEIGHT / 2;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        // Loop over children and draw them
        for (Container cont : rootCont.children) {
            int ly = drawContainer(cont, newX, startY + yOffset);
            yOffset += cont.totalHeight;
            if (ly < minY)
                minY = ly;
            if (ly > maxY)
                maxY = ly;
        }
        // now draw vertical line
        if (rootCont.children.size() > 1) {
            if (ret < minY)
                minY = ret;
            if (ret > maxY)
                maxY = ret;

            int lx = startX + rootCont.localWidth + Constants.X_OFFSET - 5;
            svgGenerator.drawLine(lx, minY, lx, maxY);
            svgGenerator.setColor(Color.GRAY);
            svgGenerator.drawLine(lx + 1, minY + 1, lx + 1, maxY - 1);
            svgGenerator.setColor(Color.BLACK);
        }

        return ret;
    }

    /**
     * Fill in the sizes of each element
     *
     * @param rootCont Container in the tree to start with
     */
    private void computeTreeSize(Container rootCont) {
        int fontHeight = rProps.getFontHeight();
        int w;
        final String text = rootCont.name;
        if (text != null) {
            w = rProps.getFontMetrics().stringWidth(rootCont.name) + 2;
        } else {
            w = 20; // anonymous container needs space for its content model
        }

        // need more space if we show additional information.
        if (rootCont.isEmpty || rootCont.isPcData || rootCont.isReference || rootCont.isAbstract) {
            w += 8;
        }

        w = Math.max(fontHeight, w);  // provide some minimal size

        // TODO calculate the width of the attributes too
        rootCont.totalWidth = w + Constants.BORDER_WIDTH;
        rootCont.localWidth = rootCont.totalWidth;

        rootCont.localHeight = fontHeight + Constants.BORDER_HEIGHT;
        if (!rootCont.attributes.isEmpty()) {
            // TODO Make the following class global ?
            FontMetrics afm = svgGenerator.getFontMetrics(rProps.getAttributeFont());
            int afh = afm.getAscent() + afm.getDescent() + afm.getLeading();
            // TODO use a smaller font for the attributes
            rootCont.localHeight += (rootCont.attributes.size() * afh) + 4;
        }


        rootCont.totalHeight = rootCont.localHeight + Constants.BORDER_HEIGHT;


        int width = 0;
        int height = 0;
        for (Container cont : rootCont.children) {
            computeTreeSize(cont);
            height += cont.totalHeight;
            width = Math.max(cont.totalWidth, width);
        }
        rootCont.totalWidth += width;
        rootCont.totalWidth += 36; // width of content model
        rootCont.totalHeight = Math.max(rootCont.totalHeight, height);

    }

}
