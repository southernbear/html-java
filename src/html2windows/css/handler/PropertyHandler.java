package html2windows.css.handler;

import html2windows.dom.Element;

import html2windows.css.Style;
import html2windows.css.CSSRuleSet;

public interface PropertyHandler{
	
	public String getName();
	
	public Object initialize(Style style);

	public Object onSetProperty(String propertyName,
								String value,
								CSSRuleSet ruleSet,
								Style style,
								Object handlerData);

	public Object onAddCSSRuleSet(CSSRuleSet ruleSet, 
								  Style style,
								  Object handlerData);
}
