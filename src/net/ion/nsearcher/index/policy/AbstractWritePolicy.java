package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.index.IndexSession;


public abstract class AbstractWritePolicy implements IWritePolicy{
	public void begin(IndexSession session) throws IOException {}
	public void end(IndexSession session) {}

}
