package net.ion.crawler.link;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import net.ion.crawler.core.ICrawler;

public class LinkGraph {

	private Link link ;
	private Collection<Link> inLinks ;
	private Collection<Link> outLinks ;
	public LinkGraph(Link link, Collection<Link> inLinks, Collection<Link> outLinks) {
		this.link = link ;
		this.inLinks = inLinks ;
		this.outLinks = outLinks ;
	}

	public static LinkGraph create(Link link, Collection<Link> inLinks, Collection<Link> outLinks) {
		return new LinkGraph(link, inLinks, outLinks);
	}

    public Collection<Link> outLinks() {
        return this.outLinks;
    }

	public Collection<Link> inLinks() {
		return inLinks;
	}

	public void write(ICrawler crawler, Writer rw) throws IOException{
        rw.write(link.toString() + "[depth:" + link.getDepth() + ", in:" + inLinks.size()+ ", out:" + outLinks.size()+ "]\n");

        for (Link inLink : this.inLinks()) {
        	rw.write("-> in : " + inLink.toString() + "[tag:" + inLink.getLinkTagName() + ", depth:" + inLink.getDepth() + ", anchor:{" + inLink.getAnchor() + "}]\n");
        }

        for (Link outLink : this.outLinks()) {
        	rw.write("-> out : " + outLink.toString() + "[tag:" + outLink.getLinkTagName() + ", filter:" + crawler.getLinkFilter().accept(outLink) + ", depth:" + outLink.getDepth() + ", anchor:{" + outLink.getAnchor() + "}]\n");
        }
	}
}
