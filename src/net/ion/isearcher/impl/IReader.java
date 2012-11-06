package net.ion.isearcher.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ion.isearcher.common.IKeywordField;
import net.ion.isearcher.common.MyField;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.ReaderUtil;

public class IReader {

	private IndexReader indexReader ;
	IReader(IndexReader reader) throws CorruptIndexException, IOException {
		this.indexReader = reader;
	}
	
	public Collection<IndexCommit> listCommits() throws IOException{
		return (Collection<IndexCommit>)getIndexReader().listCommits(getIndexReader().directory()) ;
	}
	
	public int maxDoc() throws IOException{
		return getIndexReader().maxDoc() ;
	}
	
	public int numDoc() throws IOException{
		return getIndexReader().numDocs() ;
	}
	
	public IndexReader getIndexReader() throws IOException{
		return indexReader ;
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
