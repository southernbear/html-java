package html2windows.css.handler;

import html2windows.dom.Element;

import html2windows.css.Style;
import html2windows.css.CSSRuleSet;
import html2windows.css.BlockLayout;
import html2windows.css.handler.PropertyHandler;
import html2windows.css.value.Display;

public class DisplayHandler implements PropertyHandler{
	
	public String getName(){
		return "display";
	}
	
	public Object initialize(Style style){
		Display display = Display.BLOCK;
		setDisplay(display, style.getElement());
		return display;
	}
	
	public Object onSetProperty(String propertyName,
								String value,
								CSSRuleSet ruleSet,
								Style style,
								Object handlerData){
		Display display = (Display)handlerData;
		value = value != null ? value.trim() : "";
		
		Element element = style.getElement();
		display = Display.valueOf(value.toUpperCase());
		setDisplay(display, element);
		return display;
	}

	public Object onAddCSSRuleSet(CSSRuleSet ruleSet, 
								  Style style, 
								  Object handlerData){
		Display display = (Display)handlerData;
		String value = ruleSet.getPropertyValue("display");
		
		if (value != null) {
			value = value.trim();
			Element element = style.getElement();
			
			display = Display.valueOf(value.toUpperCase());
			setDisplay(display, element);
		}
		return display;
	}
	
	private void setDisplay(Display display, Element element){
		switch (display) {
			case NONE :
				element.setVisible(false);
				element.setLayout(null);
				break;
				
			case BLOCK :
			default :
				element.setLayout(new BlockLayout());
		}
		
	}
}
