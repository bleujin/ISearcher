package net.ion.nsearcher.config;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.SearchConstant;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.index.IndexWriter.IndexReaderWarmer;
import org.apache.lucene.search.Similarity;

public class IndexWriterConfigBuilder {

	private CentralConfig centralConfig ;
	private IndexWriterConfig clone = new IndexWriterConfig(SearchConstant.LuceneVersion, new StandardAnalyzer(SearchConstant.LuceneVersion)) ;
	
	IndexWriterConfigBuilder(CentralConfig centralConfig) {
		this.centralConfig = centralConfig ;
	}
	
	public CentralConfig parent(){
		return centralConfig ;
	}
	
	public IndexWriterConfigBuilder setIndexDeletionPolicy(IndexDeletionPolicy delPolicy){
		clone.setIndexDeletionPolicy(delPolicy) ;
		return this ;
	}
	public IndexWriterConfigBuilder setMaxBufferedDocs(int maxBufferedDocs){
		clone.setMaxBufferedDocs(maxBufferedDocs) ;
		return this ;
	}
	
	
	public IndexWriterConfigBuilder setMaxBufferedDeleteTerms(int maxBufferedDeleteTerms){
		clone.setMaxBufferedDeleteTerms(maxBufferedDeleteTerms) ;
		return this ;
	}

	public IndexWriterConfigBuilder setMaxThreadStates(int maxThreadStates){
		clone.setMaxThreadStates(maxThreadStates) ;
		return this ;
	}


	public IndexWriterConfigBuilder setMergedSegmentWarmer(IndexReaderWarmer mergeSegmentWarmer){
		clone.setMergedSegmentWarmer(mergeSegmentWarmer) ;
		return this ;
	}


	public IndexWriterConfigBuilder setMergePolicy(MergePolicy mergePolicy){
		clone.setMergePolicy(mergePolicy) ;
		return this ;
	}


	public IndexWriterConfigBuilder setMergeScheduler(MergeScheduler mergeScheduler){
		clone.setMergeScheduler(mergeScheduler) ;
		return this ;
	}

	public IndexWriterConfigBuilder setRamBufferSizeMB(double ramBufferSizeMB){
		clone.setRAMBufferSizeMB(ramBufferSizeMB) ;
		return this ;
	}
	
	public IndexWriterConfigBuilder setReaderPooling(boolean readerPooling){
		clone.setReaderPooling(readerPooling) ;
		return this ;
	}
	
	public IndexWriterConfigBuilder setReaderTermsIndexDivisor(int divisor){
		clone.setReaderTermsIndexDivisor(divisor) ;
		return this ;
	}
	
	public IndexWriterConfigBuilder setSimilarity(Similarity similarity){
		clone.setSimilarity(similarity) ;
		return this ;
	}
	
	public IndexWriterConfigBuilder setTermIndexInterval(int interval){
		clone.setTermIndexInterval(interval) ;
		return this ;
	}
	
	public IndexWriterConfigBuilder setWriteLockTimeout(long writeLockTimeout){
		clone.setWriteLockTimeout(writeLockTimeout) ;
		return this ;
	}
	
	
	public IndexWriterConfigBuilder setDefaultWriteLockTimeout(long timeout){
		IndexWriterConfig.setDefaultWriteLockTimeout(timeout) ;
		return this ;
	}
	

	public IndexWriterConfig buildIndexWriter(Analyzer analyzer) {
		final IndexWriterConfig result = new IndexWriterConfig(SearchConstant.LuceneVersion, analyzer);
		result.setIndexDeletionPolicy(clone.getIndexDeletionPolicy()) ;
		result.setMaxBufferedDocs(clone.getMaxBufferedDocs()) ;
		result.setMaxBufferedDeleteTerms(clone.getMaxBufferedDeleteTerms()) ;
		result.setMaxThreadStates(clone.getMaxThreadStates()) ;
		result.setMergedSegmentWarmer(clone.getMergedSegmentWarmer()) ;
//		result.setMergePolicy(clone.getMergePolicy()) ;
		
		result.setMergeScheduler(clone.getMergeScheduler()) ;
		result.setRAMBufferSizeMB(clone.getRAMBufferSizeMB()) ;
		
		result.setReaderPooling(clone.getReaderPooling()) ;
		result.setReaderTermsIndexDivisor(clone.getReaderTermsIndexDivisor()) ;
		
		result.setSimilarity(clone.getSimilarity()) ;
		result.setTermIndexInterval(clone.getTermIndexInterval()) ;
		result.setWriteLockTimeout(clone.getWriteLockTimeout()) ;

		return result;
	}
	
	
	
	 

}
