package net.ion.nsearcher.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.common.MyField;
import net.ion.nsearcher.search.SingleSearcher;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.store.Directory;

public class InfoReader {

	public interface InfoHandler<T> {
		public T view(IndexReader ireader, DirectoryReader dreader) throws IOException ;
	}
	
	private SingleSearcher searcher ;
	private DirectoryReader dreader;
	private InfoReader(SingleSearcher searcher, DirectoryReader dreader)  {
		this.searcher = searcher;
		this.dreader = dreader ;
	}

	public final static InfoReader create(SingleSearcher searcher, DirectoryReader direader)  {
		return new InfoReader(searcher, direader);
	}

	
//	public Collection<IndexCommit> listCommits() throws IOException{
//		return (Collection<IndexCommit>)getIndexReader().listCommits(searcher.dir()) ;
//	}
	
	public <T> T info(InfoHandler<T> ihandler) throws IOException{
		return ihandler.view(searcher.indexReader(), dreader) ;
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

	public DirectoryReader getDirectoryReader() throws IOException{
		return dreader ;
	}

	
	public List<String> getFieldNames() throws IOException{
		List<String> result = new ArrayList<String>() ;

		
		
		for (FieldInfo finfo : MultiFields.getMergedFieldInfos(getIndexReader())) {
			String fieldName = finfo.name ;
			if (ArrayUtils.contains(IKeywordField.KEYWORD_FIELD, fieldName) || fieldName.endsWith(MyField.SORT_POSTFIX) ) {
				continue ;
			}
			result.add(fieldName) ;
		}
		
		return result ;
	}

	public Map<String, String> commitUserData() throws IOException {
		List<IndexCommit> list = DirectoryReader.listCommits(dreader.directory());
		if (list.size() <= 0) return MapUtil.EMPTY ;
		return list.get(0).getUserData() ;
	}
	
}
