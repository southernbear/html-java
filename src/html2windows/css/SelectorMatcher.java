package html2windows.css;

import java.util.List;

import html2windows.dom.Document;
import html2windows.dom.Element;
import html2windows.util.Pair;

public interface SelectorMatcher {
    public List<Pair<Element, String>>
    	getElementBySelector(String selector, Document document);
}
