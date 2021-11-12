/*******************************************************************************
 * Copyright (c) 2007 Heiko W. Rupp and others. All rights reserved.
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
import de.bsd.x2svg.ContentModel;

/**
 * Draw a box of an anonymous element container
 * and indicators of cardinality and content model.
 * <p/>
 * Example: <br/>
 * <img src="{@docRoot}/../img/anon_container.png"/>
 * <p/>
 *
 * @author Gareth Floodgate
 * @author Heiko W. Rupp
 * @since 1.1
 */
public class AnonymousContainer extends ElementBox {

    private final ContentModel content;

    /**
     * Constructor to use
     *
     * @param card    The cardinality
     * @param content The content model of the container
     */
    public AnonymousContainer(final Cardinality card, final ContentModel content) {
        super(null, card); // name is null in this case
        this.content = content;
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

        svg.setColor(Color.BLACK);

        // Now draw the content model.
        int yHalf = topY - 3 + RuntimeProperties.getInstance().getFontHeight() / 2;
        int x7 = topX + 11;

        svg.setStroke(Constants.ROUNDED);
        switch (content) {
            case CHOICE:
                ChoiceModel cm = new ChoiceModel();
                cm.draw(svg, x7, yHalf - 7);
                break;
            case SEQUENCE:
                SequenceModel sm = new SequenceModel();
                sm.draw(svg, x7, yHalf - 7);
                break;
			case TYPE_ON_RIGHT:
				TypeModel tm = new TypeModel();
				tm.draw(svg, x7, yHalf-7);
				break;
			default:
				System.out.println("unknown case for container content model..." + content.name());
        }

        // line to right neighbor
        svg.drawLine(topX + 34, yHalf, topX + 69, yHalf);
        svg.setColor(Color.GRAY);
        svg.drawLine(topX + 34 + 2, yHalf - 1, topX + 69, yHalf - 1);
        svg.setColor(Color.BLACK);
    }
}
