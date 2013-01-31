package net.ion.crawler.parser;

import net.ion.crawler.link.Link;

public abstract class PageData {

	public enum DataType {
		HTML, BINARY
	}
	
    /** Status not loaded yet of the data. */
    public static final int NOT_LOADED = 0;
    /** Status data loaded without any errors. */
    public static final int OK = 1;
    /** Status data couldn't loaded due to unexpected errors. */
    public static final int ERROR = 2;
    /** Status data wasn't modified. */
    public static final int NOT_MODIFIED = 3;
    /** Status data contains a redirect. */
    public static final int REDIRECT = 4;

    public static final int NOT_HANDLE = 5;
    
    /** The link of this data. */
    private Link link;

    /** The status of the data. */
    private int status;
    
    private DataType datatype = DataType.HTML;

    public PageData(Link link) {
        this(link, NOT_LOADED);
    }

    public PageData(Link link, int status) {
        this.link = link;
        this.status = status;
    }


    public Link getLink() {
        return link;
    }

    public int getStatus() {
        return status;
    }

    public void setDataType(DataType datatype){
    	this.datatype = datatype ;
    }
    
    
    public DataType getDataType(){
    	return this.datatype ;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }

    public abstract Object getData();
    
    public abstract void setData(Object data);

	public boolean isHTMLDataType() {
		return datatype == DataType.HTML;
	}

}
