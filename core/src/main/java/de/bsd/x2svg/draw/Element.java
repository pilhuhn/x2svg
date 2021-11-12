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
package de.bsd.x2svg.draw;

import java.awt.Color;

import org.apache.batik.svggen.SVGGraphics2D;

import de.bsd.x2svg.Cardinality;
import de.bsd.x2svg.Constants;
import de.bsd.x2svg.RuntimeProperties;

/**
 * Draw a box of an element with the element name in it
 * and indicators of cardinality and content type (text)
 * <p/>
 * Example: <br/>
 * <img src="{@docRoot}/../img/element.png"/>
 * <p/>
 *
 * @author hwr@pilhuhn.de
 * @since 1.0
 */
public class Element extends ElementBox {

    /**
     * Constructor to use
     *
     * @param name The name of the element
     * @param card The cardinality
     */
    public Element(String name, Cardinality card) {
        super(name, card);
        textColor = RuntimeProperties.getInstance().getTextColor();
    }

    /**
     * Draws the element box
     *
     * @param svg    the svg canvas to draw on
     * @param topX   the top left corner
     * @param topY   the top left corner
     * @param width  width of the element box
     * @param height height of the element box
     * @param isText should the text-marker be drawn?
     */
    @Override
    public void draw(SVGGraphics2D svg, int topX, int topY, int width, int height, boolean isText, boolean isEmpty) {
        super.draw(svg, topX, topY, width, height, isText, isEmpty);
        int halfHeight = RuntimeProperties.getInstance().getFontHeight() / 2;

        svg.setColor(textColor);
        svg.setFont(RuntimeProperties.getInstance().getFont());
        svg.drawString(name, topX + 10, topY + halfHeight); // center ?

        int endX = topX + width;

        svg.setColor(Color.BLACK);
        // #PCDATA
        if (isText) {
            svg.setStroke(Constants.ROUNDED);  // Solid
            svg.drawLine(endX - 6, topY - halfHeight, endX - 6, topY + 6 - halfHeight);
            svg.drawLine(endX - 6, topY + 6 - halfHeight, endX + 1, topY + 6 - halfHeight);
            svg.drawLine(endX - 6, topY - halfHeight, endX + 1, topY + 6 - halfHeight);
        }
    }
}
