package net.ion.nsearcher.config;

import java.io.IOException;

import net.ion.framework.util.ObjectUtil;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.SearchConstant;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.debug.standard.DCJKAnalyzer;
import org.apache.lucene.analysis.debug.standard.DStandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.index.IndexWriter.IndexReaderWarmer;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.Version;

public class IndexConfigBuilder {

	private CentralConfig centralConfig ;
	private IndexWriterConfig clone = new IndexWriterConfig(SearchConstant.LuceneVersion, new DStandardAnalyzer(SearchConstant.LuceneVersion)) ;
	private Analyzer analyzer ;
	private FieldIndexingStrategy fieldIndexingStrategy = FieldIndexingStrategy.DEFAULT ;
	
	IndexConfigBuilder(CentralConfig centralConfig) {
		this.centralConfig = centralConfig ;
	}
	
	public CentralConfig parent(){
		return centralConfig ;
	}
	
	public IndexConfigBuilder setIndexDeletionPolicy(IndexDeletionPolicy delPolicy){
		clone.setIndexDeletionPolicy(delPolicy) ;
		return this ;
	}
	public IndexConfigBuilder setMaxBufferedDocs(int maxBufferedDocs){
		clone.setMaxBufferedDocs(maxBufferedDocs) ;
		return this ;
	}
	
	
	public IndexConfigBuilder setMaxBufferedDeleteTerms(int maxBufferedDeleteTerms){
		clone.setMaxBufferedDeleteTerms(maxBufferedDeleteTerms) ;
		return this ;
	}

	public IndexConfigBuilder setMaxThreadStates(int maxThreadStates){
		clone.setMaxThreadStates(maxThreadStates) ;
		return this ;
	}


	public IndexConfigBuilder setMergedSegmentWarmer(IndexReaderWarmer mergeSegmentWarmer){
		clone.setMergedSegmentWarmer(mergeSegmentWarmer) ;
		return this ;
	}


	public IndexConfigBuilder setMergePolicy(MergePolicy mergePolicy){
		clone.setMergePolicy(mergePolicy) ;
		return this ;
	}


	public IndexConfigBuilder setMergeScheduler(MergeScheduler mergeScheduler){
		clone.setMergeScheduler(mergeScheduler) ;
		return this ;
	}

	public IndexConfigBuilder setRamBufferSizeMB(double ramBufferSizeMB){
		clone.setRAMBufferSizeMB(ramBufferSizeMB) ;
		return this ;
	}
	
	public IndexConfigBuilder setReaderPooling(boolean readerPooling){
		clone.setReaderPooling(readerPooling) ;
		return this ;
	}
	
	public IndexConfigBuilder setReaderTermsIndexDivisor(int divisor){
		clone.setReaderTermsIndexDivisor(divisor) ;
		return this ;
	}
	
	public IndexConfigBuilder setSimilarity(Similarity similarity){
		clone.setSimilarity(similarity) ;
		return this ;
	}
	
	public IndexConfigBuilder setTermIndexInterval(int interval){
		clone.setTermIndexInterval(interval) ;
		return this ;
	}
	
	public IndexConfigBuilder setWriteLockTimeout(long writeLockTimeout){
		clone.setWriteLockTimeout(writeLockTimeout) ;
		return this ;
	}
	
	
	public IndexConfigBuilder setDefaultWriteLockTimeout(long timeout){
		IndexWriterConfig.setDefaultWriteLockTimeout(timeout) ;
		return this ;
	}
	
	
	IndexConfig buildSelf(CentralConfig config){
		return new IndexConfig(config.version(), indexAnalyzer(config.version()), clone, this.fieldIndexingStrategy) ;
	}

	private Analyzer indexAnalyzer(Version version) {
		return ObjectUtil.coalesce(this.analyzer, new DCJKAnalyzer(version));
	}

	public Central build() throws CorruptIndexException, IOException{
		return centralConfig.build() ;
	}
	
	public IndexConfigBuilder indexAnalyzer(Analyzer anlyzer){
		this.analyzer = anlyzer ;
		return this ;
	}
	
	public IndexConfigBuilder setFieldIndexingStrategy(FieldIndexingStrategy fiStrategy){
		this.fieldIndexingStrategy = fiStrategy ;
		return this ;
	}
	 

}
