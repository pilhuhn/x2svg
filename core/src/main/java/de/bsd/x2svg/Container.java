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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import org.apache.batik.svggen.SVGGraphics2D;

import de.bsd.x2svg.draw.AbstractElement;
import de.bsd.x2svg.draw.AnonymousContainer;
import de.bsd.x2svg.draw.ChoiceModel;
import de.bsd.x2svg.draw.Element;
import de.bsd.x2svg.draw.ElementBox;
import de.bsd.x2svg.draw.InheritanceModel;
import de.bsd.x2svg.draw.Reference;
import de.bsd.x2svg.draw.SequenceModel;
import de.bsd.x2svg.draw.SubstitutionElement;
import de.bsd.x2svg.draw.SubstitutionModel;

import java.awt.Color;

/**
 * This class holds all information about an element and its children.
 * The paint() method is used to draw them on screen.
 *
 * @author hwr@pilhuhn.de
 */
public class Container {

    /**
     * Name of this element. Yields label in the container
     */
    public String name;    // The name of this element
    /**
     * Width of this element including the with of included elements
     */
    public int totalWidth;
    /**
     * Height of this element and enclosed ones
     */
    public int totalHeight;
    /**
     * Width of the element plus its surrounding content box
     */
    public int localWidth;
    /**
     * Height of the element plus its surrounding content box
     */
    public int localHeight;
    /**
     * The children of this container
     */
    public List<Container> children = new ArrayList<Container>();
    /**
     * Can the element contain text?
     */
    public boolean isPcData;
    public boolean isAny;
    /**
     * Can the element NOT contain children (is it marked as EMPTY)?
     */
    public boolean isEmpty;
    /**
     * The content model of the children of this container
     */
    public ContentModel content = ContentModel.NONE;
    /**
     * A link to the parent container or null if this is the root of the tree
     */
    public Container parent;
    /**
     * The cardinality of this element. Default is the optional empty cardinality
     */
    public Cardinality cardinality = new Cardinality(false);
    /**
     * Top left position of this container
     */
    public int xPos;
    /**
     * Top left position of this container
     */
    public int yPos;
    /**
     * Is this a real element or a references to one already defined somewhere up the hierarchy?
     */
    public boolean isReference = false;
    /**
     * Is this an abstract element (XSD use)
     */
    public boolean isAbstract = false;
    /**
     * Is this a substitutionGroup (XSD use)
     */
    public boolean isSubstitution = false;
    /**
     * Is this a named (complex) type (XSD use)
     */
    public boolean isType = false;

    /**
     * Attributes of this element
     */
    public List<String> attributes = new ArrayList<String>();

    /**
     * A comment for this element
     */
    public String comment;

    /**
     * Paints a container and its children. Does this by recursively calling itself.
     *
     * @param svg    a svg canvas to draw on
     * @param startX top left corner of the area to draw in
     * @param startY top left corner of the area to draw in
     * @return the y coordinate of the little tail on the left side of the element box
     */
    public int paint(SVGGraphics2D svg, int startX, int startY) {

        Stroke stroke = new BasicStroke(1f);
        setStroke(svg, stroke);

        int off = 1;
        if (isPcData || isEmpty) {
            // If the element has PCDATA or EMPTY markers, make room for an annotation.
            off += 6;
        }

        RuntimeProperties rProps = RuntimeProperties.getInstance();
        int fontHeight = rProps.getFontHeight();
        int halfHeight = fontHeight / 2;

        int underTheBorder = startY + halfHeight + 1;
        if (name == null && children.size() > 0) {
            // An anonymous (unnamed) container.
            final AnonymousContainer ac = new AnonymousContainer(cardinality, content);
            ac.draw(svg, startX, startY, localWidth + off, localHeight - 1, isPcData, false);

            // link to the left side of the element box
            int endY = underTheBorder - 3;
            if (parent != null) {
                setStroke(svg, stroke);
                int fX = startX - 5;
                svg.drawLine(fX, endY, startX, endY);
            }
            // reset stroke to normal
            svg.setStroke(stroke);
        } else  // Box with content
        {
            ElementBox element;
            if (isReference) {
                element = new Reference(name, cardinality);
            } else if (isAbstract) {
                element = new AbstractElement(name, cardinality);
            } else if (isSubstitution) {
                element = new SubstitutionElement(name, cardinality, true);
            } else if (isType) {
                element = new SubstitutionElement(name, cardinality, false);
            } else {
                element = new Element(name, cardinality);
            }

            // Paint the element box
            element.draw(svg, startX, startY, localWidth + off, localHeight - 1, isPcData, isEmpty);


            if (!attributes.isEmpty()) {
                svg.drawLine(startX + 9, underTheBorder + 3, startX + localWidth - 2, underTheBorder + 3);
                Font attributeFont = rProps.getAttributeFont();
                svg.setFont(attributeFont);
                int attrFontHeight = attributeFont.getSize();

                for (int i = 0; i < attributes.size(); i++) {
                    svg.drawString(attributes.get(i), startX + 8, underTheBorder + 2 + (i + 1) * attrFontHeight);
                }
            }

            /*
             * paint the tail and the content model of the children
             * if applicable
             */
            if (children.size() != 0) {
                int endX = startX + localWidth + off;
                drawContentModel(svg, endX, startY);
            }
        }

        // link to the left side of the element box
        int endY = underTheBorder - 3;
        if (parent != null) {
            setStroke(svg, stroke);
            int fX = startX - 5;
            svg.drawLine(fX, endY - 1, startX, endY - 1);
            svg.setColor(Color.GRAY);
            svg.drawLine(fX + 1, endY - 2, startX, endY - 2);
            svg.setColor(Color.BLACK);
        }
        // reset stroke to normal
        svg.setStroke(stroke);

        return endY;
    }


    /**
     * Helper to draw the content model if the container has children
     *
     * @param svg svg canvas to draw on
     * @param x   top left corner of the content model box
     * @param y   top left corner of the content model box
     */
    private void drawContentModel(SVGGraphics2D svg, int x, int y) {
        int yHalf = y - 3 + RuntimeProperties.getInstance().getFontHeight() / 2;
        int x7 = x + 9;
        svg.drawLine(x + 2, yHalf, x7, yHalf); // tail to the left side (to element box)
        svg.setColor(Color.GRAY);
        svg.drawLine(x + 3, yHalf - 1, x7, yHalf - 1);
        svg.setColor(Color.BLACK);

        svg.setClip(null);

        svg.setStroke(Constants.ROUNDED);
        de.bsd.x2svg.draw.ContentModel cm;
        switch (content) {
            case CHOICE:
                cm = new ChoiceModel();
                cm.draw(svg, x7, yHalf - 7);
                break;
            case SEQUENCE:
                cm = new SequenceModel();
                cm.draw(svg, x7, yHalf - 7);
                break;
            case INHERITANCE:
                cm = new InheritanceModel();
                cm.draw(svg, x7, yHalf - 7);
                break;
            case MIXED:
                System.out.println("mixed content at " + name);
                break;
            case SUBSTITUTION:
                cm = new SubstitutionModel();
                cm.draw(svg, x7, yHalf - 7);
                break;
            default:
                System.err.println("unknown case for ContentModel ... : " + content);
        }

        // line to right neighbour
        svg.drawLine(x + 27, yHalf, x + 36, yHalf);
        svg.setColor(Color.GRAY);
        svg.drawLine(x + 27 + 2, yHalf - 1, x + 36, yHalf - 1);
        svg.setColor(Color.BLACK);
    }

    /**
     * Set the stroke to dashed if the container is optional.
     *
     * @param svg    svg canvas
     * @param stroke alternate stroke if the container is not optional
     */
    private void setStroke(SVGGraphics2D svg, Stroke stroke) {
        if (cardinality.isOptional())
            svg.setStroke(Constants.DASHED);
        else
            svg.setStroke(stroke);
    }

    /**
     * Safely determine if the passed container has children
     *
     * @param rootCont Container to examine
     * @return true if rootCont has children.
     */
    public static boolean hasChildren(Container rootCont) {
		return rootCont != null && rootCont.children != null && rootCont.children.size() > 0;
	}

    /**
     * See if an item with the passed name already exists on our way to the
     * root of the container tree.
     *
     * @param elementName Name of an Element
     * @return true if it is found, false otherwise
     */
    public boolean isElementInHierarchy(String elementName) {
        Container c = this;
        while (c != null) {
            if (c.name != null && c.name.equals(elementName)) {
                return true;
            }
            c = c.parent;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("['");
        buf.append(name).append("',p=");
        buf.append(parent);
        buf.append(", c={");
        buf.append(cardinality.getFrom()).append(",").append(cardinality.getTo());
        buf.append("}, ");
        if (isAny)
            buf.append('A');
        if (isEmpty)
            buf.append('E');
        if (isPcData)
            buf.append('P');
        if (isReference)
            buf.append('R');
        buf.append(']');
        return buf.toString();

    }
}
