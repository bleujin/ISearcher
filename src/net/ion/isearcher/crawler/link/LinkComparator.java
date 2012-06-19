package net.ion.isearcher.crawler.link;

import java.io.Serializable;
import java.util.Comparator;


public final class LinkComparator implements Comparator, Serializable {

    private static final long serialVersionUID = -6326885638036338613L;

    /**
     * {@inheritDoc}
     */
    public int compare(Object o1, Object o2) {
        Link l1 = (Link) o1;
        Link l2 = (Link) o2;

        return l1.getURI().compareTo(l2.getURI());
    }

}
