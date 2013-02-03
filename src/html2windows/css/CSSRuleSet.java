package html2windows.css;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Iterator;

import org.w3c.dom.DOMException;

/**
 * CSSRuleSet contains several CSS property in it, such as height, width and their value.
 * You can get or set priority to CSSRuleSet.
 * 
 * @author Jason Kuo, Southernbear
 */

public class CSSRuleSet implements Iterable<String>{

	/**
	 * A HashMap that maps property name to its value.
	 */
	private Map<String,Property> properties = new LinkedHashMap<String,Property>();
	
	/**
	 * This CSS rule set's priority.
	 */
	private Origin origin;
	
	/**
	 * Selector text
	 */
	private String selectorText;
	
	/**
	 * Parser for parsing property value
	 */
	private CSSParser parser;

	/**
	 * Property order counter
	 *
	 * Property has larger order may affect property having smaller order.
	 */
	private int orderCounter = 0;
	
	/**
	 * Construct CSSRuleSet and set priority.
	 * 
	 * @param priority		CSSRuleSet's priority
	 */
	public CSSRuleSet(CSSParser parser, Origin origin, String selectorText){
		this.parser = parser;
		this.origin = origin;
		this.selectorText = selectorText;
	}
	
    /**
     * Get CSS rule by given property name.
     * 
     * @param propertyName			property to get
     * @return					property's value
     */
    public String getProperty(String propertyName){
    	Property property = properties.get(propertyName);
    	return property != null ? property.getValue() : null;
    }
    
    public String getPropertyValue(String propertyName){
    	return getProperty(propertyName);
    }
    
    public Object getPropertyComputedValue(String propertyName){
    	Property property = properties.get(propertyName);
    	return property != null ? property.getComputedValue() : null;
    }
    
    public Integer getPropertyOrder(String propertyName){
    	Property property = properties.get(propertyName);
    	return property != null ? property.getOrder() : null;
    }
    
    /**
     * Set new CSS rule to CSSRuleSet.
     * 
     * @param propertyName			property's name
     * @param value				property's value
     */
    public boolean setProperty(String propertyName, String value){
    	try {
    		Property property = new Property(propertyName, value);
    		properties.put(propertyName, property);
    		return true;
		}
		catch (DOMException ex) {
			System.err.println("[Warning] " + ex.getMessage());
		}
		return false;
    }
	
	/**
	 * Get CSSRuleSet's Priority.
	 *
	 * @return	CSSRuleSet's Priority
	 */
	public Origin getOrigin(){
		return origin;
	}
	
	public Iterator<String> iterator(){
		return properties.keySet().iterator();
	}
	
	public String cssText(){
		String text = "";
		TreeSet<String> set = new TreeSet<String>(properties.keySet());
		for (String propertyName : set) {
			Property property = properties.get(propertyName);
			text += propertyName + " : " + property.getValue() + "; ";
		}
		return text.trim();
	}
	
	@Override
	public String toString(){
		return cssText();
	}
    
    /**
     * Property
     */
    private class Property{
    	private String propertyName;
    	private String value;
    	private Object computedValue;
    	
    	private int order;
    	
    	public Property(String propertyName, String value) throws DOMException{
    		this.propertyName = propertyName;
    		this.value = value;
    		this.computedValue = parser != null ?
				parser.parseProperty(propertyName, value) :
				null;
    		this.order = orderCounter++;
    	}
    	
    	public String getPropertyName(){
    		return propertyName;
    	}
    	
    	public String getValue(){
    		return value;
    	}
    	
    	public Object getComputedValue(){
    		return computedValue;
    	}
    	
    	public int getOrder(){
    		return order;
    	}
    }
}
