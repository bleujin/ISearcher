package net.ion.crawler.auth;

import java.io.IOException;

import net.ion.crawler.http.IHttpClient;

public interface IAuth {

	public void authProcess(IHttpClient client) throws IOException ;
}
