package de.bsd.x2svg.draw;

import de.bsd.x2svg.RuntimeProperties;
import org.apache.batik.svggen.SVGGraphics2D;

import java.awt.*;

/**
 *
 */
public class TypeModel extends ContentModel {
    @Override
    public void draw(SVGGraphics2D svg, int topX, int topY) {
        // We do on purpose not draw the super() container
        // now draw dashes
        boolean simpleShadow = RuntimeProperties.getInstance().isSimple_shadow();

        int yHalf = topY + 7; // TODO  7 relative to font
        svg.setColor(Color.BLACK);
        drawDashes(svg, topX, yHalf);

        if (simpleShadow)
            svg.setColor(Color.GRAY);
        else
            svg.setColor(Color.DARK_GRAY);
        drawDashes(svg, topX, yHalf-1);

    }

    private void drawDashes(SVGGraphics2D svg, int topX, int yHalf) {
        svg.drawLine(3+ topX, yHalf, 6+ topX, yHalf);
        svg.drawLine(8+ topX, yHalf, 10+ topX, yHalf);
        svg.drawLine(12+ topX, yHalf, 15+ topX, yHalf);
    }
}
