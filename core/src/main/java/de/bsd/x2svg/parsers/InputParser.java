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
package de.bsd.x2svg.parsers;

import java.io.File;

import de.bsd.x2svg.Container;

/**
 * The interface, all input parsers need to implement.
 *
 * @author hwr@pilhuhn.de
 * @since 1.0
 */
public interface InputParser {

    /**
     * Parse the input and build a tree of Containers.
     * The method does not need to fill in the sizes
     * (localHeight, totalHeight etc.) as those will be later
     * filled in in a second pass
     *
     * @return A Container that is the root of the parsed input.
     * @throws Exception if anything goes wrong.
     * @see de.bsd.x2svg.Container
     */
    Container parseInput() throws Exception;

    /**
     * Pass in the input file to parse
     *
     * @param inputFile a File object
     */
    void setInputFile(File inputFile);

    /**
     * Pass in options that are parser specific.
     * The DtdParser as an example needs a root element, while
     * the PropertiesParser doesn't
     *
     * @param options array of individual option parts as passed on the command line.
     */
    void setParserOptions(String[] options);

    /**
     * Return a String that gives help for this specific parser.
     * This string should list valid command line options.
     *
     * @return a help string or null if no help is available.
     */
    String getSpecificHelp();

    /**
     * Return the mode string
     *
     * @return the mode string
     */
    String getMode();

    /**
     * Return the file suffix this parser reacts upon
     *
     * @return the file suffix
     */
    String getFileSuffix();

    /**
     * Tells the parer to set the debug mode.
     * It depends on the individual parser if it actually has a debug mode.
     */
    void setDebug();

    /**
     * Tells the parser that parsing attributes of elements is wanted.
     * It depends on the individual parser if it acutally can parse them.
     * For example the {@link DtdParser} is able to do this, while the {@link PropertiesParser}
     * is not.
     *
     * @param value The value to set this to.
     */
    void setWithAttributes(boolean value);

    /**
     * Tells the parser that parsing comments for elements (and possibly attributes)
     * is desired.<br/>
     * It depends on the individual parser if it actually can parse them
     *
     * @param value The value to set this to.
     */
    void setWithElementComments(boolean value);
}
