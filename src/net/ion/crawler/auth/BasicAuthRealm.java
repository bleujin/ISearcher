package net.ion.crawler.auth;

import net.ion.crawler.http.IHttpClient;
import net.ion.framework.util.Debug;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

public class BasicAuthRealm implements IAuth {

	private AuthScope authScope;
	private Credentials credentials;

	public BasicAuthRealm(String hostNm, int port, String realm, String userId, String password) {
		this(new AuthScope(hostNm, port, realm), new UsernamePasswordCredentials(userId, password));
	}

	public BasicAuthRealm(AuthScope authScope, Credentials credentials) {
		this.authScope = authScope;
		this.credentials = credentials;
	}

	public void authProcess(IHttpClient client) {
		if (client.getCredentials(getAuthScope()) == null) {
			// HostConfiguration hostConfiguration = new HostConfiguration();
			// hostConfiguration.setHost("im.i-on.net");
			// AuthPolicy.registerAuthScheme("I-ON MEMBERS", BasicScheme.class);
			// List<String> list = new ArrayList<String>();
			// list.add(AuthPolicy.BASIC);
			//	        
			// client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, list);

			client.setCredentials(getAuthScope(), getCredentials());
			Debug.debug("setCredential");
		}
		// getMethod.setDoAuthentication(true);
	}

	private AuthScope getAuthScope() {
		return this.authScope;
	}

	private Credentials getCredentials() {
		return this.credentials;
	}
}
