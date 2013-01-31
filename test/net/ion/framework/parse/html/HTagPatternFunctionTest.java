package net.ion.framework.parse.html;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import junit.framework.TestCase;
import net.ion.crawler.http.HTTPResponse;
import net.ion.crawler.http.IHttpClient;
import net.ion.crawler.http.MyHttpClient;
import net.ion.crawler.parser.httpclient.DetectEncodingInputStream;
import net.ion.framework.logging.LogBroker;
import net.ion.framework.util.Debug;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.restlet.resource.ResourceException;

public class HTagPatternFunctionTest extends TestCase{

	public void testDetect() throws Exception {
		Spider spider = new Spider() ;
		Reader reader = new DetectEncodingInputStream(spider.getInputStream("http://bleujin.tistory.com/155"), 8192, Locale.getDefault()).getReader() ;
		Debug.debug(IOUtils.toString(reader)) ;
		
	}
	
	public void testStart() throws Exception {
		Spider spider = new Spider() ;
		Reader reader = new DetectEncodingInputStream(spider.getInputStream("http://bleujin.tistory.com/155"), 8192, Locale.getDefault()).getReader() ;
		HTag root = GeneralParser.parseHTML(reader) ;
		
		//Debug.debug(root.getContent().length(), root.getTrimText()) ;
		
		InfoCenter counter = new InfoCenter() ;
		printPattern(counter, root, 0) ;
		
		List<CountElement<String>> list = counter.getList() ;
		Collections.sort(list) ;
		for (CountElement<String> ele : list) {
			Debug.debug(ele) ;
		}
		
		Debug.debug(counter.getContentTag().getPath(), counter.getContentTag().getContent()) ;
	}

	private void printPattern(InfoCenter counter, HTag tag, int index) throws IOException {
		counter.add(tag) ;
		List<HTag> children = tag.getChildren() ;
		
		index++ ;
		for (HTag child : children) {
			if (skipTag(child)) continue ;
			printPattern(counter, child, index) ;
		}
	}

	private String[] skipTagName = new String[]{"frame", "dd", "dt", "center", "tt", "i", "pre", "b", "font", "p", "br", "!--", "strong", "script"} ;
	private boolean skipTag(HTag child) {
		return ArrayUtils.contains(skipTagName, child.getTagName());
	}
	
}

class InfoCenter {
	private String[] spaceTagName = new String[] {"td", "div", "span"} ; 
	private Map<String, CountElement<String>> pathCount = new HashMap<String, CountElement<String>>() ;
	private HTag contentTag = null ;
	
	// not thread safe 
	public void add(HTag tag) throws IOException{
		String tagPath = tag.getPathName() ;
		CountElement<String> value = pathCount.get(tagPath) ;
		if (value == null){
			value = new CountElement<String>(tagPath, 0, 0) ;
		} 
		pathCount.put(tagPath, new CountElement<String>(tagPath, value.getCount()+1, value.getLength() + tag.getOnlyText().length())) ;
		
		// what is the main content -t-?
		if (ArrayUtils.contains(spaceTagName, tag.getTagName())) {
			if (contentTag == null ){
				contentTag = tag ;
			}
			
			boolean isMainContent =  contentTag.getOnlyText().length() * 0.45 < tag.getOnlyText().length() ;
			if (tag.getPathName().startsWith(contentTag.getPathName()) && isMainContent){
				contentTag = tag ;
			} else if (contentTag.getOnlyText().length() / 0.45 < tag.getOnlyText().length()){
				contentTag = tag ;
			}
		}
	}

	public List<CountElement<String>> getList(){
		return new ArrayList<CountElement<String>>(pathCount.values()) ;
	}
	
	public HTag getContentTag(){
		return contentTag ;
	}
}



class CountElement<E> implements Comparable {

	private E obj ;
	private Integer count ;
	private Long length ;
	CountElement(E obj, int count, long length){
		this.obj = obj ;
		this.count = count ;
		this.length = length ;
	}

	public int hahsCode(){
		return obj.hashCode() ;
	}
	
	public boolean equals(Object o){
		return obj.equals(o) ;
	}
	
	public int getCount(){
		return count ;
	}
	
	public long getLength(){
		return length ;
	}
	
	
	public int compareTo(Object o) {
		return this.count.compareTo(((CountElement<E>)o).count);
	}
	
	public String toString(){
		return obj.toString() + ", count:" + count + ", avg length:" + getLength() / getCount() ;
	}
} 


class Spider {

	private Logger log = LogBroker.getLogger(Spider.class) ;
	
	public Reader getPageContent(String httpURL) throws IOException {
		String content = IOUtils.toString(getInputStream(httpURL), "EUC-KR") ;
		return new StringReader(content) ;
	}

	public InputStream getInputStream(String httpURL) throws IOException {
		IHttpClient httpclient = MyHttpClient.create() ;
		HTTPResponse response = null;
		try {
			response = httpclient.createRequest(httpURL).get() ;
			log.info("recevied data : " + httpURL) ;
			
			InputStream input = response.getStream() ;
			InputStream result = new ByteArrayInputStream(IOUtils.toByteArray(input)) ;
			input.close() ;
			return result ;
			
		} catch (IOException ex) {
			throw new ResourceException(response.getStatusCode()) ;
		} finally {
			response.consume() ;
			httpclient.shutdown() ;
		}
	}
}
