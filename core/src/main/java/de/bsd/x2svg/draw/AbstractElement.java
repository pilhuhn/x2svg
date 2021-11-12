/**
 *
 */
package de.bsd.x2svg.draw;

import java.awt.Color;

import org.apache.batik.svggen.SVGGraphics2D;

import de.bsd.x2svg.Cardinality;
import de.bsd.x2svg.RuntimeProperties;

/**
 * This class represents an abstract XSD Element, that can not exist
 * on its own, but serves as parent for inherited elements.
 * <p/>
 * Example: <br/>
 * <img src="{@docRoot}/../img/abstractElement.png"/>
 * <p/>
 * @author hwr@pilhuhn.de
 * @since 1.2
 */
public class AbstractElement extends ElementBox {

    /**
     * Constructor to use
     * @param name The name of the element
     * @param card The cardinality
     */
    public AbstractElement(String name, Cardinality card) {
        super(name, card);
        textColor = RuntimeProperties.getInstance().getTextColor();

    }

    @Override
    public void draw(SVGGraphics2D svg, int topX, int topY, int width, int height, boolean isText, boolean isEmpty) {
        super.draw(svg, topX, topY, width, height, isText, isEmpty);
        int halfHeight = RuntimeProperties.getInstance().getFontHeight() / 2;

        svg.setColor(textColor);
        svg.setFont(RuntimeProperties.getInstance().getFont());
        svg.drawString(name, topX + 10, topY + halfHeight); // center ?

        int endX = topX + width;

        svg.setColor(Color.BLACK);

        // Now draw the 'A', that denotes this as abstract
        svg.setFont(RuntimeProperties.getInstance().getFontSmall());
        svg.drawString("A", endX - 6, topY + 2);
    }
}
