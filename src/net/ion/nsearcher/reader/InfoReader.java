package net.ion.nsearcher.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

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
//		Lock lock = searcher.central().readLock() ;
		try {
//			lock.lock(); 
			T result = ihandler.view(searcher.indexReader(), searcher.dirReader());
			return result ;
		} finally {
//			lock.unlock(); 
		}
	}
	
	public int maxDoc() throws IOException{
		return getIndexReader().maxDoc() ;
	}
	
	public int numDoc() throws IOException{
		return getIndexReader().numDocs() ;
	}
	
	@Deprecated
	public IndexReader getIndexReader() throws IOException{
		return searcher.indexReader() ;
	}

	@Deprecated
	public DirectoryReader getDirectoryReader() throws IOException{
		return searcher.dirReader() ;
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
		List<IndexCommit> list = DirectoryReader.listCommits(getDirectoryReader().directory());
		if (list.size() <= 0) return MapUtil.EMPTY ;
		return list.get(0).getUserData() ;
	}
	
}
