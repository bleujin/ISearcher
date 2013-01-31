package net.ion.crawler.parser.httpclient;

import net.ion.crawler.link.Link;
import net.ion.crawler.parser.PageData;

/**
 * A special page data container for the SimpleHttpClientParser which contain
 * the character set also.
 *
 * @author bleujin
 * @version $Revision: 1.4 $
 */
public class PageDataHttpClient extends PageData {

    /** the data of the page. */
    private CharSequence data;

    /** last modified date of the page. */
    private long lastModified = -1L;

    /** the charSet of the data. */
    private String charSet;

    /** 
     * standard last modified header. 
     * @deprecated use HttpClientUtil.HEADER_LAST_MODIFIED, constant will be removed with version 2.0
     */
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";

    private PageDataHttpClient(final Link link, final int status) {
        super(link, status);
    }

    public final static PageData create(final Link link, int status, final CharSequence data, final String charSet) {
    	PageDataHttpClient result = new PageDataHttpClient(link, status);
    	result.data = data;
    	result.charSet = charSet;
    	result.lastModified = link.getTimestamp(); // setting lastModified for backward compatibility
		return result ;
    }
    

    
    public final static PageDataHttpClient create(final Link link, final int status) {
    	return new PageDataHttpClient(link, status) ;
    }
    
    /**
     * {@inheritDoc}
     * @see net.ion.isearcher.crawler.parser.PageData#getData()
     */
    public final Object getData() {
        return data;
    }

    /**
     * Sets the data which has to be a String object.
     * {@inheritDoc}
     * @see net.ion.isearcher.crawler.parser.PageData#setData(java.lang.Object)
     */
    public void setData(Object data) {
        this.data = (CharSequence) data;
    }

    public void setData(Object data, String charset) {
        this.data = (CharSequence) data;
        this.charSet = charset ;
    }


    /**
     * Returns the character encoding of data.
     *
     * @return String The character set.
     */
    public final String getCharSet() {
        return charSet;
    }

}
