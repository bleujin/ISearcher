package net.ion.nsearcher.config;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.IndexFieldType;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter.IndexReaderWarmer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.Version;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class IndexConfig {

	private IndexDeletionPolicy indexDeletionPolicy;
	private int maxBufferedDocs;
	private int maxBufferedDeleteTerms;
	private int maxThreadStates;
	private IndexReaderWarmer mergedSegmentWarmer;
	private MergeScheduler mergeScheduler;
	private double ramBufferSizeMB;
	private boolean readerPooling;
	private int readerTermsIndexDivisor;
	private Similarity similarity;
	private int termIndexInterval;
	private long writeLockTimeout;

	private Version version ;
	private Analyzer analyzer ;
	
	private final Map<String, Analyzer> analMap = MapUtil.newMap() ;
	private PerFieldAnalyzerWrapper wrapperAnalyzer ;
	private FieldIndexingStrategy fieldIndexingStrategy;
	private ExecutorService es;
	private IndexFieldType indexFieldType = IndexFieldType.DEFAULT;
	
	IndexConfig(Version version, ExecutorService es, Analyzer analyzer, IndexWriterConfig clone, FieldIndexingStrategy fiStrategy) {
		this.version = version ;
		this.es = ((es == null) ? Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("isearch-indexer-%d").build()) : es) ;
		this.analyzer = analyzer;
		
		this.indexDeletionPolicy = clone.getIndexDeletionPolicy();
		this.maxBufferedDocs = clone.getMaxBufferedDocs();
		this.maxBufferedDeleteTerms = clone.getMaxBufferedDeleteTerms();
		this.maxThreadStates = clone.getMaxThreadStates();
		this.mergedSegmentWarmer = clone.getMergedSegmentWarmer();
		// result.setMergePolicy(clone.getMergePolicy()) ;

		this.mergeScheduler = clone.getMergeScheduler();
		this.ramBufferSizeMB = clone.getRAMBufferSizeMB();

		this.readerPooling = clone.getReaderPooling();
		this.readerTermsIndexDivisor = clone.getReaderTermsIndexDivisor();

		this.similarity = clone.getSimilarity();
		this.termIndexInterval = clone.getTermIndexInterval();
		this.writeLockTimeout = clone.getWriteLockTimeout();
		this.fieldIndexingStrategy = fiStrategy ;
		this.wrapperAnalyzer = new PerFieldAnalyzerWrapper(analyzer) ;
	}
	
	public static IndexConfig create(Version version, ExecutorService es, Analyzer analyzer, IndexWriterConfig clone, FieldIndexingStrategy fiStrategy) {
		return new IndexConfig(version, es, analyzer, clone, fiStrategy);
	}



	public IndexConfig indexAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer ;
		if (this.analMap.size() > 0) this.wrapperAnalyzer = new PerFieldAnalyzerWrapper(this.analyzer, this.analMap) ;
		return this ;
	}

	public IndexDeletionPolicy getIndexDeletionPolicy() {
		return indexDeletionPolicy;
	}

	public int getMaxBufferedDocs() {
		return maxBufferedDocs;
	}

	public int getMaxBufferedDeleteTerms() {
		return maxBufferedDeleteTerms;
	}

	public int getMaxThreadStates() {
		return maxThreadStates;
	}

	public IndexReaderWarmer getMergedSegmentWarmer() {
		return mergedSegmentWarmer;
	}

	public MergeScheduler getMergeScheduler() {
		return mergeScheduler;
	}

	public double getRAMBufferSizeMB() {
		return ramBufferSizeMB;
	}

	public boolean isReaderPooling() {
		return readerPooling;
	}

	public int getReaderTermsIndexDivisor() {
		return readerTermsIndexDivisor;
	}

	public Similarity getSimilarity() {
		return similarity;
	}

	public int getTermIndexInterval() {
		return termIndexInterval;
	}

	public long getWriteLockTimeout() {
		return writeLockTimeout;
	}
	
	public FieldIndexingStrategy getFieldIndexingStrategy(){
		return fieldIndexingStrategy ;
	}

	public IndexConfig fieldIndexingStrategy(FieldIndexingStrategy strategy){
		this.fieldIndexingStrategy = strategy ;
		return this ;
	}
	
	public IndexWriterConfig newIndexWriterConfig(Analyzer analyzer) {
		final IndexWriterConfig result = new IndexWriterConfig(version, analyzer);
		result.setIndexDeletionPolicy(getIndexDeletionPolicy());
		result.setMaxBufferedDocs(getMaxBufferedDocs());
		result.setMaxBufferedDeleteTerms(getMaxBufferedDeleteTerms());
		result.setMaxThreadStates(getMaxThreadStates());
		result.setMergedSegmentWarmer(getMergedSegmentWarmer());
		// result.setMergePolicy(clone.getMergePolicy()) ;

		result.setMergeScheduler(getMergeScheduler());
		result.setRAMBufferSizeMB(getRAMBufferSizeMB());

		result.setReaderPooling(isReaderPooling());
		result.setReaderTermsIndexDivisor(getReaderTermsIndexDivisor());

		result.setSimilarity(getSimilarity());
		result.setTermIndexInterval(getTermIndexInterval());
		result.setWriteLockTimeout(getWriteLockTimeout());

		return result;
	}

	public Analyzer indexAnalyzer() {
		return analMap.size() == 0 ? this.analyzer : this.wrapperAnalyzer ; 
	}
	
	public IndexConfig fieldAnalyzer(String fieldName, Analyzer analyzer) {
		analMap.put(fieldName, analyzer) ;
		this.wrapperAnalyzer = new PerFieldAnalyzerWrapper(this.analyzer, this.analMap) ;
		return this;
	}

	public IndexConfig removeFieldAnalyzer(String fieldName) {
		if (! analMap.containsKey(fieldName)) return this ;
		
		analMap.remove(fieldName) ;
		this.wrapperAnalyzer = new PerFieldAnalyzerWrapper(this.analyzer, this.analMap) ;
		return this;
	}


	public ExecutorService indexExecutor() {
		return es;
	}

	public IndexFieldType indexFieldType() {
		return indexFieldType;
	}
	
	public IndexConfig indexFieldType(IndexFieldType indexFieldType){
		this.indexFieldType = indexFieldType ;
		return this ;
	}

	


}
