package html2windows.css;

import html2windows.css.Style;
import html2windows.css.parser.ColorParser;
import html2windows.dom.Element;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Draw border by border painter according to some 
 * user defined property in style.
 *  
 * @author Jason Kuo
 */

public class BackgroundPainter implements CSSPainter{

    /**
     * Function to paint. First, get property from user defined style.
     * Then, set color, stroke and draw on graphic.
     *
     * @param style     user defined style property
     * @param element   element to be drawn
     * @param g         graphic to draw on
     */
    public void paint(Style style, Element element, Graphics g) {
		Color color = (Color)style.getPropertyComputedValue("background-color");
		if (color != null) {
			g.setColor(color);
		    g.fillRect(0, 0, element.getWidth(), element.getHeight());
        }
    }
}
