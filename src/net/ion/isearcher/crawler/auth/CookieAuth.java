package net.ion.isearcher.crawler.auth;

import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.http.HTTPRequest;
import net.ion.isearcher.http.HTTPResponse;
import net.ion.isearcher.http.IHttpClient;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;

public class CookieAuth implements IAuth {

	private LoginSetting setting;

	public CookieAuth(LoginSetting setting) {
		this.setting = setting;
	}

	public void authProcess(IHttpClient client) throws IOException {

		client.setDefaultHost(getLoginSetting().getLoginHost(), getLoginSetting().getLoginPort(), getLoginSetting().getProtocol());
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

		// some site has cookie compliance problems
		// Their session cookie's domain attribute is in violation of the RFC2109
		// We have to resort to using compatibility cookie policy

		HTTPResponse get = client.createRequest(getLoginSetting().getLoginPath()).get();
		Debug.debug("Login form get: " + get.getStatusCode());
		get.consume() ;

		
		HTTPRequest request = client.createRequest(getLoginSetting().getLoginPath());
		for (NameValuePair pair : getLoginSetting().getFormNameValuePair()) {
			request.addParam(pair.getName(), pair.getValue()) ;
		}

		HTTPResponse post = request.post() ;
		Debug.debug("Login form post: " + post.getStatusCode());
		// Usually a successful form-based login results in a redicrect to
		// another url
		int statuscode = post.getStatusCode();
		if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) || (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) || (statuscode == HttpStatus.SC_SEE_OTHER) || (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
			Header header = post.getFirstHeader("location");
			if (header == null) return ;
			String newuri = StringUtil.defaultIfEmpty(header.getValue(), "/");
			HTTPResponse newResponse = client.createRequest(newuri).get() ;
			newResponse.consume() ;
		}
		post.consume() ;
	}

	private LoginSetting getLoginSetting() {
		return setting;
	}

}
