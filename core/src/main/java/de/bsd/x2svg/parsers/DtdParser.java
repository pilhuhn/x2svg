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
package de.bsd.x2svg.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import de.bsd.x2svg.Cardinality;
import de.bsd.x2svg.Cardinality.DTDValues;
import de.bsd.x2svg.Container;
import de.bsd.x2svg.ContentModel;
import de.bsd.x2svg.Messages;
import de.bsd.x2svg.util.SantasLittleHelper;

import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDAny;
import com.wutka.dtd.DTDAttribute;
import com.wutka.dtd.DTDCardinal;
import com.wutka.dtd.DTDChoice;
import com.wutka.dtd.DTDContainer;
import com.wutka.dtd.DTDElement;
import com.wutka.dtd.DTDEmpty;
import com.wutka.dtd.DTDItem;
import com.wutka.dtd.DTDMixed;
import com.wutka.dtd.DTDName;
import com.wutka.dtd.DTDPCData;
import com.wutka.dtd.DTDParser;
import com.wutka.dtd.DTDSequence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A parser that reads Document Type Descriptions (DTDs)
 *
 * @author hwr@pilhuhn.de
 */
public class DtdParser implements InputParser {

    private final Log log = LogFactory.getLog(DtdParser.class);

    private static final String MODE_STRING = "dtd"; //$NON-NLS-1$
    private static final String FILE_SUFFIX_STRING = ".dtd"; //$NON-NLS-1$
    private File dtdFile;
    private String dtdRootName;
    private boolean shouldGuessDtdRootName;
    private int treeDepth = Integer.MAX_VALUE; // default = unlimited
    private boolean alsoAttributes = false;
    private boolean debug = false;

    /**
     * Parse the input. The dtdFile and dtdRootElement
     * need to be set previously.If the dtdRootElement is null or empty, the parser will then
     * try to guess it
     *
     * @return container tree of this DTD starting at the element specified in dtdRootName
     * @throws ParserProblemException If a problem with the passed DTD is found
     * @throws IOException            if the underlying {@link com.wutka.dtd.DTDParser} encounters a problem.
     */
    public Container parseInput() throws IOException, ParserProblemException {
        // prerequisites not met
        if (dtdFile == null) //$NON-NLS-1$
            throw new ParserProblemException(Messages.getString("DtdParser.3"), getSpecificHelp()); //$NON-NLS-1$

        if (dtdRootName == null || "".equals(dtdRootName)) {
            // If no root node name was specified for the dtd, let the parser guess it,
            shouldGuessDtdRootName = true;
        }

        DTDParser parser = new DTDParser(dtdFile);
        DTD dtd = parser.parse(shouldGuessDtdRootName);

        DTDElement rootElement;
        if (shouldGuessDtdRootName) {
            rootElement = dtd.rootElement;
        } else {
            rootElement = (DTDElement) dtd.elements.get(dtdRootName);
        }

        if (rootElement == null) {
            throw new ParserProblemException(Messages.getString("DtdParser.4") + dtdRootName + Messages.getString("DtdParser.5")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (debug)
            log.debug("Root element is " + rootElement.getName());

        // Now start the real show. Parent container is null for the root element.
        return parse(null, dtd, rootElement, 0);
    }

    /**
     * Recursively parse the DTD and fill its data into containers
     * (This method deals with DTDItem, rather than DTDElement).
     *
     * @param parent The parent container where our stuff will be appended to
     * @param dtd    The DTD to use
     * @param root   The 'current' root item of this part of the DTD tree
     * @param level  How deep do we want the generated tree to be? 0=root element only
     * @return container tree of this DTD starting at the root element
     * @throws ParserProblemException If a DTD element that is referred to can not be found.
     */
    private Container parse(Container parent, final DTD dtd, final DTDItem root, final int level) throws ParserProblemException {
        Container cont = new Container();
        cont.parent = parent;
        cont.cardinality = new Cardinality(DTDValues.ONE); // to prevent NPE later on. Basically only needed for the root.

        if (debug)
            log.debug("parse1: " + SantasLittleHelper.spaces(level) + "?"); // TODO replace ? by real content

        if (root instanceof DTDContainer) {
            setContentModel(cont, root);

            DTDContainer dCont = (DTDContainer) root;
            DTDItem[] items = dCont.getItems();

            if (cont.children == null)
                cont.children = new ArrayList<Container>(items.length);

            for (DTDItem anItem : items) {
                // call this
                if (treeDepth > 0 && level < treeDepth) {
                    Container res = parse(cont, dtd, anItem, level + 1);
                    setCardinality(res, anItem.getCardinal());
                    cont.children.add(res);
                }
            }

        } else if (root instanceof DTDName) {
            String name = ((DTDName) root).value;

            DTDElement element = (DTDElement) dtd.elements.get(name);
            if (element == null) {
                throw new ParserProblemException(Messages.getString("DtdParser.6") + name + Messages.getString("DtdParser.7") + name + Messages.getString("DtdParser.8")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

            // check for tree path here as well
            if (cont.isElementInHierarchy(name)) {
                Container res = new Container();
                res.isReference = true;
                res.name = name;
                setCardinality(res, root.getCardinal());
                res.parent = cont.parent;
                cont = res;
            } else if (treeDepth > 0 && level < treeDepth) {
                cont = parse(cont.parent, dtd, element, level + 1);
            }
        } else if (root instanceof DTDEmpty) {
            cont.isEmpty = true;
        } else if (root instanceof DTDAny) {
            cont.isAny = true;
        } else if (root instanceof DTDPCData) {
            cont.isPcData = true;
        } else {
            log.warn(Messages.getString("DtdParser.9") + " - " + root.getClass().getName()); //$NON-NLS-1$
        }

        return cont;
    }


    /**
     * Recursively pare the DTD and fill its data into containers.
     *
     * @param parent the parent container of those that will be created on this round. null if it is the parent is the tree root.
     * @param dtd    The DTD to use
     * @param root   The 'current' root element of this part of the DTD tree
     * @param level  How deep do we want the generated tree to be? 0=root element only
     * @return container tree of this DTD starting at the root element
     * @throws ParserProblemException If a DTD element that is referred to can not be found.
     */
    private Container parse(Container parent, DTD dtd, DTDElement root, int level) throws ParserProblemException {
        Container cont = new Container();
        cont.cardinality = new Cardinality(DTDValues.ONE); // to prevent NPE later on. Basically only needed for the root.
        DTDItem item = root.getContent();
        cont.name = root.name;
        cont.parent = parent;

        if (debug)
            log.debug("parse2: " + SantasLittleHelper.spaces(level) + cont.name);

        if (alsoAttributes) {
            addAttributes(cont, root);
        }


        if (item instanceof DTDContainer) {
            setContentModel(cont, item);

            DTDContainer dCont = (DTDContainer) item;
            DTDItem[] items = dCont.getItems();

            if (cont.children == null)
                cont.children = new ArrayList<Container>(items.length);

            for (DTDItem anItem : items) {
                if (anItem instanceof DTDContainer) {
                    if (treeDepth > 0 && level < treeDepth) {
                        Container res = parse(cont, dtd, anItem, level + 1);
                        setCardinality(res, anItem.getCardinal());
                        cont.children.add(res);
                    }
                }
                if (anItem instanceof DTDName) {
                    String name = ((DTDName) anItem).value;

                    DTDElement element = (DTDElement) dtd.elements.get(name);
                    if (element == null) {
                        throw new ParserProblemException(Messages.getString("DtdParser.6") + name + Messages.getString("DtdParser.7") + root.name + Messages.getString("DtdParser.8")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }

                    if (cont.isElementInHierarchy(name)) {
                        Container res = new Container();
                        res.isReference = true;
                        res.name = name;
                        setCardinality(res, anItem.getCardinal());
                        cont.children.add(res);
                    } else if (treeDepth > 0 && level < treeDepth) {
                        Container res = parse(cont, dtd, element, level + 1);
                        setCardinality(res, anItem.getCardinal());
                        cont.children.add(res);
                    }
                }
                if (anItem instanceof DTDPCData) {
                    cont.isPcData = true;
                }
                if (anItem instanceof DTDEmpty) {
                    cont.isEmpty = true;
                }
            }

        } else if (item instanceof DTDAny) {
            cont.isAny = true;
        } else if (item instanceof DTDPCData) {
            cont.isPcData = true;
        } else if (item instanceof DTDEmpty) {
            cont.isEmpty = true;
        } else {
            log.warn(Messages.getString("DtdParser.9") + " - " + item.getClass().getName()); //$NON-NLS-1$
        }

        return cont;
    }

    /**
     * Extract the attributes from the element. We currently only use their name
     *
     * @param cont    Container to put the attributes into
     * @param element The element from where the attributes get pulled.
     */
    @SuppressWarnings("unchecked")
    private void addAttributes(Container cont, DTDElement element) {

        Enumeration<String> keys = element.attributes.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            DTDAttribute attr = element.getAttribute(key);
            cont.attributes.add(attr.getName());
        }
    }

    /**
     * Set the cardinality of the container depending on the DTD cardinality
     *
     * @param cont     The container to fill
     * @param cardinal The cardinality of the DTD
     */
    private void setCardinality(Container cont, DTDCardinal cardinal) {
        if (cardinal.equals(DTDCardinal.NONE))
            cont.cardinality = new Cardinality(DTDValues.ONE);
        else if (cardinal.equals(DTDCardinal.ONEMANY))
            cont.cardinality = new Cardinality(DTDValues.ONE_OR_MORE);
        else if (cardinal.equals(DTDCardinal.OPTIONAL))
            cont.cardinality = new Cardinality(DTDValues.ZERO_OR_ONE);
        else
            cont.cardinality = new Cardinality(DTDValues.ZERO_OR_MORE);
    }

    /**
     * Set the content model in the container from the passed item
     *
     * @param cont The container to fill
     * @param item The DTDitem from where information is obtained
     */
    private void setContentModel(Container cont, DTDItem item) {
        if (item instanceof DTDMixed)
            cont.content = ContentModel.MIXED;
        else if (item instanceof DTDSequence)
            cont.content = ContentModel.SEQUENCE;
        else if (item instanceof DTDChoice)
            cont.content = ContentModel.CHOICE;
        else
            cont.content = ContentModel.NONE;
    }


    /**
     * Sets the input file to work on
     *
     * @param inputFile a valid file object to work on
     */
    public void setInputFile(File inputFile) {
        dtdFile = inputFile;
    }

    /**
     * Set the parser specific options.
     * These are: [-d depth] [root-element]
     */
    public void setParserOptions(String[] options) {
        if (options == null || options.length < 1)
            return;
        int pos = 0;
        if (options[pos].equals("-d")) { //$NON-NLS-1$
            String td = options[pos + 1];
            try {
                treeDepth = Integer.parseInt(td);
            } catch (NumberFormatException nfe) {
                treeDepth = Integer.MAX_VALUE; // unlimited
            }
            pos += 2;
        }
        if (options.length > pos)
            dtdRootName = options[pos];
    }

    /**
     * Additional help for this parser
     */
    public String getSpecificHelp() {
        return Messages.getString("DtdParser.11"); //$NON-NLS-1$
    }

    public String getFileSuffix() {
        return FILE_SUFFIX_STRING;
    }

    /**
     * Our mode is 'dtd'
     */
    public String getMode() {
        return MODE_STRING;
    }

    public void setDebug() {
        debug = true;
    }

    public void setWithAttributes(boolean value) {
        alsoAttributes = value;
    }

    public void setWithElementComments(boolean value) {
        // nothing for now
    }

}
