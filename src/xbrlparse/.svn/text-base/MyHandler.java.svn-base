package xbrlparse;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

class MyHandler implements ErrorHandler {
    public void warning(SAXParseException e) {
        System.out.println("警告: " + e.getLineNumber() +"行目");
        System.out.println(e.getMessage());
    }
    public void error(SAXParseException e) {
        System.out.println("エラー: " + e.getLineNumber() +"行目");
        System.out.println(e.getMessage());
    }
    public void fatalError(SAXParseException e) {
        System.out.println("深刻なエラー: " + e.getLineNumber() +"行目");
        System.out.println(e.getMessage());
    }
}