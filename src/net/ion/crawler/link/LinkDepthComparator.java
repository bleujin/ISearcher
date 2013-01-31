package net.ion.crawler.link;

import java.io.Serializable;
import java.util.Comparator;


public final class LinkDepthComparator implements Comparator, Serializable {

	private static final long serialVersionUID = 7052071018705115422L;

    public int compare(Object o1, Object o2) {
    	Link l1 = (Link) o1;
    	Link l2 = (Link) o2;

        if (l1.getDepth() != l2.getDepth()) {
            return l1.getDepth() - l2.getDepth();
        } else {
            return l1.getURI().compareTo(l2.getURI());
        }
    }

}
