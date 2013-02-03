package html2windows.css;

import html2windows.css.Style;
import html2windows.css.value.Orientation;
import html2windows.css.value.CSSBorder;
import html2windows.css.handler.BorderHandler;

import html2windows.dom.Element;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

public class BorderPainter implements CSSPainter{
	
    /**
     * It will paint ,when frame add or setVisible , Our Manager will start paint
     *
     * @param style     style is css style
     * @param element   element also can get his style
     * @param g         Graphic is painted by Font Painter
     *
     */
    public void paint(Style style, Element element, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        CSSBorder border = getBorder(style);
        
        int compWidth  = element.getWidth();
        int compHeight = element.getHeight();
        
        CSSBorder.Edge[] edges = new CSSBorder.Edge[]{
        	border.top(), border.right(), border.bottom(), border.left()};
        
        int[][] startTL = new int[][]{
        	new int[]{0, 0},
        	new int[]{0, compWidth - border.right().width},
        	new int[]{compHeight - border.bottom().width, 0},
        	new int[]{0, 0}
        };
		  
        int[][] clipTLInner = new int[][]{
        	new int[]{border.top().width,
        			  border.left().width},
        	new int[]{border.top().width,
        			  compWidth - border.right().width},
        	new int[]{compHeight - border.bottom().width,
        			  compWidth - border.right().width},
        	new int[]{compHeight - border.bottom().width,
        			  border.left().width}
        };

        int[][] clipTLOuter = new int[][]{
        	new int[]{0, 0},
        	new int[]{0, compWidth},
        	new int[]{compHeight, compWidth},
        	new int[]{compHeight, 0}
        };

		for (int i = 0; i < edges.length; i++) {
			CSSBorder.Edge edge = edges[i];
			Orientation orientation =
				(i % 2 == 0) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
			int length = i % 2 == 0 ? compWidth : compHeight;
		    if (edge.style != CSSBorder.Style.NONE){
		    	int width = edge.width;
		    	Color color = edge.color;
		    	if (color == null) {
		    		color = (Color)style.getPropertyComputedValue("color");
		    		if (color == null) {
		    			color = Color.BLACK;
		    		}
		    	}
		    	
		    	if (width > 0) {
		    		g.setColor(color);
		    		
		    		int j = (i + 1) % 4;
		    		
		    		GeneralPath p = new GeneralPath();
		    		p.moveTo(clipTLOuter[i][1], clipTLOuter[i][0]);
		    		p.lineTo(clipTLInner[i][1], clipTLInner[i][0]);
		    		p.lineTo(clipTLInner[j][1], clipTLInner[j][0]);
		    		p.lineTo(clipTLOuter[j][1], clipTLOuter[j][0]);
		    		p.closePath();
		    		
		    		g2d.clip(p);
		    		
		    		switch (edge.style) {
		    			case SOLID :
	    					paintSolid(g2d,
	    								orientation,
	    								startTL[i][0],
	    								startTL[i][1],
	    								width,
	    								compWidth);
		    				break;
	    				case DOUBLE :
	    					paintDouble(g2d,
	    								orientation,
	    								startTL[i][0],
	    								startTL[i][1],
	    								width,
	    								compWidth);
							break;
	    				case DOTTED :
	    					paintDotted(g2d,
	    								orientation,
	    								startTL[i][0],
	    								startTL[i][1],
	    								width,
	    								compWidth);
							break;
	    				case DASHED :
	    					paintDashed(g2d,
	    								orientation,
	    								startTL[i][0],
	    								startTL[i][1],
	    								width,
	    								compWidth);
							break;
		    		}
		    		
		    		g2d.setClip(null);
		    	}
		    }
	    }
    }
	
	private CSSBorder getBorder(Style style){
		return (CSSBorder)style.getPropertyHandlerData("border");
	}
	
	private void paintSolid(Graphics2D g,
							Orientation orientation,
							int top,
							int left,
							int width,
							int length){
		g.setStroke(new BasicStroke(width));
		
		int x1 = cell(left + width * 0.5 * vertical(orientation));
		int y1 = cell(top  + width * 0.5 * horizontal(orientation));
		int x2 = x1 + length * horizontal(orientation);
		int y2 = y1 + length * vertical(orientation);
		
		g.drawLine(x1, y1, x2, y2);
	}
	
	private void paintDouble(Graphics2D g,
								Orientation orientation,
								int top,
								int left,
								int width,
								int length){
		double lineWidth = Math.ceil(width / 3);
		g.setStroke(new BasicStroke((float)lineWidth));
		{
			int x1 = cell(left + lineWidth * 0.5 * vertical(orientation));
			int y1 = cell(top  + lineWidth * 0.5 * horizontal(orientation));
			int x2 = x1 + length * horizontal(orientation);
			int y2 = y1 + length * vertical(orientation);
			g.drawLine(x1, y1, x2, y2);
		}
		{
			int x1 = cell(left + lineWidth * 2.5 * vertical(orientation));
			int y1 = cell(top  + lineWidth * 2.5 * horizontal(orientation));
			int x2 = x1 + length * horizontal(orientation);
			int y2 = y1 + length * vertical(orientation);
			g.drawLine(x1, y1, x2, y2);
		}
	}
	
	private void paintDotted(Graphics2D g,
							 Orientation orientation,
							 int top,
							 int left,
							 int width,
							 int length){
		g.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE,
					BasicStroke.JOIN_MITER, 10f,
					new float[] {0, width * 2}, width * 1.5f));
		
		int x1 = cell(left + width * 0.5 * vertical(orientation));
		int y1 = cell(top  + width * 0.5 * horizontal(orientation));
		int x2 = x1 + length * horizontal(orientation);
		int y2 = y1 + length * vertical(orientation);
		
		g.drawLine(x1, y1, x2, y2);
	}
	
	private void paintDashed(Graphics2D g,
							 Orientation orientation,
							 int top,
							 int left,
							 int width,
							 int length){
		g.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE,
					BasicStroke.JOIN_MITER, 10f,
					new float[] {width, width * 3}, 0f));
		
		int x1 = cell(left + width * 0.5 * vertical(orientation));
		int y1 = cell(top  + width * 0.5 * horizontal(orientation));
		int x2 = x1 + length * horizontal(orientation);
		int y2 = y1 + length * vertical(orientation);
		
		g.drawLine(x1, y1, x2, y2);
	}
	
	private int horizontal(Orientation orientation){
		return (orientation == Orientation.HORIZONTAL ? 1 : 0);
	}
	
	private int vertical(Orientation orientation){
		return (orientation == Orientation.VERTICAL ? 1 : 0);
	}
	
	private int cell(double d){
		return (int)Math.floor(d);
	}
}
