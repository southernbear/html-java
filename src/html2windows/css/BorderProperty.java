package html2windows.css;

import html2windows.css.value.Edge;

public enum BorderProperty{

	BORDER,
	BORDER_WIDTH,
	BORDER_COLOR,
	BORDER_STYLE,
	BORDER_TOP,
	BORDER_TOP_WIDTH,
	BORDER_TOP_COLOR,
	BORDER_TOP_STYLE,
	BORDER_RIGHT,
	BORDER_RIGHT_WIDTH,
	BORDER_RIGHT_COLOR,
	BORDER_RIGHT_STYLE,
	BORDER_BOTTOM,
	BORDER_BOTTOM_WIDTH,
	BORDER_BOTTOM_COLOR,
	BORDER_BOTTOM_STYLE,
	BORDER_LEFT,
	BORDER_LEFT_WIDTH,
	BORDER_LEFT_COLOR,
	BORDER_LEFT_STYLE;
	
	public Edge getEdge(){
		String[] text = name().split("_");
		if (text.length > 1){
			try {
				return Edge.valueOf(text[2]);
			}
			catch (IllegalArgumentException ex) {
			}
		}
		return null;
	}
	
	public static BorderProperty fromString(String text)
			throws IllegalArgumentException {
		return valueOf(text.toUpperCase().replace("-", "_"));
	}
	
	@Override
	public String toString(){
		return super.toString().toLowerCase().replace("_", "-");
	}
}

