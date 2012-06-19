package net.ion.isearcher.indexer.policy;

import net.ion.isearcher.indexer.write.IWriter;

public abstract class AbstractWritePolicy implements IWritePolicy{
	public void begin(IWriter writer) {}
	public void end(IWriter writer) {}

}
