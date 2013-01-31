package net.ion.crawler.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import junit.framework.TestCase;
import net.ion.crawler.parser.httpclient.DetectEncodingInputStream;
import net.ion.framework.util.Debug;

import org.apache.commons.io.IOUtils;

public class TestGuessInputStream extends TestCase{

	String path = "C:/temp/ion_page/index.html" ;
	public void testGuess() throws Exception {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)))) ;
		Debug.debug(IOUtils.toString(reader)) ;
		
	}
	
	public void testRightGuess() throws Exception {
		FileInputStream fis = new FileInputStream(new File(path));
		DetectEncodingInputStream si = new DetectEncodingInputStream(fis) ;
		Charset cs = si.getEncoding() ;
		
		Debug.debug(cs.name()) ;
		
		
		Debug.debug(IOUtils.toString(fis)) ;
	}
}
