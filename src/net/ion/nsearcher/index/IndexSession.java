package net.ion.nsearcher.index;

import java.io.IOException;
import java.util.List;

import javax.swing.plaf.ListUI;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.common.AbDocument.Action;
import net.ion.nsearcher.common.FieldIndexingStrategy;
import net.ion.nsearcher.common.IndexFieldType;
import net.ion.nsearcher.common.ReadDocument;
import net.ion.nsearcher.common.SearchConstant;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.search.SingleSearcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

public class IndexSession {

	private final SingleSearcher searcher;
	private IndexWriter writer;
	private IndexWriterConfig wconfig;
	private String owner;
	private FieldIndexingStrategy fieldIndexingStrategy;
	private boolean ignoreBody;

	public final static String VERSION = "version" ;
	public final static String LASTMODIFIED = "lastmodified" ;
	private final IndexFieldType indexFieldType ;
	private Indexer indexer;

	IndexSession(SingleSearcher searcher, Analyzer analyzer, IndexWriter iwriter, Indexer indexer) {
		this.searcher = searcher;
		this.wconfig = searcher.central().indexConfig().newIndexWriterConfig(analyzer);
		this.fieldIndexingStrategy = searcher.central().indexConfig().getFieldIndexingStrategy();
		this.writer = iwriter ;
		this.indexFieldType = searcher.central().indexConfig().indexFieldType() ;
		this.indexer = indexer ;
	}

	static IndexSession create(SingleSearcher searcher, Analyzer analyzer, IndexWriter iwriter, Indexer indexer) {
		return new IndexSession(searcher, analyzer, iwriter, indexer);
	}

	public void begin(String owner) throws IOException {
		this.owner = owner;
		
	}
	

	// finally 
	public IndexSession end() {
		release();

		return this;
	}

	private void release() {
		 
	}
	
	
	Document findById(String id) throws IOException{
		return searcher.findById(id) ;
	}
	
	public WriteDocument newDocument(String docId) {
		return new WriteDocument(this, docId) ;
	}
	
	public WriteDocument newDocument(){
		return new WriteDocument(this) ;
	}
	
	public WriteDocument loadDocument(String docId, boolean replaceValue, String... numfieldnames) throws IOException, ParseException {
		ReadDocument rdoc = searcher.central().newSearcher().createRequestByKey(docId).findOne();
		Document findDoc = (rdoc == null) ? new Document() : rdoc.toLuceneDoc() ;
		WriteDocument result = new WriteDocument(this, docId, findDoc, replaceValue);
		
		
		List<String> numFieldName = ListUtil.newList(); // find numeric field
		for(IndexableField field : findDoc.getFields()){
			IndexableFieldType type = field.fieldType() ;
			if ( (!type.indexed()) && field.numericValue() != null){
				numFieldName.add(field.name()) ;
			}
		}

		for (String nfield : numFieldName) {
			IndexableField field = findDoc.getField(nfield);
			if (field == null)
				continue;
			result.number(nfield, field.numericValue().longValue());
		}

		
//		for (String nfield : numfieldnames) {
//			IndexableField field = findDoc.getField(nfield) ;
//			if (field == null) continue ;
//			result.number(nfield, field.numericValue().longValue()) ;
//		}
		return result;
	}
		
	
	public WriteDocument loadDocument(String docId) throws IOException, ParseException {
		return loadDocument(docId, false) ;
	}

	public WriteDocument loadDocument(String docId, boolean replaceValue, FieldLoadable floadable) throws IOException, ParseException {
		ReadDocument rdoc = searcher.central().newSearcher().createRequestByKey(docId).findOne();
		Document findDoc = (rdoc == null) ? new Document() : rdoc.toLuceneDoc() ;
		WriteDocument result = new WriteDocument(this, docId, findDoc, replaceValue);
		
		return floadable.handle(result, findDoc);
	}
	


	
	public FieldIndexingStrategy fieldIndexingStrategy() {
		return fieldIndexingStrategy;
	}
	
	
	
	
	public IndexSession fieldIndexingStrategy(FieldIndexingStrategy fieldIndexingStrategy) {
		this.fieldIndexingStrategy = fieldIndexingStrategy ;
		return this ;
	}
	
	public IndexSession setIgnoreBody(boolean ignoreBody){
		this.ignoreBody = ignoreBody ;
		return this ;
	}
	
	public boolean handleBody(){
		return ! this.ignoreBody ;
	}
	

	public IndexReader reader() throws IOException {
		return searcher.indexReader();
	}

	public Action insertDocument(WriteDocument doc) throws IOException {
		writer.addDocument(doc.toLuceneDoc());
		return Action.Insert;
	}

	public Action updateDocument(WriteDocument doc) throws IOException {
		final Document idoc = doc.toLuceneDoc();
		
		if (doc.isNewDoc()) writer.addDocument(idoc);
		else writer.updateDocument(new Term(SearchConstant.DocKey, doc.idValue()), idoc);
		
		return Action.Update;
	}

//	public Action copy(Directory src) throws IOException {
//		for (String fileName : src.listAll()) {
//			src.copy(searcher.central().dir(), fileName, fileName, IOContext.DEFAULT);
//		}
//
//		return Action.Update;
//	}
	

	// public IndexSession commit() throws IOException{
	// commit() ;
	//		
	// return this ;
	// }


	public void commit() throws CorruptIndexException, IOException {
		if (alreadyCancelled)
			return;
		if (writer != null) {
//			writer.prepareCommit(); 
			writer.forceMerge(10000, true);
			writer.prepareCommit();
			
			final String lastmodified = String.valueOf(System.currentTimeMillis());
			writer.setCommitData(MapUtil.<String>chainKeyMap().put(VERSION, SearchConstant.LuceneVersion.toString()).put(LASTMODIFIED, lastmodified).toMap()) ;
			writer.commit();
		}
	}

	private boolean alreadyCancelled = false;

	public void cancel() throws IOException {
		this.alreadyCancelled = true;
		writer.rollback();
		indexer.closedWriter(); 
	}

	public IndexSession rollback() {
		if (alreadyCancelled)
			return this;
		this.alreadyCancelled = true;
		if (writer != null) {
			try {
				writer.rollback();
				indexer.closedWriter(); 
			} catch (IOException ignore) {
				ignore.printStackTrace();
			}
		}
		return this;
	}

	public Action deleteDocument(WriteDocument doc) throws IOException {
		writer.deleteDocuments(new Term(SearchConstant.DocKey, doc.idValue()));
		return Action.Delete;
	}

	public Action deleteById(String idValue) throws IOException {
		writer.deleteDocuments(new Term(SearchConstant.DocKey, idValue));
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


	// public Map loadHashMap() {
	// Map<String, HashBean> map = new HashMap<String, HashBean>();
	//
	// IndexReader reader = central.getIndexReader() ;
	//
	// for (int i = 0, last = reader.maxDoc(); i < last; i++) {
	// if (reader.isDeleted(i))
	// continue;
	// Document doc = reader.document(i);
	// HashBean bean = new HashBean(getIdValue(doc), getBodyValue(doc));
	// map.put(getIdValue(doc), bean);
	// }
	//
	// return Collections.unmodifiableMap(map);
	// }

	public String getIdValue(Document doc) {
		return doc.get(SearchConstant.DocKey);
	}

	public String getBodyValue(Document doc) {
		return doc.get(SearchConstant.BodyHash);
	}

	public IndexSession indexWriterConfig(IndexWriterConfig wconfig){
		this.wconfig = wconfig ;
		return this ;
	}
	
	public IndexWriterConfig indexWriterConfig() {
		return wconfig;
	}

	public void appendFrom(Directory... dirs) throws CorruptIndexException, IOException {
		writer.addIndexes(dirs);
	}

	public IndexSession continueUnit() throws IOException {
		commit();
		// begin(this.owner) ;
		return this;
	}

	public IndexFieldType indexFieldType() {
		return this.indexFieldType ;
	}


}
