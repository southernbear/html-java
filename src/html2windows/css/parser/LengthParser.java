package html2windows.css.parser;

import org.w3c.dom.DOMException;

public class LengthParser implements ValueParser{
	public Integer parse(String propertyName, String cssText) throws DOMException {
		if(cssText != null) {
			String numString = null;
			
            if( cssText.matches("[0-9]+") ) {
                numString = cssText;
            }
            else if(cssText.matches("[0-9]+px")) {
                numString = cssText.replaceAll("([0-9]+)px","$1");
            }
            else if(cssText.matches("[0-9]+em")) {
                numString = cssText.replaceAll("([0-9]+)em","$1");
            }
            
            if (numString != null)
            	return Integer.parseInt(numString);
        }
        
		throw new DOMException(DOMException.SYNTAX_ERR, "Syntax Error");
	}
}
