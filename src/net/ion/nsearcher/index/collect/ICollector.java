package net.ion.nsearcher.index.collect;

import java.util.Date;

import net.ion.nsearcher.index.handler.DocumentHandler;

import org.apache.http.impl.cookie.DateUtils;


public interface ICollector  {
	
	final static String DEFAULT_NAME  = DateUtils.formatDate(new Date(), "yyyyMMdd") + "/DEFAULT";

	public void collect() ;
	public void shutdown(String cause) ;
	public String getCollectName() ;
	public DocumentHandler getDocumentHandler() ;
	public void setDocumentHandler(DocumentHandler handler) ;
}
