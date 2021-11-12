/**
 *
 */
package de.bsd.x2svg.draw;

import java.awt.Color;
import java.awt.Font;

import org.apache.batik.svggen.SVGGraphics2D;

import de.bsd.x2svg.Cardinality;
import de.bsd.x2svg.RuntimeProperties;

/**
 * This class represents an XSD substitution group.
 * <p/>
 * Example: <br/>
 * <img src="{@docRoot}/../img/SubstitutionElement.png"/>
 * <p/>
 * @author hwr@pilhuhn.de
 * @since 1.2
 */
public class SubstitutionElement extends ElementBox {

    private boolean isSubstitution = false;

    /**
     * Constructor to use
     * @param name The name of the element
     * @param card The cardinality
     * @param substitution when true it is a substitution group, else a type
     */
    public SubstitutionElement(String name, Cardinality card, boolean substitution) {
        super(name, card);
        this.isSubstitution = substitution;
        textColor = RuntimeProperties.getInstance().getTextColor();

    }

    @Override
    public void draw(SVGGraphics2D svg, int topX, int topY, int width, int height, boolean isText, boolean isEmpty) {
        Color background = svg.getBackground();
        svg.setBackground(Color.YELLOW);
        super.draw(svg, topX, topY, width, height, isText, isEmpty);
        svg.setBackground(background);
        int halfHeight = RuntimeProperties.getInstance().getFontHeight() / 2;

        svg.setColor(textColor);
        Font font = RuntimeProperties.getInstance().getFont();
        Font italics = font.deriveFont(Font.ITALIC);
        svg.setFont(italics);
        svg.drawString(name, topX + 10, topY + halfHeight); // center ?
        svg.setFont(font);
        int endX = topX + width;

        svg.setColor(Color.BLACK);

        // Now draw the 'S', that denotes this as SG
        // Or the 'T' that denotes a type
        svg.setFont(RuntimeProperties.getInstance().getFontSmall());
        if (isSubstitution)
            svg.drawString("S", endX - 6, topY + 2);
        else
            svg.drawString("T", endX - 6, topY + 2);
    }
}
