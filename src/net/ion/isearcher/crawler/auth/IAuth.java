package net.ion.isearcher.crawler.auth;

import java.io.IOException;

import net.ion.isearcher.http.IHttpClient;

public interface IAuth {

	public void authProcess(IHttpClient client) throws IOException ;
}
