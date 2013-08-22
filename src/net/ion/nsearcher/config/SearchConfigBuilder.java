package net.ion.nsearcher.config;

import java.io.IOException;

import net.ion.framework.util.ObjectUtil;
import net.ion.nsearcher.common.SearchConstant;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.debug.standard.DCJKAnalyzer;
import org.apache.lucene.index.CorruptIndexException;

public class SearchConfigBuilder {

	private CentralConfig centralConfig ;
	private Analyzer queryAnalyzer ;
	private String dftSearchFieldName = SearchConstant.ISALL_FIELD ;
	
	public SearchConfigBuilder(CentralConfig centralConfig) {
		this.centralConfig = centralConfig ;
	}

	
	public SearchConfigBuilder queryAnalyzer(Analyzer queryAnalyzer){
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
	
	public Analyzer queryAnalyzer(){
		return ObjectUtil.coalesce(queryAnalyzer, new DCJKAnalyzer(centralConfig.version())) ;
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
