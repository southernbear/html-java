package html2windows.css;

import html2windows.dom.Element;
import java.awt.*;

import java.util.*;

/**
 * layout manager control all css position and layout
 *
 * @author bee040811
 */
public class BlockLayout implements LayoutManager {

    /**
     * preferredWidth mean that preferred width
     */
    private int preferredWidth = 0;

    /**
     * preferredHeight mean that preferred height 
     */
    private int preferredHeight = 0;

	/**
	 * offsetHeight contains content, padding, and border height
	 */
	private int offsetHeight = 0;

	/**
	 * Top margins consists of collapsed top margins
	 */
	private int[] topMargins = new int[0];
	
	/**
	 * Bottom margins consists of collapsed bottom margins
	 */
	private int[] bottomMargins = new int[0];

    /**
     * sizeUnknown mean that before know or not setting size
     */
    private boolean sizeUnknown = true;

    /**
     * Required by LayoutManager. 
     *
     * @param name
     * @param comp
     *
     * @return   
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Required by LayoutManager. 
     *
     * @param comp
     *
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * This is called when the panel is first displayed,
     * and every time its size changes,
     * Note: You CAN'T assume preferredLayoutSize or
     * minimumLayoutSize will be called -- in the case
     * of applets, at least, they probably won't be.
     *
     * @param parent    Container
     *
     */
    public void layoutContainer(Container parent) {
        if(parent instanceof Element && 
        	"none".equals(((Element)parent).getStyle().getProperty("display"))) {
        	return;
        }
        
        // Go through the components' sizes, if neither
        // preferredLayoutSize nor minimumLayoutSize has
        // been called.
        if (sizeUnknown) {
            setSizes(parent);
        }
        
        int x, y, x0, y0;

		int contentWidth = parent.getWidth();
		int contentHeight = parent.getHeight();
        
        SpaceList spaces = new SpaceList();
        
        if (parent instanceof Element) {
        	setPreferredTopMargin((Element)parent, spaces);
			x = 0;
			y = 0;
        }
        else {
		    Insets insets = parent.getInsets();
		    contentWidth  -= insets.left + insets.right;
		    
		    x = insets.left;
		    y = insets.top;
        }
        
        x0 = x;
        y0 = y;

        for (Component c : parent.getComponents()) {
            if (c instanceof Element) {
                Element element = (Element) c;
                Style style = element.getStyle();
                BlockLayout layout = (BlockLayout)element.getLayout();
                
				Border margin = getBorder(style, Box.MARGIN);
				Border border = getBorder(style, Box.BORDER);
				Border padding = getBorder(style, Box.PADDING);
                
                String floating = getFloat(style);
                String position = getPosition(style);
                
                if("none".equals(style.getProperty("display"))) {
                	continue;
                }
                
            	int width = layoutWidth(contentWidth, element, style, margin, border, padding);
            	
            	int height = 0;
            	if (element.parentNode() instanceof Element)
            		height = layout.getOffsetHeight();
        		else
        			height = contentHeight;
                
                x = margin.left;
            	y = setPreferredChildHeight(element, spaces);
            	
        		c.setBounds(x, y, width, height);
            }
            else if (c.isVisible()) {
                Dimension d = c.getPreferredSize();

                c.setBounds(0, y, d.width, d.height);

				spaces.push(SpaceType.HEIGHT, d.height);
            }
        }
    }
    
    private int layoutWidth(int maxWidth, Element element, Style style, Border margin, Border border, Border padding){
    	int offsetWidth;
    	
    	if (isAuto(style, "width")) {
    		int width = 0;
    		width = maxWidth - margin.left - margin.right;
    		width = limit(style, "min-width", "max-width", width);
    		Math.max(element.getPreferredSize().width, width);
    		
    		offsetWidth = border.left
						+ padding.left
						+ width
						+ padding.right
						+ border.right;
    	}
    	else {
    		offsetWidth = border.left
						+ padding.left
						+ limit(style, "min-width", "max-width", getWidth(style))
						+ padding.right
						+ border.right;
			
			if (margin.left + offsetWidth + margin.right < maxWidth) {
				boolean autoL = "auto".equals(style.getProperty("margin-left"));
				boolean autoR = "auto".equals(style.getProperty("margin-right"));
				if (autoL && autoR) {
					int delta = maxWidth - offsetWidth;
					margin.left = delta / 2;
					margin.right = delta - delta / 2;
				}
			}
    	}
		return offsetWidth;
    }

    /**
     * set container size
     *
     * @param parent
     *
     */
    private void setSizes(Container parent) {
    	if (!sizeUnknown)
    		return;
    		
        //Reset preferred/minimum width and height.
        preferredWidth = 0;
        preferredHeight = 0;
        
    	setPerferredWidth(parent);
    	setPreferredHeight(parent);
        
        sizeUnknown = false;
    }
    
    /**
     * Set preferred height
     *
     * margin of elements in normal flow may collapse.
     */
	private void setPreferredHeight(Container parent){
		boolean normalFlow = parent instanceof Element;
		
	    SpaceList spaces = new SpaceList();
	    
	    // Top margin, border and padding
		if (normalFlow) {
			setPreferredTopMargin((Element)parent, spaces);
        }
        
		// Content box
	    for (Component c : parent.getComponents()) {
	        if (isInNormalFlow(c)) {
        		// Margin in normal flow may collapse
        		setPreferredChildHeight((Element)c, spaces);
	        }
	        else if (c.isVisible()) {
				spaces.push(SpaceType.HEIGHT, c.getPreferredSize().height);
	        }
        }
        
        // Bottom padding, border and margin
        if (normalFlow) {
        	Element element = (Element)parent;
			Style style = element.getStyle();
			BlockLayout layout = (BlockLayout)element.getLayout();
			
			Border margin = getBorder(style, Box.MARGIN);
			Border border = getBorder(style, Box.BORDER);
			Border padding = getBorder(style, Box.PADDING);
			
			// Margin of root element will not collapse
			if (!(element.parentNode() instanceof Element)) {
				spaces.push(SpaceType.HEIGHT, 0);
			}
			
			if (border.bottom != 0 || padding.bottom != 0) {
				spaces.push(SpaceType.HEIGHT, border.bottom + padding.bottom);
			}
			else if (!isAuto(style, "height") || getPxNumber(style.getProperty("min-height")) != 0) {
				spaces.push(SpaceType.HEIGHT, 0);
			}
			
			if (margin.bottom != 0)
				spaces.push(SpaceType.MARGIN, margin.bottom);
        }
        
        if (normalFlow) {
			int topMargin = spaces.getTopMargin();
			int height = spaces.getOffsetHeight();
			int bottomMargin = spaces.getBottomMargin();
			
			topMargins = spaces.getTopMargins();
			bottomMargins = spaces.getBottomMargins();
			
			Style style = ((Element)parent).getStyle();
			height = Math.max(getHeight(style), height);
			height = limit(style, "min-height", "max-height", height);
			
			offsetHeight = height;
			preferredHeight = topMargin + offsetHeight + bottomMargin;
        }
        else {
			offsetHeight = spaces.totalWidth();
			topMargins = new int[0];
			bottomMargins = new int[0];
			
			Insets insets = parent.getInsets();
        }
    }
    
    private void setPreferredTopMargin(Element element, SpaceList spaces){
		Style style = element.getStyle();
		BlockLayout layout = (BlockLayout)element.getLayout();
		
		Border margin = getBorder(style, Box.MARGIN);
		Border border = getBorder(style, Box.BORDER);
		Border padding = getBorder(style, Box.PADDING);
		
		if (margin.top != 0)
			spaces.push(SpaceType.MARGIN, margin.top);
		
		if (border.top != 0 || padding.top != 0) {
			spaces.push(SpaceType.HEIGHT, border.top + padding.top);
		}
		
		// Margin of root element will not collapse
		if (!(element.parentNode() instanceof Element)) {
			spaces.push(SpaceType.HEIGHT, 0);
		}
	}
    
    /**
     * Return height of child
     *
     * margin of elements in normal flow may collapse.
     *
     * @return height of child
     */
    private int setPreferredChildHeight(Element element, SpaceList spaces){
    	Style style = element.getStyle();
    	BlockLayout layout = (BlockLayout)element.getLayout();
		
        if("none".equals(style.getProperty("display"))) {
        	return spaces.getWidth();
        }
		
    	// Stop collapsing if child has clearance
    	if (hasClearance(style)) {
    		spaces.push(SpaceType.CLEAR, 0);
		}
		
		if (layout.getTopMargins().length > 0)
			spaces.push(SpaceType.MARGIN, layout.getTopMargins());
		
		int y = spaces.getWidth();
		
		int height = layout.getOffsetHeight();
		if (height > 0)
			spaces.push(SpaceType.HEIGHT, layout.getOffsetHeight());
			
		if (layout.getBottomMargins().length > 0)
			spaces.push(SpaceType.MARGIN, layout.getBottomMargins());
			
		return y;
    }
    
    /**
     * Calculate total height
     *
     * margin of elements in normal flow may collapse.
     */
    private void evalSpaces(Container parent, SpaceList spaces){
    	// top margin
    }
    
    /**
     * Calculate total width
     */
    private void setPerferredWidth(Container parent){
    	int width = 0;
    	for (Component c : parent.getComponents()) {
    		width = Math.max(width, c.getPreferredSize().width);
    	}
    	
        if (parent instanceof Element) {
        	Element element = (Element)parent;
        	Style style = element.getStyle();
        	
        	if (!isAuto(style, "width")) {
				width = Math.max(width, getWidth(style));
			}
			width = limit(style, "min-width", "min-height", width);
			
			preferredWidth = getWidth(style, Edge.LEFT) + width + getWidth(style, Edge.RIGHT);
        }
        else {
			Insets insets = parent.getInsets();
			preferredWidth = insets.left + width + insets.right;
        }
    }

    /**
     * Required by LayoutManager. 
     *
     * @param parent
     *
     * @return   
     */
    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);

		if (sizeUnknown) {
        	setSizes(parent);
        }
		
		if (parent instanceof Element) {
			dim = normalFlowSize((Element)parent);
		}
		else {
		    //Always add the container's insets!
		    Insets insets = parent.getInsets();
		    dim.width = preferredWidth
		        + insets.left + insets.right;
		    dim.height = preferredHeight
		        + insets.top + insets.bottom;
		}

        return dim;

    }

    /**
     * Required by LayoutManager. 
     *
     * @param parent
     *
     * @return   
     */
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }
    
    public Dimension normalFlowSize(Element parent) {
        Dimension dim = new Dimension(0, 0);
        
    	Style style = parent.getStyle();
			
		dim.width = preferredWidth
				  + getWidth(style, Box.MARGIN, Edge.LEFT)
				  + getWidth(style, Box.MARGIN, Edge.RIGHT)
				  + getWidth(style, Box.BORDER, Edge.LEFT)
				  + getWidth(style, Box.BORDER, Edge.RIGHT)
				  + getWidth(style, Box.PADDING, Edge.LEFT)
				  + getWidth(style, Box.PADDING, Edge.RIGHT);
				  
		dim.height = preferredHeight
				  + getWidth(style, Box.MARGIN, Edge.TOP)
				  + getWidth(style, Box.MARGIN, Edge.BOTTOM)
				  + getWidth(style, Box.BORDER, Edge.TOP)
				  + getWidth(style, Box.BORDER, Edge.BOTTOM)
				  + getWidth(style, Box.PADDING, Edge.TOP)
				  + getWidth(style, Box.PADDING, Edge.BOTTOM);
				   
	   return dim;
    }
    
    private int[] getTopMargins(){
    	return topMargins;
    }
    
    private int[] getBottomMargins(){
    	return bottomMargins;
    }
    
    private int getOffsetHeight(){
    	return offsetHeight;
    }

    /**
     * Function will get Width property
     *
     * @param style     mean Element style
     *
     * @return width    mean that width value  
     */
    private int getWidth(Style style) {
        int width = 70;
        int value = getPxNumber(style.getProperty("width"));
        if(value != 0) {
            return value;
        }
        return width;
    }

    /**
     * Function will get float property
     *
     * @param style     mean Element style
     *
     * @return floating mean that floating type
     */
    private String getFloat(Style style) {
        String floating = "none";
        if(style.getProperty("float") != null) {
            return style.getProperty("float");
        }
        return floating;
    }

    /**
     * Function will get height property 
     *
     * @param style     mean Element style
     *
     * @return height   mean that height value
     */
    private int getHeight(Style style) {
        int height = 50;
        int value = getPxNumber(style.getProperty("height"));
        if(value != 0) {
            return value;
        }
        return height;
    }

    /**
     * Function will get top property
     *
     * @param style      mean Element style
     *
     * @return top       mean that top value  
     */
    private int getTop(Style style) {
        int top = 0;
        int value = getPxNumber(style.getProperty("top"));
        if(value != 0) {
            return value;
        }
        return top;
    }

    /**
     * function will get left property
     *
     * @param style      mean element style
     *
     * @return left      mean that left value  
     */
    private int getLeft(Style style) {
        int left = 0;
        int value = getPxNumber(style.getProperty("left"));
        if(value != 0) {
            return value;
        }
        return left;
    }
    
    /**
     * covert number px into number
     *
     * @param numString
     *
     * @return value mean that number
     */
    private int getPxNumber(String numString) {
        if( numString != null) {
            if( numString.matches("[0-9]+") ) {
                return Integer.parseInt(numString);
            }
            else if(numString.matches("[0-9]+px")) {
                numString = numString.replaceAll("([0-9]+)px","$1");          
                return Integer.parseInt(numString);
            }
            else if(numString.matches("[0-9]+em")) {
                numString = numString.replaceAll("([0-9]+)em","$1");          
                return Integer.parseInt(numString);
            }
        }
        return 0;
    }

    public enum Box{
    	MARGIN,
    	BORDER,
    	PADDING
    }
    
    public enum Edge{
    	TOP,
    	RIGHT,
    	BOTTOM,
    	LEFT
    }
    
    public class Border{
    	public int top;
    	public int right;
    	public int bottom;
    	public int left;
    }
    
    private Border getBorder(Style style, Box box){
    	Border border = new Border();
    	border.top = getWidth(style, box, Edge.TOP);
    	border.right = getWidth(style, box, Edge.RIGHT);
    	border.bottom = getWidth(style, box, Edge.BOTTOM);
    	border.left = getWidth(style, box, Edge.LEFT);
    	
    	return border;
    }

    /**
     * Return parsed property of edge of box.
     *
     * @param style Element style
     * @param box Target box.
     * @param edge Target edge.
     *
     * @return parsed property of edge of box
     */
    private int getWidth(Style style, Box box, Edge edge) {
    	String boxString = box.toString().toLowerCase();
    	String edgeString = edge.toString().toLowerCase();
        return getPxNumber(style.getProperty(boxString + "-" + edgeString));
    }
    
    /**
     * Return width of edge.
     *
     * @param style Element style
     * @param edge Target edge.
     *
     * @return parsed property of edge
     */
    private int getWidth(Style style, Edge edge){
    	return	  getWidth(style, Box.MARGIN, edge)
				+ getWidth(style, Box.BORDER, edge)
				+ getWidth(style, Box.PADDING, edge);
	}
	
	
	private boolean isAuto(Style style, String property){
		String value = style.getProperty(property);
		return value == null || "auto".equals(value.trim());
	}
	
	private boolean hasClearance(Style style){
		String value = style.getProperty("clear");
		return value != null && !"none".equals(value.trim());
	}
	
	private int limit(Style style, String minProp, String maxProp, int value){
		if (style.getProperty(maxProp) != null)
    		value = Math.min(getPxNumber(style.getProperty(maxProp)), value);
    		
		if (style.getProperty(minProp) != null)
    		value = Math.max(getPxNumber(style.getProperty(minProp)), value);
    		
		return value;
	}

    /**
     * function will get position property
     *
     * @param style      mean element style
     *
     * @return position mean that absolute or relative  
     */
    private String getPosition(Style style) {
        String position= "relative";
        if(style.getProperty("position") != null) {
            return style.getProperty("position");
        }
        return position;
    }
    
    private boolean isInNormalFlow(Component comp){
    	return comp instanceof Element && ((Container)comp).getLayout() instanceof BlockLayout;
    }
    
    private enum SpaceType{
    	MARGIN,
    	HEIGHT,
    	CLEAR
    }
    
    private class SpaceList{
    	private ArrayList<Integer> margins = new ArrayList<Integer>();
    	
    	private boolean isTopMarginSet = false;
    	private int topMargin = 0;
    	private int[] topMargins = new int[0];
    	
    	private int width = 0;
    	
    	private int maxMargin = 0;
    	private int minMargin = 0;
    	
    	/**
    	 *
    	 * @note Ignore that bottom margin of container not collapse with margins overlap with clearance.
    	 */
    	public void push(SpaceType type, int width){
    		if (type == SpaceType.MARGIN) {
    			if (width > maxMargin)
    				maxMargin = width;
				else if (width < minMargin)
					minMargin = width;
				margins.add(width);
    		}
    		else {
    			if (!isTopMarginSet) {
    				topMargin = maxMargin + minMargin;
    				topMargins = new int[margins.size()];
    				int i = 0;
    				for (int margin : margins) {
    					topMargins[i++] = margin;
    				}
    				isTopMarginSet = true;
    			}
    			else {
    				this.width += maxMargin + minMargin;
    			}
    			maxMargin = 0;
    			minMargin = 0;
    			margins = new ArrayList<Integer>();
    			
    			this.width += width;
    		}
    	}
    	
    	public void push(SpaceType type, int[] widths){
    		for (int width : widths) {
    			push(type, width);
    		}
    		
    	}
    	
    	public int getTopMargin(){
    		return topMargin;
    	}
    	
    	public int getOffsetHeight(){
    		return width;
    	}
    	
    	public int getBottomMargin(){
    		if (isTopMarginSet)
    			return maxMargin + minMargin;
			else
				return 0;
    	}
    	
    	public int[] getTopMargins(){
    		return topMargins;
    	}
    	
    	public int[] getBottomMargins(){
    		if (isTopMarginSet) {
				int[] bottomMargins = new int[margins.size()];
				int i = 0;
				for (int margin : margins) {
					bottomMargins[i++] = margin;
				}
				return bottomMargins;
			}
			else {
				return new int[0];
			}
    	}
    	
    	public int getWidth(){
    		return getOffsetHeight() + getBottomMargin();
    	}
    	
    	public int totalWidth(){
    		return getTopMargin() + getOffsetHeight() + getBottomMargin();
    	}
    }
}
