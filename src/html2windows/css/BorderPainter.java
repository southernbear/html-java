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
import java.util.HashMap;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BorderPainter extends JPanel implements CSSPainter{
	
	private Graphics2D g2d;
	private HashMap<String, String> property = new HashMap<String, String>();
	
	/**
	 * height, width, top, left values of border.
	 */
	private int height=10;
	private int width=10;
	private int top=0;
	private int left=0;
	private int borderWidth = 2;

    public BorderPainter() { 
        initial();
    }

    /**
     * initial some property ,ex: font-size is 12 ...
     */
    private void initial() {
    	property.put("border-width","2");
		property.put("width","10");
		property.put("height","10");
		property.put("top","10");
		property.put("left","5");
		property.put("bottom","10");
		property.put("border-style","solid");
		property.put("border-color","black");
    }

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
        
        int[][] start = new int[][]{
        	new int[]{0, 0},
        	new int[]{0, compWidth - border.right().width},
        	new int[]{compHeight - border.bottom().width, 0},
        	new int[]{0, 0}
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
		    		
		    		switch (edge.style) {
		    			case SOLID :
	    					paintSolid(g2d,
	    								orientation,
	    								start[i][0],
	    								start[i][1],
	    								width,
	    								compWidth);
		    				break;
	    				case DOUBLE :
	    					paintDouble(g2d,
	    								orientation,
	    								start[i][0],
	    								start[i][1],
	    								width,
	    								compWidth);
							break;
	    				case DOTTED :
	    					paintDotted(g2d,
	    								orientation,
	    								start[i][0],
	    								start[i][1],
	    								width,
	    								compWidth);
							break;
		    		}
		    	}
		    }
	    }
    }


    /**
     * Function will get Font style  
     *
     * @param style     element style
     * @param name      style name, ex: font-size
     *
     */
    private void getBorderStyle(Style style,String name) {
        String type = style.getProperty(name);
        if( type != null ) {
            this.property.put(name, type) ;    
        }
    }

    
    
	
    /**
     * function will set Color     
     *
     */
    private void setColor(){
		String color=property.get("border-color").toLowerCase();
		g2d.setColor(null);
    }
	
	/**
	 * Set border width according to user defined border-width
	 * 
	 * @throws NumberFormatException
	 */
	private void setBorderWidth(){
		try{
			borderWidth=Integer.parseInt(property.get("border-width"));
	    }
	    catch (NumberFormatException e){
	    }
	}
	
	/**
	 * Set width according to user defined width
	 * 
	 * @throws NumberFormatException
	 */
	private void setWidth() throws NumberFormatException{
		try{
			width=Integer.parseInt(property.get("width"));
	    }
	    catch (NumberFormatException e){
	    }
	}
	
	/**
	 * Set height according to user defined height
	 * 
	 * @throws NumberFormatException
	 */
	private void setHeight() throws NumberFormatException{
		try{
			height=Integer.parseInt(property.get("height"));
	    }
	    catch (NumberFormatException e){
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
	
	private int horizontal(Orientation orientation){
		return (orientation == Orientation.HORIZONTAL ? 1 : 0);
	}
	
	private int vertical(Orientation orientation){
		return (orientation == Orientation.VERTICAL ? 1 : 0);
	}
	
	private int cell(double d){
		return (int)Math.floor(d);
	}
	
	private Stroke setStroke(){
		Stroke s;
		if (property.get("border-style").equals("dotted")) {
			s = new BasicStroke(10, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND, 5,
					new float[] { 5 }, 10);
			return s;
		} else if (property.get("border-style").equals("dashed")) {
			s = new BasicStroke(borderWidth, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, borderWidth,
					new float[] { borderWidth*2,borderWidth }, 0f);
			return s;
		} else if (property.get("border-style").equals("solid")) {
			s = new BasicStroke(borderWidth);
			return s;

		} else if (property.get("border-style").equals("double")) {
			s = new BasicStroke(2);
			return s;
		}
		return null;
	}
	
}
