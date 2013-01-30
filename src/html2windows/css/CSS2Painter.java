package html2windows.css;

import html2windows.dom.Element;

import java.awt.Graphics;

public class CSS2Painter implements CSSPainter {
	private CSSPainter backgroundPainter = new BackgroundPainter();

	private CSSPainter borderPainter = new BorderPainter();

	private CSSPainter fontPainter = new FontPainter();


	public void paint(Style style, Element element, Graphics g){
		backgroundPainter.paint(style, element, g);
		borderPainter.paint(style, element, g);
		fontPainter.paint(style, element, g);
	}
}
