package net.ion.nsearcher.search;

import java.io.IOException;

import net.ion.framework.db.Page;

import org.apache.lucene.index.DirectoryReader;

public class PageCollector implements DocCollector{

	private Page page ;
	private DocCollector pre;
	private int count = -1 ;
	
	public PageCollector(Page page, DocCollector pre){
		this.page = page ;
		this.pre  = pre  ;
	}
	
	@Override
	public ColResult accept(DirectoryReader dreader, SearchRequest sreq, int docId) throws IOException {
		if (pre.accept(dreader, sreq, docId) == ColResult.ACCEPT){
			count++ ;
			if (count >= page.getStartLoc() && count < page.getEndLoc()){
				return ColResult.ACCEPT ;
			} else if (count == page.getEndLoc()){
				return ColResult.BREAK ;
			}
		}
		return ColResult.REVOKE ;
	}
}
