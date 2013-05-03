package net.ion.nsearcher.config;

import net.ion.nsearcher.common.FieldIndexingStrategy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.index.IndexWriter.IndexReaderWarmer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.util.Version;

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
	private ReusableAnalyzerBase analyzer ;
	private FieldIndexingStrategy fieldIndexingStrategy;
	
	IndexConfig(Version version, ReusableAnalyzerBase analyzer, IndexWriterConfig clone, FieldIndexingStrategy fiStrategy) {
		this.version = version ;
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
		return analyzer;
	}

}
