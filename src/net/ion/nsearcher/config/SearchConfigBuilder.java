package net.ion.nsearcher.config;

import java.io.IOException;

import net.ion.framework.util.ObjectUtil;
import net.ion.nsearcher.common.SearchConstant;

import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.index.CorruptIndexException;

public class SearchConfigBuilder {

	private CentralConfig centralConfig ;
	private ReusableAnalyzerBase queryAnalyzer ;
	private String dftSearchFieldName = SearchConstant.ISALL_FIELD ;
	
	public SearchConfigBuilder(CentralConfig centralConfig) {
		this.centralConfig = centralConfig ;
	}

	
	public SearchConfigBuilder queryAnalyzer(ReusableAnalyzerBase queryAnalyzer){
		this.queryAnalyzer = queryAnalyzer ;
		return this ;
	}
	
	public SearchConfigBuilder defaultSearchFieldName(String defaultSearchFieldName){
		this.dftSearchFieldName = defaultSearchFieldName ;
		return this ;
	}
	
	public String defaultSearchFieldName(){
		return dftSearchFieldName ;
	}
	
	public ReusableAnalyzerBase queryAnalyzer(){
		return ObjectUtil.coalesce(queryAnalyzer, new CJKAnalyzer(centralConfig.version())) ;
	}
	
	
	public CentralConfig parent(){
		return centralConfig ;
	}
	
	
	public Central build() throws CorruptIndexException, IOException{
		return centralConfig.build() ;
	}


	SearchConfig buildSelf(CentralConfig parent) {
		return new SearchConfig(parent.version(), queryAnalyzer(), defaultSearchFieldName());
	}



	

}
