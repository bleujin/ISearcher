package net.ion.crawler.auth;

import junit.framework.TestCase;

public class LoginURLInfoTest extends TestCase {

	public void testStart() throws Exception {
		URLInfo info = new URLInfo("http://a.b.c/abcdefg/xxx.cgi?abd=ddd");

		assertEquals(80, info.getPort());
		assertEquals("a.b.c", info.getHostNm());
		assertEquals("/abcdefg/xxx.cgi?abd=ddd", info.getPath());
		assertEquals("http", info.getProtocol());

		info = new URLInfo("https://a.b.c:8080/abcdefg/xxx");
		assertEquals(8080, info.getPort());
		assertEquals("a.b.c", info.getHostNm());
		assertEquals("/abcdefg/xxx", info.getPath());
		assertEquals("https", info.getProtocol());

	}
}
