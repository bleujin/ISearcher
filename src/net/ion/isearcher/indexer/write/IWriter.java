package net.ion.isearcher.indexer.write;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.ion.isearcher.common.IKeywordField;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.impl.HashBean;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;


public interface IWriter extends IKeywordField{
	IWriter EMPTY_WRITER = new IWriter(){
		public void begin(String owner) {
		}

		public void end() {
		}

		public Action deleteAll() throws IOException {
			return Action.Unknown ;
		}

		public Action deleteDocument(MyDocument doc) throws IOException {
			return Action.Unknown ;
		}

		public Action insertDocument(MyDocument doc) throws IOException {
			return Action.Unknown ;
		}

		public Action appendFrom(Directory srcDir) {
			return Action.Unknown;
		}		

		public Map<String, HashBean> loadHashMap() throws IOException {
			return new HashMap<String, HashBean>();
		}

		public void rollback() throws IOException {
		}

		public Action updateDocument(MyDocument doc) throws IOException {
			return Action.Unknown ;
		}

		public void close() {
		}
		public void commit() {
		}

		public void optimize() {
		}
		
		public boolean isLocked(){
			return false ;
		}

		public Action deleteTerm(Term term) throws IOException {
			return Action.Unknown;
		}
		public Action deleteQuery(Query query) throws IOException{
			return Action.Unknown;
		}

	};

	public Map<String, HashBean> loadHashMap() throws IOException ;
	public Action deleteDocument(MyDocument doc) throws IOException ;
	public Action insertDocument(MyDocument doc) throws IOException ;
	public Action updateDocument(MyDocument doc) throws IOException ;
	public Action deleteAll() throws IOException ;
	public Action deleteTerm(Term term) throws IOException ;
	public Action deleteQuery(Query query) throws IOException ;
	public Action appendFrom(Directory srcDir) throws IOException;

	public void rollback() throws IOException ;
	
	public void begin(String owner) throws LockObtainFailedException, IOException;
	public void end() throws IOException  ;
	public void close() throws IOException;
	
	// This method has been deprecated, as it is horribly inefficient and very rarely justified.
	// Lucene's multi-segment search performance has improved over time, and the default TieredMergePolicy now targets segments with deletions.
	@Deprecated public void optimize() throws IOException ;
	public void commit() throws IOException ;
	public boolean isLocked() throws IOException ;
}
