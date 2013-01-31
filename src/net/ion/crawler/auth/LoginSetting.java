package net.ion.crawler.auth;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class LoginSetting {

	private String loginHost;
	private int port ;
	private String protocol ;
	private String loginActionUrl ;

	private List<NameValuePair> params ;
	public LoginSetting(String loginActionUrl) throws MalformedURLException{
		this(loginActionUrl, new NameValuePair[0]) ;
	}
	public LoginSetting(String loginActionUrl, NameValuePair[] nameValuePairs) throws MalformedURLException{
		URLInfo uinfo = new URLInfo(loginActionUrl) ;
		this.loginHost = uinfo.getHostNm() ;
		this.port = uinfo.getPort() ;
		this.protocol = uinfo.getProtocol() ;
		this.loginActionUrl = uinfo.toString() ;
		this.params = new ArrayList(ListUtil.toList(nameValuePairs)) ;
	}
	
	public LoginSetting addParameter(String key, String value){
		this.params.add(new BasicNameValuePair(key, value)) ;
		return this ;
	}
	
	public String getLoginPath(){
		return this.loginActionUrl ;
	}
	
	public String getProtocol(){
		return this.protocol ;
	}
	
	public String getLoginHost() {
		return this.loginHost;
	}

	public int getLoginPort() {
		return 0;
	}

	public NameValuePair[] getFormNameValuePair() {
		return params.toArray(new NameValuePair[0]);
	}

}

class URLInfo {

	private URL url ;
	public URLInfo(String url) throws MalformedURLException{
		this.url = new URL(url) ;
	}
	
	public String getHostNm() {
		return url.getHost() ;
	}
	public int getPort() {
		return url.getPort() < 0 ? url.getDefaultPort() : url.getPort() ;
	}
	
	public String getPath(){
		return url.getPath() + (StringUtil.isBlank(url.getQuery()) ? "" : "?" +url.getQuery());
	}

	public String getProtocol() {
		return url.getProtocol() ;
	}
	
	public String toString(){
		return url.toString() ;
	}
	
}