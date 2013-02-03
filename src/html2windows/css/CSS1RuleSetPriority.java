package html2windows.css;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CSS1RuleSetPriority implements CSSRuleSetPriority{
	
	private static int serialCounter = 0;
	
	private Origin origin;

	private int a;
	
	private int b;
	
	private int c;
	
	private int serial;
	
	public CSS1RuleSetPriority(CSSRuleSet ruleSet, String selectorText, int serial){
		this.origin = ruleSet.getOrigin();
		this.serial = serial;
		setSpecificity(selectorText);
	}
	
	public int compareTo(CSSRuleSetPriority o){
		CSS1RuleSetPriority p = null;
		if (o instanceof CSS1RuleSetPriority){
			p = (CSS1RuleSetPriority)o;
		}
		else{
			return this.getClass().getName().compareTo(o.getClass().getName());
		}
		
		int result = 0;
		result = origin.compareTo(p.origin);
		if (result != 0)
			return result;
			
		result = a - p.a;
		if (result != 0)
			return result;
			
		result = b - p.b;
		if (result != 0)
			return result;
			
		result = c - p.c;
		if (result != 0)
			return result;
			
		return serial - p.serial;
	}
	
	private void setSpecificity(String selectorText){
		String ident = "[-]?([_a-z]|[^\0-\177]|\\[0-9a-f]{1,6}(\r\n|[ \n\r\t\f])?|\\[^\n\r\f0-9a-f])([_a-z0-9-]|([^\0-\177])|((\\[0-9a-f]{1,6}(\r\n|[ \n\r\t\f])?)|\\[^\n\r\f0-9a-f]))*";
	
		Matcher matcher;
		// ID selector
		matcher = Pattern.compile("#" + ident).matcher(selectorText);
		while (matcher.find())
			a++;
			
		// Class selector
		matcher = Pattern.compile("\\." + ident).matcher(selectorText);
		while (matcher.find())
			b++;
			
		// Type selector
		matcher = Pattern.compile(ident).matcher(selectorText);
		while (matcher.find())
			c++;
	}
}
