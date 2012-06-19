package net.ion.isearcher.crawler.util;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;

public final class CredentialsUtil {

    private CredentialsUtil() {
    }

    public static Credentials createUsernamePasswordCredentials(String userName, String password) {
        return new UsernamePasswordCredentials(userName, password);
    }

    public static Credentials createNTLMCredentials(String userName, String password, String host, String domain) {
        return new NTCredentials(userName, password, host, domain);
    }
}
