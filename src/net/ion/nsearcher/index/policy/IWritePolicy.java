package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.MyDocument.Action;
import net.ion.nsearcher.index.IndexSession;



public interface IWritePolicy {
	public void begin(IndexSession session) throws IOException ;
	public Action apply(IndexSession session, MyDocument doc) throws IOException ;
	public void end(IndexSession session) ;
}
