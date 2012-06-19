package net.ion.isearcher.crawler.link;

import java.io.Serializable;

public class Link implements Comparable, Serializable {

	private static final long serialVersionUID = 5102110656117114677L;
	
	private String referer ;
    private String uri ; /** the URI of the link. */
    private String anchor ;
    private String tagName ;
    /** the modification timestamp of the link. */
    private long timestamp = -1L;
	private int depth = -1;

    private static final String EMPTY_ANCHOR = "";
    
	public final static Link Top = new Link(null, "*", "", ""){
		public int getDepth(){
			return -1 ;
		}
	} ;
	
    private Link(String referer, String uri, String anchor, String tagName){
        if (uri == null) {
            throw new IllegalArgumentException("Parameter uri is null.");
        }
        this.referer = referer ;
        this.uri = uri;
        this.anchor = anchor ;
        this.tagName = tagName ;
    }
    
    /**
     * Refere가 Link형이 아닌 String 형인 이유는 GC를 할수 있게 하기 위해서.. 근데 별로 의미는 없군 =ㅅ=
     * @param referer
     * @param uri
     * @param _anchor
     * @param tagName
     * @return
     */
    
    public final static Link create(String referer, String uri, CharSequence _anchor, String tagName){
    	String anchor = (_anchor != null) ? _anchor.toString().trim() : "" ;
    	return new Link(referer, uri, anchor, tagName) ;
    } 

    public final static Link test(String referer, String uri){
    	return new Link(referer, uri, EMPTY_ANCHOR, "test") ;
    } 

    
	public static Link createStart(String startPage) {
		return new Link(Top.getURI(), startPage, EMPTY_ANCHOR, "startpage");
	}


    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }


    public long getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Link) {
            Link l = (Link) obj;
            // TODO maybe check timestamp also
            return uri.equals(l.getURI());
        }
        return false;
    }


    public int hashCode() {
        // TODO maybe use timestamp also
        return uri.hashCode();
    }


    public String toString() {
        return uri;
    }


    public int compareTo(Object obj) {
        Link l = (Link) obj;
        // TODO maybe check timestamp also
        return uri.compareTo(l.getURI());
    }


    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
    
	public String getAnchor() {
		return (anchor == null) ? EMPTY_ANCHOR : anchor;
	}
	
	public String toFullString(){
		return "referer:" + referer + ", url:" + uri + ", anchor:" + anchor ;
	}

	public String getReferer() {
		return referer;
	}

	public String getLinkTagName(){
		return tagName ;
	}
}
