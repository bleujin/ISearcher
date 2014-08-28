package net.ion.nsearcher.config;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.WithinThreadExecutor;
import net.ion.nsearcher.common.SearchConstant;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.debug.standard.DCJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;

public class SearchConfigBuilder {

	private CentralConfig centralConfig ;
	private Analyzer queryAnalyzer ;
	private String dftSearchFieldName = SearchConstant.ISALL_FIELD ;
	private ExecutorService es = new WithinThreadExecutor() ;
	
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
		return ObjectUtil.coalesce(queryAnalyzer, new StandardAnalyzer(centralConfig.version())) ;
	}
	
	
	public CentralConfig parent(){
		return centralConfig ;
	}
	
	public SearchConfigBuilder executorService(ExecutorService es){
		this.es = es ;
		return this ;
	}
	
	
	public Central build() throws CorruptIndexException, IOException{
		return centralConfig.build() ;
	}


	SearchConfig buildSelf(CentralConfig parent) {
		return new SearchConfig(this.es, parent.version(), queryAnalyzer(), defaultSearchFieldName());
	}

}
