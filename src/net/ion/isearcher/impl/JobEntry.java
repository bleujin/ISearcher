package net.ion.isearcher.impl;

import java.io.IOException;

import net.ion.framework.util.ObjectId;
import net.ion.isearcher.indexer.write.IWriter;

import org.apache.lucene.analysis.Analyzer;

public abstract class JobEntry<T> {

	private String jobId ;
	protected JobEntry(){
		this.jobId = new ObjectId().toString() ;
	}
	
	public abstract <T> T handle(IWriter writer) throws IOException ;

	public abstract Analyzer getAnalyzer();
	
	public void onException(Throwable ex) {
		ex.printStackTrace() ;
	}
	
	public String getId(){
		return jobId ;
	}
}
