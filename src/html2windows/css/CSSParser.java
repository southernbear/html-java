package html2windows.css;

import java.util.regex.Pattern;

import html2windows.dom.Document;

public class CSSParser {
    String cssString;
    int pos;

    public void parse(String cssString, Document document) {
        this.cssString = cssString;
        this.pos = 0;
        parseStyleSheet();
    }

    // done
    private void parseSpace() {
        char ch = 0;

        if (isNotEnd()) {
            ch = getChar();
            while (ch == '\t' || ch == '\r' || ch == '\n' || ch == '\f'
                    || ch == ' ') {
                pos++;
                if (isNotEnd()) {
                    ch = getChar();
                }
                else
                    break;
            }
        }
    }

    private boolean isNotEnd() {
        return (pos < cssString.length());
    }

    // done
    private char getChar() {
        // css syntax is case-insensitive
        return Character.toLowerCase(cssString.charAt(pos));
    }

    // done
    private void parseStyleSheet() {
        while (isNotEnd()) {
            parseSpace();
            parseStatement();
        }
    }

    // done
    private void parseStatement() {
        if (isNotEnd()) {
            if (getChar() == '@') {
                pos++;
                parseAtRule();
            }
            else
                parseRuleSet();
        }
    }

    // done
    private void parseAtRule() {
        if (isNotEnd()) {
            parseIdent();
            parseSpace();
            parseAny();
            if (isNotEnd()) {
                if (getChar() == '{') {
                    pos++;
                    parseBlock();
                }
                else if (getChar() == ';') {
                    pos++;
                    parseSpace();
                }
            }
        }
    }

    private void parseBlock() {
        if (isNotEnd()) {
            parseSpace();
            while (isNotEnd() && getChar() != '}') {
                if (getChar() == '{') {
                    pos++;
                    parseBlock();
                }
                else if (getChar() == '@') {
                    pos++;
                    parseIdent();
                }
                else if (getChar() == ';') {
                    pos++;
                    parseSpace();
                }
                else {
                    parseAny();
                }
            }
            if (isNotEnd() && getChar() == '}') {
                pos++;
                parseSpace();
            }
        }
    }

    /**
     * 
     */
    private void parseRuleSet() {
        if (isNotEnd()) {
            if (Pattern.compile("(.*) {").matcher(cssString.substring(pos))
                    .find()) {
                // parseSelector?;
                // FIXIT
                parseSelector();
                if (isNotEnd() && getChar() == '{') {
                    pos++;
                    parseSpace();
                    if (isNotEnd()) {
                        // declaration?;
                        // FIXIT
                        parseDeclaration();
                        while (isNotEnd() && getChar() == ';') {
                            pos++;
                            parseSpace();
                            // declaration?;
                            // FIXIT
                            parseDeclaration();
                        }
                        if (isNotEnd() && getChar() == '}') {
                            pos++;
                            parseSpace();
                        }
                    }
                }
            }
        }
    }

    // done
    private void parseSelector() {
        while (isNotEnd()) {
            parseAny();
        }
    }

    // done
    private void parseDeclaration() {
        if (isNotEnd()) {
            parseProperty();
            parseSpace();
            if (isNotEnd()) {
                if (getChar() == ':') {
                    pos++;
                    parseSpace();
                    parseValue();
                }
            }
        }
    }

    private void parseProperty() {
        if (isNotEnd()) {
            parseIdent();
        }
    }

    // done
    private void parseValue() {
        if (isNotEnd()) {
            do {
                if (getChar() == '{') {
                    pos++;
                    parseBlock();
                }
                else if (getChar() == '@') {
                    pos++;
                    parseIdent();
                    parseSpace();
                }
                else {
                    parseAny();
                }
            }
            while (isNotEnd());
        }
    }

    private void parseAny() {
        if (isNotEnd()) {
            parseIdent();

        }
    }

    // done
    private void parseUnused() {
        if (getChar() == '{') {
            pos++;
            parseBlock();
        }
        else if (getChar() == '@') {
            pos++;
            parseIdent();
        }
        else if (getChar() == ';') {
            pos++;
            parseSpace();
        }
        else if (getChar() == '<') {
            pos++;
            parseCDO();
            parseSpace();
        }
        else if (getChar() == '-') {
            pos++;
            parseCDC();
            parseSpace();
        }
    }

    // done
    private void parseCDO() {
        if (isNotEnd()) {
            if (isNotEnd() && getChar() == '!') {
                pos++;
                if (isNotEnd() && getChar() == '-') {
                    pos++;
                    if (isNotEnd() && getChar() == '-') {
                        pos++;
                    }
                }
            }
        }
    }

    // done
    private void parseCDC() {
        if (isNotEnd()) {
            if (isNotEnd() && getChar() == '-') {
                pos++;
            }
        }
    }

    private String parseIdent() {
        char ch = 0;
        String buffer = "";

        if (isNotEnd()) {
            ch = getChar();
            if (ch == '-') {
                buffer += ch;
                pos++;
                if (isNotEnd())
                    ch = getChar();
            }
            else {
                System.out.println("ident no '-'");
                buffer += ch;

            }
            if (isNmstart(ch)) {
                buffer += parseNmstart();
            }
            while (isNmchar(ch)) {
                buffer += ch;
                pos++;
                if (isNotEnd())
                    ch = getChar();
            }
        }

        return buffer;
    }

    private boolean isNmstart(char ch) {
        // [_a-z] || nonascii
        if (ch == 95 || (ch >= 97 && ch <= 122) || isNonascii(ch)) {
            return true;
        }
        // escape
        else if (isEscape(ch)) {
            return true;
        }

        return false;
    }

    private boolean isEscape(char ch) {
        if (ch == 92) {
            pos++;
            ch = getChar();
            // unicode in escape
            if (isHex(ch)) {
                try {
                    int hexCh = parseUnicode();
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ch = getChar();
                if (ch == '\r') {
                    pos++;
                    ch = getChar();
                    if (ch == '\n') {
                        return true;
                    }
                }
                // space
                else if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t'
                        || ch == '\f') {
                    return true;
                }
            }
            // else in escape
            if (!(ch == '\n' || ch == '\r' || ch == '\f' || isHex(ch))) {
                return true;
            }
        }

        return false;
    }

    private boolean isNmchar(char ch) {
        boolean isTrue;

        isTrue = false;
        while (ch == 95 || (ch >= 97 && ch <= 122) || (ch >= 48 && ch <= 57)
                || isNonascii(ch) || isEscape(ch)) {
            pos++;
            ch = getChar();
            isTrue = true;
        }

        return isTrue;
    }

    private boolean isHex(char ch) {
        return ((ch >= 48 && ch <= 57) || (ch >= 97 && ch <= 102));
    }
    
    private void parseName(){
        if(isNotEnd()){
            do {
                parseNmchar();
            }
            while(isNotEnd());
        }
    }

    private String parseNmstart() {
        char ch;
        String buffer = "";

        if (isNotEnd()) {
            ch = getChar();
            // [_a-z] || nonascii
            if (ch == 95 || (ch >= 97 && ch <= 122) || isNonascii(ch)) {
                buffer += ch;
            }
            // escape
            else if (ch == 92) {
                pos++;
                parseEscape();
            }
        }

        return buffer;
    }
    
    private void parseNonascii(){
        if(isNotEnd()){
            
        }
    }
    
    private boolean isNonascii(char ch) {
        if(ch < 0 || ch > 159){
            pos++;
        }
        
        return false;
    }
    
    private int parseUnicode() throws Exception {
        char ch;
        String buffer = "";

        if (isNotEnd()) {
            ch = getChar();
            if (isHex(ch)) {
                int times = 0;
                do {
                    ch = getChar();
                    if (!isHex(ch))
                        break;
                    buffer += ch;
                    pos++;
                    times++;
                }
                while (times <= 6 && isNotEnd());

                return Integer.parseInt(buffer, 16);
            }
        }
        throw new Exception();
    }
    
    private String parseEscape() {
        char ch = 0;
        String buffer = "";

        if (ch == 92) {
            pos++;
            ch = getChar();
            // unicode in escape
            if (isHex(ch)) {
                try {
                    int hexInt = parseUnicode();
                    char hexCh = (char) hexInt;
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ch = getChar();
                if (ch == '\r') {
                    pos++;
                    ch = getChar();
                    if (ch == '\n') {

                    }
                }
                // space
                else if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t'
                        || ch == '\f') {

                }
            }
            // else in escape
            if (!(ch == '\n' || ch == '\r' || ch == '\f' || isHex(ch))) {

            }
        }

        return buffer;
    }
    
    private void parseNmchar(){
        char ch;
        
        if(isNotEnd()){
            ch = getChar();
        }
    }
    
    private void parseNum(){
        
    }
    
    private void parseString(){
        char ch;
        
        if(isNotEnd()){
            ch = getChar();
            if(ch == '"'){
                pos++;
                parseString1();
            }
            else if(ch == '\''){
                pos++;
                parseString2();
            }
        }
    }
    
    private void parseString1(){
        char ch;
        
        if(isNotEnd()){
            ch = getChar();
            if((!(ch == '\n' || ch == '\r' || ch == '\f' || ch == '\"'))){
                
            }
        }
    }
    
    private void parseString2(){
        
    }
    
    //\n|\r\n|\r|\f
    private void parseNl(){
        char ch;
        
        if(isNotEnd()){
            ch = getChar();
            if(ch == '\n' || ch == '\f'){
                System.out.println("in parseNl isEnd(end of \n || \f)");
            }
            else if(ch == '\r'){
                pos++;
                if(isNotEnd()){
                   ch = getChar();
                   if(ch == '\n'){
                       System.out.println("in parseNl isEnd(end of \r\n)");
                   }
                }
                else{
                   System.out.println("in parseNl isEnd(end of \r)");
                }
            }
        }
    }
    
    //[ \t\r\n\f]*
    private void parseW(){
        char ch;
        
        while(isNotEnd()){
            ch = getChar();
            if(ch == 32 || ch == '\t' || ch == '\r' || ch == '\n' || ch == '\f'){
                pos++;
            }
        }
    }
}