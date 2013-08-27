package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.common.AbDocument.Action;
import net.ion.nsearcher.index.IndexSession;



public interface IWritePolicy {
	public void begin(IndexSession session) throws IOException ;
	public Action apply(IndexSession session, WriteDocument doc) throws IOException ;
	public void end(IndexSession session) ;
}
