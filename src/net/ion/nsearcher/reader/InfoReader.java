package net.ion.nsearcher.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.search.SingleSearcher;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.ReaderUtil;

public class InfoReader {

	public interface InfoHandler<T> {
		public T view(IndexReader dreader) throws IOException ;
	}
	
	private SingleSearcher searcher ;
	private InfoReader(SingleSearcher searcher)  {
		this.searcher = searcher;
	}

	public final static InfoReader create(SingleSearcher searcher)  {
		return new InfoReader(searcher);
	}

	
//	public Collection<IndexCommit> listCommits() throws IOException{
//		return (Collection<IndexCommit>)getIndexReader().listCommits(searcher.dir()) ;
//	}
	
	public <T> T info(InfoHandler<T> ihandler) throws IOException{
		return ihandler.view(searcher.indexReader()) ;
	}
	
	public int maxDoc() throws IOException{
		return getIndexReader().maxDoc() ;
	}
	
	public int numDoc() throws IOException{
		return getIndexReader().numDocs() ;
	}
	
	public IndexReader getIndexReader() throws IOException{
		return searcher.indexReader() ;
	}
	
	public List<String> getFieldNames() throws IOException{
		List<String> result = new ArrayList<String>() ;
		
		for (FieldInfo finfo : ReaderUtil.getMergedFieldInfos(getIndexReader())) {
			String fieldName = finfo.name ;
			if (ArrayUtils.contains(IKeywordField.KEYWORD_FIELD, fieldName) || fieldName.endsWith(MyField.SORT_POSTFIX) ) {
				continue ;
			}
			result.add(fieldName) ;
		}
		
		return result ;
	}
	
}
