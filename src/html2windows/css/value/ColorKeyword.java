package html2windows.css.value;

import java.awt.Color;

public enum ColorKeyword{
	
	MAROON  (new Color(128,0,0)),
	RED     (new Color(255,0,0)),
	ORANGE  (new Color(255,165,0)),
	YELLOW  (new Color(255,255,0)),
	OLIVE   (new Color(128,128,0)),
	PURPLE  (new Color(128,0,128)),
	FUCHSIA (new Color(255,0,255)),
	WHITE   (new Color(255,255,255)),
	LIME    (new Color(0,255,255)),
	GREEN   (new Color(0,255,0)),
	NAVY    (new Color(0,0,128)),
	BLUE    (new Color(0,0,255)),
	AQUA    (new Color(0,255,255)),
	TEAL    (new Color(0,128,128)),
	BLACK   (new Color(0,0,0)),
	SILVER  (new Color(192,192,192)),
	GRAY    (new Color(128,128,128));
	
	private Color color;
	
	private ColorKeyword(Color color){
		this.color = color;
	}
	
	public Color getColor(){
		return color;
	}
	
	public static ColorKeyword fromString(String text) 
			throws IllegalArgumentException {
		return valueOf(text.toUpperCase());
	}
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
}
