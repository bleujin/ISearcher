package net.ion.isearcher.indexer.collect;

import java.util.Date;

import net.ion.isearcher.indexer.handler.DocumentHandler;

import org.apache.http.impl.cookie.DateUtils;


public interface ICollector  {
	
	final static String DEFAULT_NAME  = DateUtils.formatDate(new Date(), "yyyyMMdd") + "/DEFAULT";

	public void collect() ;
	public void shutdown(String cause) ;
	public String getCollectName() ;
	public DocumentHandler getDocumentHandler() ;
	public void setDocumentHandler(DocumentHandler handler) ;
}
