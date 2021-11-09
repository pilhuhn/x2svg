/**
 * 
 */
package de.bsd.x2svg.draw;

import java.awt.Color;

import org.apache.batik.svggen.SVGGraphics2D;

import de.bsd.x2svg.Cardinality;
import de.bsd.x2svg.Constants;
import de.bsd.x2svg.RuntimeProperties;

/**
 * This class represents a reference to another location
 * where this element is actually defined.
 * <p/>
 * Example: <br/>
 * <img src="{@docRoot}/../img/reference.png"/>
 * <p/>

 * @author hwr@pilhuhn.de
 * @since 1.2
 */
public class Reference extends ElementBox 
{

	/**
	 * Constructor to use
	 * @param name The name of the element
	 * @param card The cardinality
	 */
	public Reference(String name, Cardinality card)
	{
		super(name,card);
		textColor = RuntimeProperties.getInstance().getTextColor();
		
	}

	@Override
	public void draw(SVGGraphics2D svg, int topX, int topY, int width, int height, boolean isText, boolean isEmpty)
	{
		super.draw(svg, topX, topY, width, height, isText, isEmpty );
		int halfHeight = RuntimeProperties.getInstance().getFontHeight() / 2;

		svg.setColor(textColor);
		svg.setFont(RuntimeProperties.getInstance().getFont());
		svg.drawString(name, topX+10, topY  + halfHeight); // center ?

		int endX = topX+width;
		
		svg.setColor(Color.BLACK);

		// Now draw that circle thingy with an arrow at the end
		svg.setStroke(Constants.ROUNDED);
		svg.drawArc(endX-6, topY-3, 5, 5, 180, 270);
		svg.drawLine(endX-5, topY-3, endX-4, topY-2);
		svg.drawLine(endX-5, topY-3, endX-4, topY-4);
	}
}
