package net.ion.nsearcher.index;

import java.io.IOException;

import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.common.MyDocument.Action;
import net.ion.nsearcher.search.SingleSearcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

public class IndexSession {

	private final SingleSearcher searcher;
	private IndexWriter writer ;
	private final IndexWriterConfig wconfig ;
	private String owner;
	private FieldIndexingStrategy fieldIndexingStrategy;
	
	IndexSession(SingleSearcher searcher, Analyzer analyzer) {
		this.searcher = searcher ;
		this.wconfig = searcher.central().indexConfig().newIndexWriterConfig(analyzer);
		this.fieldIndexingStrategy = searcher.central().indexConfig().getFieldIndexingStrategy() ;
	}

	static IndexSession create(SingleSearcher searcher, Analyzer analyzer) {
		return new IndexSession(searcher, analyzer);
	}

	public void begin(String owner) throws IOException {
		this.owner = owner ;
		this.writer = new IndexWriter(searcher.central().dir(), wconfig);
	}

	public void release() {
		
	}
	
	public FieldIndexingStrategy fieldIndexingStrategy(){
		return fieldIndexingStrategy ;
	}

	public IndexReader reader() throws IOException{
		return searcher.indexReader() ;
	}
	
	public Action insertDocument(WriteDocument doc) throws IOException {
		writer.addDocument(doc.toLuceneDoc(fieldIndexingStrategy)) ;
		return Action.Insert ;
	}
	
	public Action updateDocument(WriteDocument doc) throws IOException{
		final Document idoc = doc.toLuceneDoc(fieldIndexingStrategy);
		writer.updateDocument(new Term(SearchConstant.ISKey, idoc.get(SearchConstant.ISKey)), idoc) ;
		return Action.Update ;
	}

	public IndexSession end() throws IOException{
		try {
			commit() ;
		} finally {
			writer.close() ;
			release() ;
		}
		return this ;
	}
	
	public void commit() throws CorruptIndexException, IOException{
		if (alreadyCancelled) return ;
		if (writer != null) writer.commit() ;
	}
	

	private boolean alreadyCancelled = false ;
	public void cancel() throws IOException {
		this.alreadyCancelled = true ;
		writer.rollback() ;
	}
	
	public IndexSession rollback() throws IOException {
		if (alreadyCancelled) return this ;
		this.alreadyCancelled = true ;
		if (writer != null) writer.rollback() ;
		return this ;
	}

	public Action deleteDocument(WriteDocument doc) throws IOException {
		writer.deleteDocuments(new Term(SearchConstant.ISKey)) ;
		return Action.Delete;
	}
	
	public Action deleteAll() throws IOException {
		writer.deleteAll();
		return Action.DeleteAll;
	}

	public Action deleteTerm(Term term) throws IOException {
		writer.deleteDocuments(term);
		return Action.DeleteAll;
	}

	public Action deleteQuery(Query query) throws IOException {
		writer.deleteDocuments(query);
		return Action.DeleteAll;
	}

//	public Map loadHashMap() {
//		Map<String, HashBean> map = new HashMap<String, HashBean>();
//
//		IndexReader reader = central.getIndexReader() ;
//
//		for (int i = 0, last = reader.maxDoc(); i < last; i++) {
//			if (reader.isDeleted(i))
//				continue;
//			Document doc = reader.document(i);
//			HashBean bean = new HashBean(getIdValue(doc), getBodyValue(doc));
//			map.put(getIdValue(doc), bean);
//		}
//
//		return Collections.unmodifiableMap(map);
//	}


	public String getIdValue(Document doc) {
		return doc.get(SearchConstant.ISKey);
	}

	public String getBodyValue(Document doc) {
		return doc.get(SearchConstant.ISBody);
	}

	public IndexWriterConfig getIndexWriterConfig() {
		return wconfig ;
	}

	public void appendFrom(Directory... dirs) throws CorruptIndexException, IOException {
		writer.addIndexes(dirs) ;
	}



}
