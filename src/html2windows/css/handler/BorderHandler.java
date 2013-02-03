package html2windows.css.handler;

import html2windows.css.Style;
import html2windows.css.CSSRuleSet;
import html2windows.css.BorderProperty;
import html2windows.css.value.Edge;
import html2windows.css.value.CSSBorder;

import java.awt.Color;

public class BorderHandler implements PropertyHandler{

	public String getName(){
		return "border";
	}

	/**
	 * TODO : Default color ought to be value of 'color' property
	 */
	public Object initialize(Style style){
		CSSBorder border = new CSSBorder();
		CSSBorder.Shorthand  defaultValue = new CSSBorder.Shorthand();
		defaultValue.width = CSSBorder.Width.MEDIUM;
		defaultValue.color = null;
		defaultValue.style = CSSBorder.Style.NONE;
		border.setBorder(defaultValue);
		return border;
	}
	
	public Object onSetProperty(String propertyName,
								String valueText,
								CSSRuleSet ruleSet,
								Style style,
								Object handlerData){
		CSSBorder border = (CSSBorder)handlerData;
		try {
			BorderProperty property = BorderProperty.valueOf(propertyName);
			Object value = ruleSet.getPropertyComputedValue(propertyName);
			setValue(border, property, value);
		}
		catch (IllegalArgumentException ex) {
			// Skip
		}
		return border;
	}

	public Object onAddCSSRuleSet(CSSRuleSet ruleSet, 
								  Style style,
								  Object handlerData){
		CSSBorder border = (CSSBorder)handlerData;
		
		for (String propertyName : ruleSet) {
			try {
				BorderProperty property = BorderProperty.fromString(propertyName);
				Object value = ruleSet.getPropertyComputedValue(propertyName);
				setValue(border, property, value);
			}
			catch (IllegalArgumentException ex) {
				// Skip
			}
		}
		return border;
	}
	
	private void setValue(CSSBorder border, BorderProperty property, Object value){
		switch (property) {
			case BORDER : {
				CSSBorder.Shorthand shorthand = (CSSBorder.Shorthand)value;
				border.setBorder(shorthand);
				break;
			}
			
			case BORDER_WIDTH : {
				Integer[] width = (Integer[])value;
				border.setWidth(width);
				break;
			}
		
			case BORDER_COLOR : {
				Color[] color = (Color[])value;
				border.setColor(color);
				break;
			}
			
			case BORDER_STYLE : {
				CSSBorder.Style[] style = (CSSBorder.Style[])value;
				border.setStyle(style);
				break;
			}
		
			case BORDER_TOP :
			case BORDER_RIGHT :
			case BORDER_BOTTOM :
			case BORDER_LEFT : {
				CSSBorder.Edge edge = (CSSBorder.Edge)value;
				getEdge(property, border).setEdge(edge);
				break;
			}
		
			case BORDER_TOP_WIDTH :
			case BORDER_RIGHT_WIDTH :
			case BORDER_BOTTOM_WIDTH :
			case BORDER_LEFT_WIDTH : {
				int width = (Integer)value;
				getEdge(property, border).width = width;
				break;
			}
		
			case BORDER_TOP_COLOR :
			case BORDER_RIGHT_COLOR :
			case BORDER_BOTTOM_COLOR :
			case BORDER_LEFT_COLOR : {
				Color color = (Color)value;
				getEdge(property, border).color = color;
				break;
			}
			
			case BORDER_TOP_STYLE :
			case BORDER_RIGHT_STYLE :
			case BORDER_BOTTOM_STYLE :
			case BORDER_LEFT_STYLE : {
				CSSBorder.Style style = (CSSBorder.Style)value;
				getEdge(property, border).style = style;
				break;
			}
		}
	}
	
	private CSSBorder.Edge getEdge(BorderProperty property, CSSBorder border){
		switch(property.getEdge()) {
		case TOP :
			return border.top();
		
		case RIGHT :
			return border.right();
			
		case BOTTOM :
			return border.bottom();
			
		case LEFT :
			return border.left();
			
		default :
			return null;
		}
		
	}
}
