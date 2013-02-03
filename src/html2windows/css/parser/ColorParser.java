package html2windows.css.parser;

import html2windows.css.value.ColorKeyword;

import java.awt.Color;
import java.lang.Math;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.w3c.dom.DOMException;

public class ColorParser implements ValueParser{

	public Color parse(String propertyName, String cssText) throws DOMException {
		if (cssText == null)
			throw new DOMException(DOMException.SYNTAX_ERR, "Syntax Error : '" + cssText + "'");
		
		cssText = cssText.trim();
		
		if ("".equals(cssText))
			throw new DOMException(DOMException.SYNTAX_ERR, "Syntax Error : '" + cssText + "'");
	
		Color value;
		
		try {
			ColorKeyword keyword = ColorKeyword.fromString(cssText.toUpperCase());
			value = keyword.getColor();
		}
		catch (IllegalArgumentException ex) {
			if ("transparent".equals(cssText)) {
				value = new Color(0, 0, 0, 0);
			}
			else if(cssText.matches("#[0-9A-Fa-f]{6}")){
		        String firstColor=cssText.substring(1, 3);
		        int firstColorNum=Integer.parseInt(firstColor, 16);

		        String secondColor=cssText.substring(3,5);
		        int secondColorNum=Integer.parseInt(secondColor, 16);

		        String thirdColor=cssText.substring(5,7);
		        int thirdColorNum=Integer.parseInt(thirdColor, 16);

		        value = new Color(firstColorNum,secondColorNum,thirdColorNum);
			}
			else {
				value = parseFunctional(cssText, true, false, false);
			if (value == null) {
				value = parseFunctional(cssText, true, false, true);
			if (value == null) {
				value = parseFunctional(cssText, true, true, false);
			if (value == null) {
				value = parseFunctional(cssText, true, true, true);
			if (value == null) {
				value = parseFunctional(cssText, true, false, false);
			if (value == null) {
				value = parseFunctional(cssText, false, true, false);
			}}}}}}
        }
        
        if (value != null)
        	return value;
        	
		throw new DOMException(DOMException.SYNTAX_ERR, "Syntax Error : '" + cssText + "'");
	}
	
	private Color parseFunctional(String cssText,
								boolean RGB,
								boolean alpha,
								boolean percent){
		String pattern = RGB ? "rgb" : "hsl";
		if (alpha)
			pattern += "a";
		pattern += "\\s*\\(";
		pattern += "\\s*(\\d+)" + ((percent && RGB) ? "%" : "") + "\\s*";
		pattern += ",\\s*(\\d+)" + (percent || !RGB ? "%" : "") + "\\s*";
		pattern += ",\\s*(\\d+)" + (percent || !RGB ? "%" : "") + "\\s*";
		if (alpha)
			pattern += ",\\s*(\\d*.\\d+)" + "\\s*";
		pattern += "\\)";
		
		Pattern compile = Pattern.compile(pattern);
		Matcher matcher = compile.matcher(cssText);
		if (matcher.matches()) {
			float[] value = new float[4];
			value[0] = new Float(matcher.group(1));
			value[1] = new Float(matcher.group(2));
			value[2] = new Float(matcher.group(3));
			value[3] = alpha ? new Float(matcher.group(4)) : 1;
			
			if (RGB)
				value[0] = limit(value[0] / (percent ? 100 : 255));
			else
				value[0] = (((value[0] % 360) + 360) % 360) / 360;
				
			value[1] = value[1] / (RGB ? (percent ? 100 : 255) : 100);
			value[2] = value[2] / (RGB ? (percent ? 100 : 255) : 100);
			
			if (RGB) {
				return new Color(value[0], value[1], value[2], value[3]);
			}
			else {
				return getHSLA(value);
			}
		}
		return null;
	}
	
	private Color getHSLA(float ... value){
		int rgb = Color.HSBtoRGB(value[0], value[1], value[2])
				| (int)(255 * value[3]) << 24;
		return new Color(rgb, true);
	}
	
	private float limit(float value){
		return Math.min(1, Math.max(0, value));
	}
}
