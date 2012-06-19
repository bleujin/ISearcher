package net.ion.isearcher.crawler.auth;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.isearcher.http.IHttpClient;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.params.AuthPolicy;
public class AlternateAuthRealm implements IAuth {

	private AuthScope authScope;
	private Credentials credentials;

	public AlternateAuthRealm(AuthScope authScope, Credentials credentials) {
		this.authScope = authScope;
		this.credentials = credentials;
	}

	public void authProcess(IHttpClient client) {
		if (client.getCredentials(getAuthScope()) == null) {
			client.setCredentials(getAuthScope(), getCredentials());
			// Suppose the site supports several authetication schemes:
			// NTLM and Basic authetication is considered inherently insecure.
			// Hence, NTLM authentication is used per default

			// This is to make HttpClient pick the Basic authentication scheme over NTLM & Digest
			List authPrefs = ListUtil.toList(AuthPolicy.BASIC, AuthPolicy.NTLM, AuthPolicy.DIGEST);
			client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authPrefs);
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
