package html2windows.css.parser;

import html2windows.css.BorderProperty;
import html2windows.css.value.CSSBorder;

import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.awt.Color;
import org.w3c.dom.DOMException;

public class BorderParser implements ValueParser{
	
	public Object parse(String propertyName, String cssText) throws DOMException {
		if (cssText == null)
			throw new DOMException(DOMException.SYNTAX_ERR, "Syntax Error");
		
		cssText = cssText.trim();
		if ("".equals(cssText))
			throw new DOMException(DOMException.SYNTAX_ERR, "Syntax Error");
	
		Object value = null;
		
		BorderProperty property = BorderProperty.fromString(propertyName);
		switch (property) {
			case BORDER : {
				CSSBorder.Edge edge = (CSSBorder.Edge)parseEdge(cssText);
				if (edge != null) {
					CSSBorder.Shorthand border = new CSSBorder.Shorthand();
					border.width = edge.width;
					border.color = edge.color;
					border.style = edge.style;
					value = border;
				}
				break;
			}
			
			case BORDER_WIDTH : {
				String[] texts = cssText.split("\\s+");
				value = parseMultiple(Integer.class,
									  getParseMethod("parseWidth"),
									  texts);
				break;
			}
		
			case BORDER_COLOR : {
				String[] texts = cssText.split("\\s+");
				value = parseMultiple(Color.class,
									  getParseMethod("parseColor"),
									  texts);
				break;
			}
			
			case BORDER_STYLE : {
				String[] texts = cssText.split("\\s+");
				value = parseMultiple(CSSBorder.Style.class,
									  getParseMethod("parseStyle"),
									  texts);
				break;
			}
		
			case BORDER_TOP :
			case BORDER_RIGHT :
			case BORDER_BOTTOM :
			case BORDER_LEFT : {
				value = parseEdge(cssText);
				break;
			}
		
			case BORDER_TOP_WIDTH :
			case BORDER_RIGHT_WIDTH :
			case BORDER_BOTTOM_WIDTH :
			case BORDER_LEFT_WIDTH : {
				value = parseWidth(cssText);
				break;
			}
		
			case BORDER_TOP_COLOR :
			case BORDER_RIGHT_COLOR :
			case BORDER_BOTTOM_COLOR :
			case BORDER_LEFT_COLOR : {
				value = parseColor(cssText);
				break;
			}
			
			case BORDER_TOP_STYLE :
			case BORDER_RIGHT_STYLE :
			case BORDER_BOTTOM_STYLE :
			case BORDER_LEFT_STYLE : {
				value = parseStyle(cssText);
				break;
			}
		}
		
		if (value != null)
			return value;
		
		throw new DOMException(DOMException.SYNTAX_ERR, "Syntax Error : '" + cssText + "'");
	}
	
	private Object parseEdge(String cssText){
		String[] texts = cssText.split("\\s+");
		CSSBorder.Edge edge = new CSSBorder.Edge();
		
		edge.width = CSSBorder.Width.MEDIUM;
		edge.color = Color.BLACK;
		edge.style = CSSBorder.Style.NONE;
		
		for (String text : texts) {
			Object value;
			value = parseWidth(text);
			if (value != null) {
				edge.width = (Integer)value;
				continue;
			}
				
			value = parseColor(text);
			if (value != null) {
				edge.color = (Color)value;
				continue;
			}
				
			value = parseStyle(text);
			if (value != null) {
				edge.style = (CSSBorder.Style)value;
				continue;
			}
			
			// Invalid value
			return null;
		}
		
		return edge;
	}
	
	private Object parseWidth(String cssText){
		if ("thin".equals(cssText)) {
			return new Integer(1);
		}
		else if("medium".equals(cssText)) {
			return new Integer(3);
		}
		else if("thick".equals(cssText)) {
			return new Integer(5);
		}
		else {
			try {
				return new LengthParser().parse(null, cssText);
			}
			catch (DOMException ex){
			}
		}
		
		return null;
	}
	
	private Object parseColor(String cssText){
		try {
			Color color = new ColorParser().parse(null, cssText);
			return color;
		}
		catch (DOMException ex){
		}
		
		return null;
	}
	
	private Object parseStyle(String cssText){
		try {
			return CSSBorder.Style.valueOf(cssText.toUpperCase());
		}
		catch (IllegalArgumentException ex) {
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private <T> Object parseMultiple(Class<T> clazz, 
							 Method method, 
							 String[] cssTexts){
		try {
			T[] values = (T[])Array.newInstance(clazz, 4);
			switch (cssTexts.length) {
				case 1 :{
					T value = (T)method.invoke(this, cssTexts[0]);
					values[0] = value;
					values[1] = value;
					values[2] = value;
					values[3] = value;
					break;
				}
				case 2 :{
					T valueH = (T)method.invoke(this, cssTexts[0]);
					T valueV = (T)method.invoke(this, cssTexts[1]);
					values[0] = valueH;
					values[1] = valueV;
					values[2] = valueH;
					values[3] = valueV;
					break;
				}
				case 3 :{
					T valueV = (T)method.invoke(this, cssTexts[1]);
					values[0] = (T)method.invoke(this, cssTexts[0]);
					values[1] = valueV;
					values[2] = (T)method.invoke(this, cssTexts[2]);
					values[3] = valueV;
					break;
				}
				case 4 :{
					values[0] = (T)method.invoke(this, cssTexts[0]);
					values[1] = (T)method.invoke(this, cssTexts[1]);
					values[2] = (T)method.invoke(this, cssTexts[2]);
					values[3] = (T)method.invoke(this, cssTexts[3]);
					break;
				}
			}
			return values;
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	private Method getParseMethod(String methodName){
		try {
			return getClass().getDeclaredMethod(methodName, String.class);
		}
		catch (NoSuchMethodException ex) {
		}
		return null;
	}
}
