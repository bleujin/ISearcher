package net.ion.framework.parse.html;

import java.io.StringReader;
import java.util.Date;

import junit.framework.TestCase;
import net.htmlparser.jericho.Element;
import net.ion.framework.util.Debug;

import org.apache.http.impl.cookie.DateUtils;

public class HTMLTest extends TestCase{

	public void testInsensitive() throws Exception {
		
		String str = "<Hello Attr='Tag'>Hello</hello>" ;
		StringReader reader = new StringReader(str) ;
		
		HTag root = GeneralParser.parseHTML(reader) ;
		
		assertEquals("hello", root.getTagName()) ;
		assertEquals("Tag", root.getAttributeValue("attr")) ;
	}
	
	public void testEmpty() throws Exception {
		String str = "" ;
		StringReader reader = new StringReader(str) ;
		
		HTag root = GeneralParser.parseHTML(reader) ;
	}
	
	
	private String html = 	"<!DOCTYPE html>" +
		"<script atrr='a1'>Bla Bla</script>" + 
		"<html><head><title>TITLE</title></head><body></body></html>" ;
	public void testDocType() throws Exception {
		HTag root = GeneralParser.parseHTML(new StringReader(html)) ;
		assertEquals("TITLE", root.getChild("head/title").getOnlyText()) ;
		
	}
	
	public void testOnlyText() throws Exception {
		HTag root = GeneralParser.parseHTML(new StringReader(html)) ;
		assertEquals("TITLE", root.getOnlyText()) ;
		assertEquals("TITLE", root.getChild("head").getOnlyText()) ;
		assertEquals("TITLE", root.getChild("head/title").getOnlyText()) ;
	}
	
	public void testPrefix() throws Exception {
		HTag root = GeneralParser.parseHTML(new StringReader(html)) ;
		assertEquals(true, root.getPrefixTag().hasChild()) ;
	}
	
	public void testContent() throws Exception {
		HTag root = GeneralParser.parseHTML(new StringReader(html)) ;
		HTag script = root.getPrefixTag().getChildren().get(1) ;
		
		assertEquals("<script atrr='a1'>Bla Bla</script>", script.getContent()) ;
		assertEquals(false, script.hasChild()) ;
		assertEquals(0, script.getChildren().size()) ;
	}
	
	public void testTagText() throws Exception {
		HTag root = GeneralParser.parseHTML(new StringReader(html)) ;
		HTag script = root.getPrefixTag().getChildren().get(1) ;

		assertEquals("Bla Bla", script.getTagText()) ;
		assertEquals("<head><title>TITLE</title></head><body></body>", root.getTagText()) ;
	}
	
	public void testDepth() throws Exception {
		HTag root = GeneralParser.parseHTML(new StringReader(html)) ;
		assertEquals(0, root.getDepth()) ;
	}

	
	public void testChild() throws Exception {

		String s = "<root><p><center><img src=''/>gacdd</center><br><br><br></p><p></p></root>" ;
		HTag root = GeneralParser.parseHTML(new StringReader(s)) ;
		
		Debug.debug(DateUtils.formatDate(new Date(), "yyyyMMdd") + "/DEFAULT") ; 
		
		for (HTag child : root.getChildren()) {
			Element celement = child.getElement();
			Debug.debug(child, celement.getDebugInfo(), celement.getEndTag(), celement.getBegin(), celement.getEnd()) ;
		}
	}
	
}
