package net.ion.crawler.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import net.ion.framework.util.Debug;

import org.apache.commons.lang.StringUtils;

public class Robot {

	// Data members
	private String robotContent =""; // content of the robots.txt
	private URL thisUrl; // the site url
	private String[] lookUpTable; // an array of 'disallow's
	private int sizeOfTable; // size of the array
	private String hostUrl = "";

	/**
	 * Constructor: initialize the URL object with the incoming URL.
	 * <p>
	 * @throws IOException 
	 * 
	 */
	public Robot(URL url) throws IOException {
		if (! url.getPath().endsWith("robots.txt")) {
			String checkUrl = url.getProtocol() + "://" + url.getHost() + ":" +url.getPort() + "/robots.txt";
			thisUrl = new URL(checkUrl) ;
		} else {
			thisUrl = url;
			
		}
		hostUrl = url.getProtocol() + "://" + url.getHost() + ":" +url.getPort();
		sizeOfTable = 0;
		connectAndRead();
		build();
	}
	
	public Robot(StringBuffer buffer){
		this.robotContent = buffer.toString() ;
		build() ;
	}

	/**
	 * Connect to the web and read its robots.txt, if any.
	 * <p>
	 * @throws IOException 
	 */
	private void connectAndRead() throws IOException {

		HttpURLConnection uC = null;
		int responseCode = -1;
		InputStream input;
		BufferedReader remote;

		// connet to the site
		uC = (HttpURLConnection) thisUrl.openConnection();
		responseCode = uC.getResponseCode();

		if (responseCode != HttpURLConnection.HTTP_OK) {
			Debug.debug("Server hasn't robots.txt");
		}

		input = uC.getInputStream();
		remote = new BufferedReader(new InputStreamReader(input));

		String line = new String();
		line = remote.readLine();
		while (line != null) {
			// System.out.println("in reading:" + line + ":");
			sizeOfTable++;
			robotContent += (line + "\n");
			line = remote.readLine();
		}
	}

	/**
	 * build the look-up table.
	 * <p>
	 * 
	 * @param inContent
	 *            the robots.txt in a string format.
	 */
	private void build() {

		StringTokenizer st = new StringTokenizer(robotContent);
		lookUpTable = new String[sizeOfTable];
		int tokenCount = 0;
		int cmdCount = 0;
		while (st.hasMoreTokens() == true) {
			String op = st.nextToken();
			// System.out.println(tokenCount + ":" + op);
			int separatorLoc = op.indexOf(":");
			if (separatorLoc > 0) { // this is a 'disallow' or 'allow'
				String value = st.nextToken();
				op = op.substring(0, separatorLoc);
				// System.out.println("Op|" + op + "| value|" + value + "|");
				/*
				 * Only the 'disallow' entries are recorded.
				 */
				if (op.compareToIgnoreCase("disallow") == 0) {
					/*
					 * System.out.println("adding " + op + " and " + value + "
					 * to the table " + "index " + cmdCount + " size " +
					 * sizeOfTable);
					 */
					lookUpTable[cmdCount] = new String(value.trim());
					cmdCount++;
				}
			}
			tokenCount++;
		} // while

		sizeOfTable = cmdCount;
		sortTable(); // sort the look-up table
	}

	/**
	 * Sort the table in order, using quicksort that's in Sort.java.
	 * <p>
	 */
	private void sortTable() {
		/**
		 * try { Sort.QuickSort(lookUpTable, 0, sizeOfTable - 1); } catch
		 * (Exception e) { System.err.println("sorting problem, quit!");
		 * System.exit(1); }
		 */
	}

	/**
	 * Check to see if the any of the paths inthe list is a part of the incoming
	 * 'path'.
	 * <p>
	 * 
	 * @param list
	 *            the table to check against
	 * @param path
	 *            the path to check
	 * 
	 * @return true if the 'path' is a part of the 'list', false otherwise.
	 *         <p>
	 */
	private boolean isPartOf(String path) {

		for (int i = 0; i < sizeOfTable; i++)
			if (path.startsWith(lookUpTable[i]))
				return true;

		return false;
	}

	/**
	 * Check to see if a given path is allowed to visit.
	 * <p>
	 * 
	 * @return true if this is allowed to visit, false otherwise.
	 */
	public boolean isAllowedToVisit(String path) {
		path = path.trim();
		path = StringUtils.replaceOnce(path, hostUrl, "");
		if (isPartOf(path))
			return false; // not allowed to visit
		else
			return true; // allowed to visit
	}
}
