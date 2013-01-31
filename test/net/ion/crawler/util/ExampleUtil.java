/*
 * Utility class for the examples to reduce complexity and increase readability of the examples.
 * 
 * $Id: ExampleUtil.java,v 1.1 2012/06/15 08:35:01 bleujin Exp $
 */
package net.ion.crawler.util;

/**
 * Utility class for the examples to reduce complexity and 
 * increase readability of the examples.
 * 
 * @author bleujin
 * @version $Id: ExampleUtil.java,v 1.1 2012/06/15 08:35:01 bleujin Exp $
 */
public class ExampleUtil {
	
	static {
    	final String osName = System.getProperty("os.name");
    	isMac = osName.startsWith("Mac OS X");
    	isWindows = osName.startsWith("Windows");		
	}

	private static boolean isMac;
	private static boolean isWindows;
	
    private static final String DOWNLOAD_PATH_MAC = "samplesite/webapps/simple/download/";
    private static final String DOWNLOAD_PATH_WINDOWS = "samplesite/webapps/simple/download/";

    public static final String getDownloadPath() {
    	if (isMac) {
    		return DOWNLOAD_PATH_MAC;
    	} else if (isWindows) {
    		return DOWNLOAD_PATH_WINDOWS;
    	}
    	return System.getProperty("java.io.tmpdir");
    }
    
    private static final String SOURCE_WWW_PATH_MAC = "/Users/Lars/Documents/workspace/crawler/src/www";
    private static final String SOURCE_WWW_PATH_WINDOWS = "C:/temp/ion_page";

    public static final String getWwwSourcePath() {
    	if (isMac) {
    		return SOURCE_WWW_PATH_MAC;
    	} else if (isWindows) {
    		return SOURCE_WWW_PATH_WINDOWS;
    	}
    	return null;
    }
    
}
