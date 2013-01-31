package net.ion.crawler.util;

import java.util.Date;

import net.ion.crawler.http.HTTPResponse;
import net.ion.crawler.http.URI;
import net.ion.crawler.http.URIException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;


public final class HttpClientUtil {

    private static final transient Log LOG = LogFactory.getLog(HttpClientUtil.class);

    /** standard if modified since header. */
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";

    /** standard last modified header. */
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";

    /**
     * Avoid instances of HttpClientUtil.
     */
    private HttpClientUtil() {
    }

    public static long getLastModified(HTTPResponse response) {
        Header header = response.getFirstHeader(HEADER_LAST_MODIFIED);
        if (header != null) {
            String value = header.getValue();
            try {
                Date date = DateUtils.parseDate(value);
                return date.getTime();
            } catch (DateParseException e) {
                LOG.info("Can't parse last modified header, value=" + value);
            }
        }
        return -1L;
    }

    public static boolean isRedirect(int statusCode) {
        switch (statusCode) {
            case HttpStatus.SC_MOVED_TEMPORARILY:
            case HttpStatus.SC_MOVED_PERMANENTLY:
            case HttpStatus.SC_SEE_OTHER:
            case HttpStatus.SC_TEMPORARY_REDIRECT:
                return true;
            default:
                return false;
        }
    }

    public static URI getRedirectURI(URI currentUri, final HTTPResponse response) {
        // get the location header to find out where to redirect to
        Header locationHeader = response.getFirstHeader("location");
        if (locationHeader == null) {
            // got a redirect response, but no location header
            LOG.warn("Received redirect response " + response.getStatusCode() + " but no location header");
            return null;
        }
        String location = locationHeader.getValue();
        
        // rfc2616 demands the location value be a complete URI
        // Location       = "Location" ":" absoluteURI
        URI redirectUri = null;

        try {
            redirectUri = new URI(location, true);
            
            if (redirectUri.isRelativeURI()) {
                //location is incomplete, use current values for defaults
                LOG.info("Redirect URI is not absolute - parsing as relative");
                redirectUri = new URI(currentUri, redirectUri);
            }
            
            if (LOG.isDebugEnabled()) {
                LOG.debug("Redirecting from '" + currentUri.getEscapedURI()
                    + "' to '" + redirectUri.getEscapedURI());
            }

            return redirectUri;

        } catch (URIException ex) {
            LOG.error("Can create redirect link for redirectUri=" + redirectUri);
        }

        return null;
    }

}
