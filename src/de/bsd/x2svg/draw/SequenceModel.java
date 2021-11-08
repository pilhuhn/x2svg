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

/**
 * Draw a Sequence content model
 * <p/>
 * Example: <br/>
 * <img src="{@docRoot}/../img/sequence.png"/>
 * <p/>
 * @author hwr@pilhuhn.de
 * @since 1.0
 */
public class SequenceModel extends ContentModel 
{

	/** 
	 * Draw the box with its content
	 * @param svg svg canvas to draw on
	 * @param topX top left corner of the box
	 * @param topY top left corner of the box
	 */
	@Override
	public void draw(SVGGraphics2D svg, int topX, int topY) 
	{
		drawContentBox(svg,topX,topY); // content model box
		int yHalf = topY + 7; // TODO  7 relative to font
		
		svg.setColor(Color.BLACK);
		svg.drawLine(3+topX, yHalf, 15+topX, yHalf);
		svg.fillOval(4+topX, yHalf-1, 2, 2);
		svg.fillOval(8+topX, yHalf-1, 2, 2);  
		svg.fillOval(12+topX, yHalf-1, 2, 2);
	}

}
