package net.ion.isearcher.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.ObjectUtil;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.util.CloseUtils;
import net.ion.isearcher.util.LRUMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class SingleCentral extends Central {

	private Directory dir;
	private IndexReader reader;
	private IndexWriter iwriter;
	private IndexSearcher isearcher;

	private Map<Filter, Filter> filters = new LRUMap(128);

	SingleCentral(Directory dir) throws IOException {
		this.dir = dir;

		if (!IndexReader.indexExists(dir)) {
			IndexWriterConfig wc = new IndexWriterConfig(Version.LUCENE_36, new StandardAnalyzer(Version.LUCENE_36));
			wc.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(dir, wc);
			writer.commit();
			writer.close();
		}
		this.reader = IndexReader.open(dir);
	}

	protected synchronized IndexWriter getIndexWriter(IWriter owner, Analyzer analyzer) throws LockObtainFailedException, IOException {

		if (getMutex().isOwner(owner)) {
			if (this.iwriter == null) { // when rollbacked
				createNewWriter(analyzer, owner);
			}
			return iwriter;
		}
		throw new LockObtainFailedException("exception.isearcher.not_owner");
	}

	protected IndexSearcher getIndexSearcher() throws IOException {

		synchronized (this) {

			if (isearcher == null) {
				this.isearcher = new IndexSearcher(getIndexReader());
			} else if (getMutex().isUpdated()) {
				try {
					if (this.isearcher != null) {

						IndexSearcher oldSearcher = this.isearcher;
						getScheduler().schedule(new SearcherCloser(oldSearcher), 10, TimeUnit.SECONDS);
					}
					this.isearcher = new IndexSearcher(getIndexReader());
					this.filters.clear();
				} finally {
					getMutex().reflectUpdate();
				}
			} else if (dir instanceof FSDirectory && IndexReader.openIfChanged(reader) != null ) {
				this.isearcher = new IndexSearcher(getIndexReader());
				this.filters.clear();
			}
		}

		return isearcher;
	}

	protected void createNewWriter(Analyzer analyzer, IWriter owner) throws IOException {

		CloseUtils.silentClose(this.iwriter);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, new LimitTokenCountAnalyzer(analyzer, 8192 * 4));
		config.setMergePolicy(new TieredMergePolicy());
		config.setMergeScheduler(new ConcurrentMergeScheduler());
		
		IndexWriter indexWriter = new IndexWriter(dir, config);

		if (owner instanceof DefaultWriter) {

			config.setRAMBufferSizeMB(getUsableBufferMemory());
			config.setWriteLockTimeout(3000L);

			MergePolicy policy = config.getMergePolicy();
			if (policy instanceof LogMergePolicy) {
				LogMergePolicy lpolicy = (LogMergePolicy) policy;
				lpolicy.setMergeFactor(1000);
				lpolicy.setMaxMergeDocs(100000);
			}
			// Debug.line('=', "CreateNewWriter", "Ram Buffer Size(MB)", config.getRAMBufferSizeMB(), "Write Lock Timeout", config.getWriteLockTimeout());
		}
		this.iwriter = indexWriter;
	}

	private long getUsableBufferMemory() {
		Runtime runtime = Runtime.getRuntime();
		long maxMemoryMega = runtime.maxMemory() / 1000 / 1000;
		long bufferSizeMemroy = maxMemoryMega / 5;
		return bufferSizeMemroy;
	}

	protected IndexReader getIndexReader() throws IOException {
		IndexReader newReader = IndexReader.openIfChanged(reader);
		if (newReader != null)
			filters.clear();

		return ObjectUtil.coalesce(newReader, reader);
	}

	public void destroySelf() {
		synchronized (STORE) {
			String lockID = this.dir.getLockID();
			CloseUtils.silentClose(iwriter);
			CloseUtils.silentClose(isearcher);
			CloseUtils.silentClose(reader);
			CloseUtils.silentClose(dir);
			this.iwriter = null;
			this.isearcher = null;
			this.reader = null;
			this.dir = null;
			STORE.remove(lockID);
			// if (IndexWriter.isLocked(dir)) IndexWriter.unlock(dir) ; // on auto reload ..
		}
	}

	public Directory getDir() {
		return this.dir;
	}

	public Filter getFilter(Filter filter) {
		synchronized (filters) {
			if (filters.containsKey(filter)) {
				return filters.get(filter);
			} else {
				CachingWrapperFilter value = null;
				if (filter instanceof CachingWrapperFilter) {
					value = (CachingWrapperFilter) filter;
				} else {
					value = new CachingWrapperFilter(filter);
				}
				filters.put(filter, value);
				return value;
			}
		}
	}

	public Filter getKeyFilter(Filter find) {
		for (Entry<Filter, Filter> entry : filters.entrySet()) {
			if (find.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		throw new IllegalArgumentException("NOT FOUND : " + find);
	}

	protected boolean existFilter(Filter filter) {
		return filters.containsKey(filter);
	}

	public synchronized void copyFrom(Analyzer analyzer, Directory... srcDirs) throws IOException {
		//getMutex().tryLock(iwriter);
		try {
			if (iwriter == null) {
				createNewWriter(analyzer, newIndexer(analyzer));
			}
			iwriter.addIndexes(srcDirs);
			iwriter.commit();
		} finally {
			//getMutex().unLock(iwriter, true);
		}
		
//		IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
//		for (Directory src : srcDirs) {
//			for (String file : src.listAll()) {
//				if (filter.accept(null, file)) {
//					src.copy(dir, file, file);
//				}
//			}
//		}

	}

}
