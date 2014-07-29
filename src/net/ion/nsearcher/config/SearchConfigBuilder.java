package net.ion.nsearcher.config;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.nsearcher.common.SearchConstant;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.debug.standard.DCJKAnalyzer;
import org.apache.lucene.index.CorruptIndexException;

public class SearchConfigBuilder {

	private CentralConfig centralConfig ;
	private Analyzer queryAnalyzer ;
	private String dftSearchFieldName = SearchConstant.ISALL_FIELD ;
	private ExecutorService es = new WithInExecutorService() ;
	
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
		return ObjectUtil.coalesce(queryAnalyzer, new CJKAnalyzer(centralConfig.version())) ;
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

class WithInExecutorService extends AbstractExecutorService {
	private volatile boolean shutDown;

	public WithInExecutorService() {
		shutDown = false;
	}

	public void execute(Runnable command) {
		command.run();
	}

	public void shutdown() {
		shutDown = true;
	}

	public List shutdownNow() {
		shutDown = true;
		return ListUtil.EMPTY ;
	}

	public boolean isShutdown() {
		return shutDown;
	}

	public boolean isTerminated() {
		return shutDown;
	}

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return shutDown;
	}

}