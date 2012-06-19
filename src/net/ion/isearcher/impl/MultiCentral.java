package net.ion.isearcher.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.indexer.write.Mutex;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;
import net.ion.isearcher.util.CloseUtils;
import net.ion.isearcher.util.LRUMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexFileNameFilter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class MultiCentral extends Central {

	private Directory src;
	private Directory dest;

	private IndexReader ireader;
	private IndexWriter iwriter;
	private IndexSearcher isearcher;

	private Map<Filter, Filter> filters = new LRUMap(128);

	MultiCentral(Directory buffer, Directory store) throws IOException {
		this.src = buffer;
		this.dest = store;

		for (Directory dir : new Directory[] { buffer, store }) {
			if (IndexReader.indexExists(dir))
				continue;
			IndexWriterConfig wc = new IndexWriterConfig(Version.LUCENE_36, new StandardAnalyzer(Version.LUCENE_36));
			wc.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(dir, wc);
			writer.commit();
			writer.close();
		}

		IndexReader[] subReaders = new IndexReader[] { IndexReader.open(buffer), IndexReader.open(store) };
		this.ireader = new MultiReader(subReaders);
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
				// new IndexSearcher(IndexReader.open(dir)) ;
				this.isearcher = new IndexSearcher(getIndexReader());
			} else if (getMutex().isUpdated()) {

				if (this.isearcher != null) {

					IndexSearcher oldSearcher = this.isearcher;
					getScheduler().schedule(new SearcherCloser(oldSearcher), 10, TimeUnit.SECONDS);
				}
				this.isearcher = new IndexSearcher(getIndexReader());
				this.filters.clear();
				getMutex().reflectUpdate();
			}
		}

		return isearcher;
	}

	public synchronized void copyFrom(Analyzer analyzer, Directory... srcDirs) throws IOException {
		getMutex().tryLock(this);
		try {
			if (iwriter == null) {
				createNewWriter(analyzer, newIndexer(analyzer));
			}
			iwriter.addIndexes(srcDirs);
			iwriter.commit();
		} finally {
			getMutex().unLock(this, true);
		}
	}

	protected MultiReader getIndexReader() throws IOException {
		IndexReader[] subReaders = new IndexReader[] { IndexReader.open(src), IndexReader.open(dest) };
		return new MultiReader(subReaders);
	}

	private long getUsableBufferMemory() {
		Runtime runtime = Runtime.getRuntime();
		long maxMemoryMega = runtime.maxMemory() / 1000 / 1000;
		long bufferSizeMemroy = maxMemoryMega / 5;
		return bufferSizeMemroy;
	}

	public void destroySelf() {
		synchronized (STORE) {
			String lockID = getDir().getLockID();
			CloseUtils.silentClose(iwriter);
			CloseUtils.silentClose(isearcher);
			CloseUtils.silentClose(ireader);
			CloseUtils.silentClose(getDir());
			this.iwriter = null;
			this.isearcher = null;
			this.ireader = null;
			STORE.remove(lockID);
			// if (IndexWriter.isLocked(dir)) IndexWriter.unlock(dir) ; // on auto reload ..
		}
	}

	public Directory getDir() {
		return this.dest;
	}

	protected void createNewWriter(Analyzer analyzer, IWriter owner) throws IOException {

		CloseUtils.silentClose(this.iwriter);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, new LimitTokenCountAnalyzer(analyzer, 8192 * 4));
		IndexWriter indexWriter = new IndexWriter(src, config);

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

	public void forceCopy() throws IOException {
		IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
		for (String file : src.listAll()) {
			if (filter.accept(null, file)) {
				src.copy(dest, file, file);
			}
		}
	}

	public ISearcher destSearcher() throws IOException {
		return Central.createOrGet(dest).newSearcher() ;
	}

}
