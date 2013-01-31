package net.ion.crawler.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Locale;

import junit.framework.TestCase;
import net.ion.crawler.parser.httpclient.DetectEncodingInputStream;
import net.ion.framework.util.JavaScriptEscape;

import org.apache.commons.io.IOUtils;

public class SmartInputStreamTest extends TestCase{


	
	public void testDetectUTF() throws Exception {
		String s = "안녕하세요." ;
		InputStream input = new ByteArrayInputStream(s.getBytes("UTF-8")) ;
		assertEquals("UTF-8", new EncodeDetector().detectEncode(input, Locale.getDefault())) ;
	}

	public void testDetectKR() throws Exception {
		String sh = "안녕하세요." ;
		InputStream inputKr = new ByteArrayInputStream(sh.getBytes("EUC-KR")) ;
		assertEquals("EUC-KR", new EncodeDetector().detectEncode(inputKr, Locale.getDefault())) ;
	}
	

	public void testDetectInputStream1() throws Exception {
		String s = "하요" ;
		InputStream input = new ByteArrayInputStream(s.getBytes("EUC-kr")) ;
		
		DetectEncodingInputStream detectInput = new DetectEncodingInputStream(input, 1024, Locale.getDefault());
		assertEquals(s, IOUtils.toString(detectInput, detectInput.getEncoding().displayName())) ;
	}
	
	public void testDetectInputStream2() throws Exception {
		String s = "하요." ;
		InputStream input = new ByteArrayInputStream(s.getBytes("UTF-8")) ;
		
		DetectEncodingInputStream detectInput = new DetectEncodingInputStream(input, 1024, Locale.getDefault());
		assertEquals(s, IOUtils.toString(detectInput, detectInput.getEncoding().displayName())) ;
	}

	public void testURLDecode() throws Exception {
		String url1 = "http://localhost:8080/ICSS5/_rest/bleujin/search.html?query=%ED%95%9C%EA%B8%80" ;
		String url2 = "http://localhost:8080/ICSS5/_rest/bleujin/search.html?query=%C7%D1%B1%DB" ;
		assertEquals("한글", JavaScriptEscape.decode("%ED%95%9C%EA%B8%80", Locale.getDefault())) ;
		assertEquals("한글", JavaScriptEscape.decode("%C7%D1%B1%DB", Locale.getDefault())) ;
		assertEquals("ABC", JavaScriptEscape.decode("ABC", Locale.getDefault())) ;

	}

	
}
