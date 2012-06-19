package net.ion.isearcher.crawler.util;

import org.apache.http.auth.AuthScope;

public final class AuthScopeUtil {

    private AuthScopeUtil() {
    }

    public static final String ANY_HOST = AuthScope.ANY_HOST;

    public static final int ANY_PORT = AuthScope.ANY_PORT;

    public static final String ANY_REALM = AuthScope.ANY_REALM;

    public static final String ANY_SCHEME = AuthScope.ANY_SCHEME;

    public static AuthScope createAuthScope(String host, int port) {
        return new AuthScope(host, port);
    }

    public static AuthScope createAuthScope(String host, int port, String realm, String scheme) {
        return new AuthScope(host, port, realm, scheme);
    }

}
