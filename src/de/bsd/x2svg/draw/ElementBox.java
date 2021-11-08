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
 * Draw a box of an element with indicators of cardinality
 * <p/>
 * Example: <br/>
 * <img src="{@docRoot}/../img/element_box.png"/>
 * <p/>
 * @author hwr@pilhuhn.de
 * @since 1.1
 */
public abstract class ElementBox 
{

	String name;
	private Cardinality cardinality;
	Color textColor;

	
	/**
	 * Constructor to use
	 * @param name The name of the element
	 * @param card The cardinality
	 */
	ElementBox(String name, Cardinality card)
	{
		this.name=name;
		this.cardinality=card;
		textColor = RuntimeProperties.getInstance().getTextColor();
		
	}
	
	/**
	 * Draws the element box
	 * @param svg the svg canvas to draw on
	 * @param topX the top left corner
	 * @param topY the top left corner
	 * @param width width of the element box 
	 * @param height height of the element box
	 * @param isText should the text-marker be drawn?
	 * @param isEmpty should the empty-marker be drawn?
	 */
	public void draw(SVGGraphics2D svg, int topX, int topY, int width, int height, boolean isText, boolean isEmpty)
	{
        boolean simpleShadow = RuntimeProperties.getInstance().isSimple_shadow();

        int halfHeight = RuntimeProperties.getInstance().getFontHeight() / 2;
        if (simpleShadow)
            svg.setColor(Color.GRAY);
        else
            svg.setColor(Color.LIGHT_GRAY);
		svg.fillRoundRect(topX+3, topY-halfHeight-2, width, height-1, 2,2);
		svg.drawRoundRect(topX+3, topY-halfHeight-2, width, height-1, 2,2);
        if (simpleShadow)
            svg.setColor(Color.GRAY);
        else
            svg.setColor(Color.DARK_GRAY);
		svg.fillRoundRect(topX+2, topY-halfHeight-1, width, height-1, 2,2);
		svg.drawRoundRect(topX+2, topY-halfHeight-1, width, height-1, 2,2);
		// white "canvas" for the text
		svg.setColor(Color.white);
		svg.fillRoundRect(topX+1, topY-halfHeight, width, height-1, 2,2);
		// textbox itself
		svg.setColor(Color.black);
		svg.drawRoundRect(topX+1, topY-halfHeight, width, height-1, 2,2);

		
		svg.setFont(RuntimeProperties.getInstance().getFontSmall());
		svg.setFont(RuntimeProperties.getInstance().getFontSmall());
		svg.drawString(cardinality.getTo(), topX+3, topY+2); 
		svg.drawString(cardinality.getFrom(), topX+3, topY+10);
			
		svg.setColor(Color.BLACK);
		// #PCDATA
		int endX = topX+width;
		if (isText)
		{
			svg.setStroke(Constants.ROUNDED);  // Solid  
			svg.drawLine(endX-6, topY-halfHeight, endX-6, topY+6-halfHeight);
			svg.drawLine(endX-6, topY+6-halfHeight, endX+1, topY+6-halfHeight);
			svg.drawLine(endX-6, topY-halfHeight, endX+1, topY +6-halfHeight);
		}
		// EMPTY
		if (isEmpty)
		{
			svg.setStroke(Constants.ROUNDED);  // Solid  
			svg.drawOval(endX-6, topY+2-halfHeight, 5, 5);
			svg.drawLine(endX-6, topY+7-halfHeight, endX-1, topY+2-halfHeight);		
		}
	}
}
