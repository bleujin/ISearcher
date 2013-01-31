package net.ion.nsearcher.config;

import java.io.Closeable;
import java.io.IOException;

import net.ion.framework.util.IOUtil;
import net.ion.nsearcher.Searcher;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.index.Indexer;
import net.ion.nsearcher.reader.InfoReader;
import net.ion.nsearcher.search.SingleSearcher;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

public class Central implements Closeable{

	private Directory dir ;
	private final SingleSearcher singleSearcher ;
	private final Indexer indexer ;

	private final CentralConfig config ;
	private Central(CentralConfig config, Directory dir) throws CorruptIndexException, IOException {
		this.config = config ;
		this.dir = dir ;
		this.singleSearcher = SingleSearcher.create(config, dir) ;
		this.indexer = Indexer.create(config, dir, singleSearcher) ;
	}

	static Central create(CentralConfig config) throws CorruptIndexException, IOException {
		Directory dir = config.buildDir() ;
		String[] files = dir.listAll();
		if (files == null || files.length == 0) {
			IndexWriterConfig wconfig = new IndexWriterConfig(SearchConstant.LuceneVersion, new StandardAnalyzer(SearchConstant.LuceneVersion));
			IndexWriter iwriter = new IndexWriter(dir, wconfig);
			iwriter.close() ;
		}

		return new Central(config, dir);
	}

	public Searcher newSearcher() throws IOException {
		return new Searcher(singleSearcher); 
	}
	
	public Indexer newIndexer() {
		return indexer  ;
	}

	public void close() throws IOException {
		indexer.close() ;
		singleSearcher.close();
		dir.close() ;
	}

	public void destroySelf() {
		IOUtil.closeQuietly(dir) ;
	}

	public InfoReader newReader() {
		return singleSearcher.reader();
	}



	
	
}
