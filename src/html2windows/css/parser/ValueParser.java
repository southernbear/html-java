package html2windows.css.parser;

import org.w3c.dom.DOMException;

public interface ValueParser{
	public Object parse(String propertyName, String text) throws DOMException;
}
