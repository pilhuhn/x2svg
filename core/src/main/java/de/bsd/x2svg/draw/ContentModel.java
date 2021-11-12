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

import de.bsd.x2svg.RuntimeProperties;

import java.awt.Color;
import java.awt.Polygon;

import org.apache.batik.svggen.SVGGraphics2D;

/**
 * Draw a box that represents the content model of this
 * tree part (Choice, Sequence, ..).
 * The contents of the box needs to be painted in a subclass.
 *
 * @author hwr@pilhuhn.de
 */
public abstract class ContentModel {

    /**
     * Draw the box that holds the content model.
     *
     * @param svg svg canvas to use
     * @param x   top left corner of the box
     * @param y   top left corner of the box
     */
    void drawContentBox(SVGGraphics2D svg, int x, int y) {
        boolean simpleShadow = RuntimeProperties.getInstance().isSimple_shadow();

        Polygon p = new Polygon();
        p.addPoint(0, 3);
        p.addPoint(3, 0);
        p.addPoint(15, 0);
        p.addPoint(18, 3);
        p.addPoint(18, 11);
        p.addPoint(15, 14);
        p.addPoint(3, 14);
        p.addPoint(0, 11);
        p.addPoint(0, 3);
        p.translate(x + 2, y - 2);
        if (simpleShadow)
            svg.setColor(Color.GRAY);
        else
            svg.setColor(Color.LIGHT_GRAY);
        svg.fillPolygon(p);
        svg.drawPolygon(p);

        p.translate(-1, 1);
        if (simpleShadow)
            svg.setColor(Color.GRAY);
        else
            svg.setColor(Color.DARK_GRAY);
        svg.fillPolygon(p);
        svg.drawPolygon(p);

        p.translate(-1, 1);
        svg.setColor(Color.WHITE);
        svg.fillPolygon(p);
        svg.setColor(Color.BLACK);
        svg.drawPolygon(p);

    }

    /**
     * Contents of the box
     *
     * @param svg  svg canvas to use
     * @param topX top left corner of the surrounding box
     * @param topY top left corner of the surrounding box
     */
    public abstract void draw(SVGGraphics2D svg, int topX, int topY);

}
