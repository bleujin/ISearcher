package net.ion.isearcher.indexer.handler;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.events.CollectorEvent;

public interface DocumentHandler {
	
	public final static float HEAD_BOOST = 2f;
	public static final String TIMESTAMP = "timestamp";
	

	MyDocument[] makeDocument(CollectorEvent event) throws IOException;

}
