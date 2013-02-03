package html2windows.css;

import html2windows.css.handler.PropertyHandler;

import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.NavigableSet;
import java.util.TreeSet;

import html2windows.dom.Document;
import html2windows.dom.Element;

/**
 * Style simulate CSS's style. It contains a set of CSSRuleSets.
 * You can add CSSRuleSet to Style or set property to CSSRuleSet in Style.
 * You can also get properties from CSSRuleSet in Style in the order of priority.
 * 
 * @author Jason Kuo, Southernbear
 */

public class Style {

	/**
	 * Define CSSRuleSet's max priority as 5
	 */
	private static final int MAX_PRIORITY=5;
	
	/**
	 * Element that own this style
	 */
	private Element element;
	
	/**
	 * Comparator that compare CSSRuleSet with priority
	 */
	private Comparator<CSSRuleSet> comparator = new CssRuleSetComparator();
	
	/**
	 * TreeSet of CSSRuleSet
	 */
	private TreeSet <CSSRuleSet> iterateRuleSets =
		new TreeSet<CSSRuleSet>(comparator);
		
	private NavigableSet<CSSRuleSet> searchRuleSets;
	
	/**
	 * TreeSet of CSSRuleSet
	 */
	private HashMap<CSSRuleSet, CSSRuleSetPriority> ruleSetPriorities =
		new HashMap<CSSRuleSet, CSSRuleSetPriority>();
	
	/**
	 * Data storage for CSS module
	 */
	private Map<String, Object> handlerDataMap = new HashMap<String, Object>();
	
	private int orderCounter = 0;
	
	/**
	 * Create style and add it to CSSRuleSet
	 * 
	 * @param element 			parent node
	 */
	public Style(Element element){
		this.element=element;
		this.searchRuleSets = iterateRuleSets.descendingSet();
		
		Document document = element.ownerDocument();
		CSSParser parser = document.getCSSParser();
		
		initialize();
		
		CSSRuleSet inline = new CSSRuleSet(parser, Origin.INLINE, null);
		addCSSRuleSet(inline, "");
	}
	
    /**
     * Set CssRuleSet's property to first of CSSRuleSet
     * 
     * @param propertyName 			inserted property name
     * @param value		 		inserted property value
     */
    public void setProperty(String propertyName, String value){
        boolean successful = searchRuleSets.first().setProperty(propertyName,value);
        if (!successful)
        	return;
        	
    	Document document = element.ownerDocument();
    	PropertyHandler[] handlers = document.getCSSPropertyHandlers();
    	for (PropertyHandler handler : handlers) {
			Object data = handlerDataMap.get(handler.getName());
			data = handler.onSetProperty(propertyName,
										 value,
										 searchRuleSets.first(),
										 this,
										 data);
			handlerDataMap.put(handler.getName(), data);
    	}
    }
    
    /**
     * Get property value according to the order of treeSet(property)
     * 
     * @param propertyName			property name
     * @return					property's value, return null if property value is null
     */
    public String getProperty(String propertyName){
    	String value=null;
    	for(CSSRuleSet ruleSet : searchRuleSets){
    		value=ruleSet.getProperty(propertyName);
    		if(value!=null)
    			return value;
    	}
        return null;	
    }
    
    /**
     * Return computed value of property
     *
     * Computed value is parsed value, not final value.
     *
     * @param propertyName Name of property
     *
     * @return Computed value of property or null if value is not specified or
     *         failed to parse.
     */
    public Object getPropertyComputedValue(String propertyName){
    	Object value;
    	for (CSSRuleSet ruleSet : searchRuleSets) {
    		value = ruleSet.getPropertyComputedValue(propertyName);
    		if (value != null) {
    			return value;
    		}
    	}
    	return null;
    }
  
    /**
     * Add new CSSRuleSet to Style
     * 
     * @param cssRuleSet		CSSRuleSet to be added
     */
    public void addCSSRuleSet(CSSRuleSet cssRuleSet, String selectorText){
    	Document document = element.ownerDocument();
    	CSSRuleSetPriority priority =
    		document.getCSSRuleSetPriority(cssRuleSet, selectorText, orderCounter++);
    		
		ruleSetPriorities.put(cssRuleSet, priority);
		iterateRuleSets.add(cssRuleSet);
    	
    	evaluate();
    }
    
    /**
     * Get element that own this style
     * 
     * @return			owner of the Style 
     */
    public Element getElement(){
        return element;
    }
    
    public Object getPropertyHandlerData(String handlerName){
    	return handlerDataMap.get(handlerName);
    }
    
    private void initialize(){
    	Document document = element.ownerDocument();
    	PropertyHandler[] handlers = document.getCSSPropertyHandlers();
    	
    	// Refresh
    	for (PropertyHandler handler : handlers) {
			Object data = handler.initialize(this);
			handlerDataMap.put(handler.getName(), data);
    	}
    }
    
    private void evaluate(){
    	initialize();
    	
    	Document document = element.ownerDocument();
    	PropertyHandler[] handlers = document.getCSSPropertyHandlers();
    	
    	for (CSSRuleSet cssRuleSet : iterateRuleSets) {
			for (PropertyHandler handler : handlers) {
				Object data = handlerDataMap.get(handler.getName());
				data = handler.onAddCSSRuleSet(cssRuleSet, this, data);
				handlerDataMap.put(handler.getName(), data);
			}
    	}
    }
    
    /**
     * CSSRuleSet's comparator
     * Compare CSSRuleSets with their priority
     */
    public class CssRuleSetComparator implements Comparator<CSSRuleSet>{
		
    	/**
    	 *	Compare two CSSRuleSets with their priority
    	 *	
    	 *	@return		return 1 if o1>o2, otherwise -1 
    	 */
    	@Override
		public int compare(CSSRuleSet o1, CSSRuleSet o2) {
			CSSRuleSetPriority p1 = ruleSetPriorities.get(o1),
							   p2 = ruleSetPriorities.get(o2);
			return p1.compareTo(p2);
		}
	}
}
