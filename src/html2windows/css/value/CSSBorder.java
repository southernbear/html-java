package html2windows.css.value;

import java.awt.Color;

public class CSSBorder{
	
	public static class Width{
		public static final int THIN = 1;
		public static final int MEDIUM = 3;
		public static final int THICK = 5;
	}
	
	public enum Style{
		NONE,
		HIDDEN,
		DOTTED,
		DASHED,
		SOLID,
		DOUBLE,
		GROOVE,
		RIDGE,
		INSET,
		OUTSET;
	}
	
	public enum EdgeType{
		TOP,
		RIGHT,
		BOTTOM,
		LEFT
	}
	
	private Edge top = new Edge();
	private Edge right = new Edge();
	private Edge bottom = new Edge();
	private Edge left = new Edge();
	
	public Edge top(){
		return top;
	}
	
	public Edge right(){
		return right;
	}
	
	public Edge bottom(){
		return bottom;
	}
	
	public Edge left(){
		return left;
	}
	
	public void setWidth(int width){
		top.width = width;
		right.width = width;
		bottom.width = width;
		left.width = width;
	}
	
	public void setWidth(Integer[] width){
		top.width = width[0];
		right.width = width[1];
		bottom.width = width[2];
		left.width = width[3];
	}
	
	public void setColor(Color color){
		top.color = color;
		right.color = color;
		bottom.color = color;
		left.color = color;
	}
	
	public void setColor(Color[] color){
		top.color = color[0];
		right.color = color[1];
		bottom.color = color[2];
		left.color = color[3];
	}
	
	public void setStyle(Style style){
		top.style = style;
		right.style = style;
		bottom.style = style;
		left.style = style;
	}
	
	public void setStyle(Style[] style){
		top.style = style[0];
		right.style = style[1];
		bottom.style = style[2];
		left.style = style[3];
	}
	
	public void setBorder(Shorthand src){
		setWidth(src.width);
		setColor(src.color);
		setStyle(src.style);
	}
	
	static public class Edge{
		public int width;
		public Color color;
		public Style style;
	
		public void setEdge(Edge edge){
			width = edge.width;
			color = edge.color;
			style = edge.style;
		}
		
		@Override
		public String toString(){
			return width + "px " + style.toString().toLowerCase() + " " + color;
		}
	}
	
	static public class Shorthand{
		public int width;
		public Color color;
		public Style style;
	}
}
