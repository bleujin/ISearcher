package net.ion.crawler.util;

import java.util.Iterator;
import java.util.Map;


public class UriFileSystemMapperUtil {

    private Map mapping;

    public UriFileSystemMapperUtil(final Map mapping) {
        this.mapping = mapping;
    }

    public String getDestination(final String uri) {
        if ((uri == null) || (mapping == null)) {
            return null;
        }

        final String find = uri.toLowerCase();
        
        
        Iterator itr = mapping.keySet().iterator();
        while (itr.hasNext()) {
            final String uriPath = (String) itr.next();

            if (find.startsWith(uriPath.toLowerCase())) {
                return (String) mapping.get(uriPath) + uri.substring(uriPath.length());
            }
        }
        return null;
    }

}
