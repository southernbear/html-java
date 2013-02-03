package html2windows.dom;

import html2windows.css.Origin;
import html2windows.css.CSSParser;
import html2windows.css.CSS2Painter;
import html2windows.css.BlockLayout;
import html2windows.css.DocumentAtRuleHandler;
import html2windows.css.handler.*;
import html2windows.css.parser.*;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.FileReader;

import java.lang.String;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.UnsupportedEncodingException;

/**
 * It will read file or String ,then building the document 
 *
 * @author bee040811
 */
public class UIParser {
	
	private static final String defaultStyleSheet = "html, address,blockquote,body, dd, div,dl, dt, fieldset, form,frame, frameset,h1, h2, h3, h4,h5, h6, noframes,ol, p, ul, center,dir, hr, menu, pre { display: block; unicode-bidi: embed }li { display: list-item }head { display: none }table { display: table }tr { display: table-row }thead { display: table-header-group }tbody { display: table-row-group }tfoot { display: table-footer-group }col { display: table-column }colgroup { display: table-column-group }td, th { display: table-cell }caption { display: table-caption }th { font-weight: bolder; text-align: center }caption { text-align: center }body { margin: 8px }h1 { font-size: 2em; margin: .67em 0 }h2 { font-size: 1.5em; margin: .75em 0 }h3 { font-size: 1.17em; margin: .83em 0 }h4, p,blockquote, ul,fieldset, form,ol, dl, dir,menu { margin: 1.12em 0 }h5 { font-size: .83em; margin: 1.5em 0 }h6 { font-size: .75em; margin: 1.67em 0 }h1, h2, h3, h4,h5, h6, b,strong { font-weight: bolder }blockquote { margin-left: 40px; margin-right: 40px }i, cite, em,var, address { font-style: italic }pre, tt, code,kbd, samp { font-family: monospace }pre { white-space: pre }button, textarea,input, select { display: inline-block }big { font-size: 1.17em }small, sub, sup { font-size: .83em }sub { vertical-align: sub }sup { vertical-align: super }table { border-spacing: 2px; }thead, tbody,tfoot { vertical-align: middle }td, th, tr { vertical-align: inherit }s, strike, del { text-decoration: line-through }hr { border: 1px inset }ol, ul, dir,menu, dd { margin-left: 40px }ol { list-style-type: decimal }ol ul, ul ol,ul ul, ol ol { margin-top: 0; margin-bottom: 0 }u, ins { text-decoration: underline }br:before { content: \"\\A\"; white-space: pre-line }center { text-align: center }:link, :visited { text-decoration: underline }:focus { outline: thin dotted invert }/* Begin bidirectionality settings (do not change) */BDO[DIR=\"ltr\"] { direction: ltr; unicode-bidi: bidi-override }BDO[DIR=\"rtl\"] { direction: rtl; unicode-bidi: bidi-override }*[DIR=\"ltr\"] { direction: ltr; unicode-bidi: embed }*[DIR=\"rtl\"] { direction: rtl; unicode-bidi: embed }@media print {h1 { page-break-before: always }h1, h2, h3,h4, h5, h6 { page-break-after: avoid }ul, ol, dl { page-break-before: avoid }}";

    /**
     * parsing the css struct will construct the Document by recursive
     *
     * @param element   mean element will save nodeType and nodeValue
     * @param document  mean the document
     *
     * @return element  
     */
	private Element parseElement(org.w3c.dom.Element element, Document document){
		
		Element outputElement = document.createElement(element.getTagName());

		org.w3c.dom.NamedNodeMap attributeMap = element.getAttributes();
		for(int i = 0 ; i < attributeMap.getLength() ; i++){
			org.w3c.dom.Node attribute  = attributeMap.item(i);	
			String name = attribute.getNodeName();
			String value = attribute.getNodeValue();

			Attr outputAttribute = document.createAttribute(name);
			outputAttribute.setValue(value);
			outputElement.setAttributeNode(outputAttribute);
		}
		
		org.w3c.dom.NodeList childNodes = element.getChildNodes();
		for(int i = 0 ; i < childNodes.getLength(); i++){
			org.w3c.dom.Node node = childNodes.item(i);
			short type = node.getNodeType();
			switch (type){
				case org.w3c.dom.Node.TEXT_NODE:{
					org.w3c.dom.Text text = (org.w3c.dom.Text) node;
					Text outputText = document.createTextNode(text.getData());
					
					outputElement.appendChild(outputText);
				}
					break;
				case org.w3c.dom.Node.ELEMENT_NODE:{
					org.w3c.dom.Element childElement = (org.w3c.dom.Element) node;
					Element outputChildElement = parseElement(childElement, document);

					outputElement.appendChild(outputChildElement);
				}
					break;
				default:
					break;
			}
		}

		return outputElement;
	}

    /**
     * Function will parse the file into document
     *
     * @param input     String 
     *
     * @return Document
     */
	public Document parse(String input) {
        input = input.replaceAll(">[ \t\n\r]*<", "><");
        InputStream inputStream = new ByteArrayInputStream( input.getBytes());
        Document outputDocument = parse(inputStream);
        
        return outputDocument;
	}

    /**
     * Function will read file and convert into string and parse into document
     *
     * @param input
     *
     * @return document  
     */
	public Document parse(File input) {
		try{
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line;
            String inputString = "";
            while( (line = reader.readLine() ) != null ) {
                inputString += line;    
            }
			Document outputDocument = parse(inputString);

			return outputDocument;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

    /**
     * others input will transform into InputStream type, then it will parse
     *
     * @param input
     *
     * @return document   
     */
	private Document parse(InputStream input){
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(input);
			
			Document outputDocument = createDocument();
		    
			Element outputElement = parseElement(doc.getDocumentElement(),outputDocument);
			outputDocument.appendChild(outputElement);
			
			CSSParser cssParser = outputDocument.getCSSParser();
			cssParser.parse(Origin.DEFAULT, defaultStyleSheet, outputDocument);

			return outputDocument;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;	
	}
	
	public Document createDocument(){
			CSSParser cssParser = new CSSParser();

			ColorParser colorParser = new ColorParser();
			cssParser.setPropertyParser("color", colorParser);
			cssParser.setPropertyParser("background-color", colorParser);
			
			BorderParser borderParser = new BorderParser();
			cssParser.setPropertyParser("border-top-width", borderParser);
			cssParser.setPropertyParser("border-right-width", borderParser);
			cssParser.setPropertyParser("border-bottom-width", borderParser);
			cssParser.setPropertyParser("border-left-width", borderParser);
			cssParser.setPropertyParser("border-width", borderParser);
			
			cssParser.setPropertyParser("border-top-color", borderParser);
			cssParser.setPropertyParser("border-right-color", borderParser);
			cssParser.setPropertyParser("border-bottom-color", borderParser);
			cssParser.setPropertyParser("border-left-color", borderParser);
			cssParser.setPropertyParser("border-color", borderParser);
			
			cssParser.setPropertyParser("border-top-style", borderParser);
			cssParser.setPropertyParser("border-right-style", borderParser);
			cssParser.setPropertyParser("border-bottom-style", borderParser);
			cssParser.setPropertyParser("border-left-style", borderParser);
			cssParser.setPropertyParser("border-style", borderParser);
			
			cssParser.setPropertyParser("border-top", borderParser);
			cssParser.setPropertyParser("border-right", borderParser);
			cssParser.setPropertyParser("border-bottom", borderParser);
			cssParser.setPropertyParser("border-left", borderParser);
			cssParser.setPropertyParser("border", borderParser);
			
			BorderHandler borderHandler = new BorderHandler();
			DisplayHandler displayHandler = new DisplayHandler();
    	
			Document document = new Document();
			document.setCSSParser(cssParser);
		    document.addCSSPropertyHandler(displayHandler);
		    document.addCSSPropertyHandler(borderHandler);
		    document.setAtRuleHandler("document", new DocumentAtRuleHandler());
			
		    document.setLayout(new BlockLayout());
		    document.setPainter(new CSS2Painter());
		    
		    return document;
	}
}
