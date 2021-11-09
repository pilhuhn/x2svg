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

import java.awt.Polygon;

import org.apache.batik.svggen.SVGGraphics2D;

/**
 * Inheritance content model specifies that the elements on the
 * right side inherit from the element on the left side.
 * <p/>
 * Example: <br/>
 * <img src="{@docRoot}/../img/InheritanceModel.png"/>
 * <p/>
 * @author hwr@pilhuhn.de
 * @since 1.2
 */
public class InheritanceModel extends ChoiceModel 
{

	/** 
	 * Draw the ChoiceModle content box and add an inheritance arrow to it.
	 * @see de.bsd.x2svg.draw.ContentModel#draw(org.apache.batik.svggen.SVGGraphics2D, int, int)
	 * @see de.bsd.x2svg.draw.ChoiceModel#draw(SVGGraphics2D, int, int)
	 */
	@Override
	public void draw(SVGGraphics2D svg, int topX, int topY) 
	{
		super.draw(svg, topX, topY);
		// now draw an arrow 
		int yHalf = topY + 7; // TODO  7 relative to font
		Polygon p = new Polygon();
		p.addPoint(1+topX, yHalf);
		p.addPoint(4+topX, yHalf+3);
		p.addPoint(4+topX, yHalf-3);
		svg.fillPolygon(p);
		
	}
}
