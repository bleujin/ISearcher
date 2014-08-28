package net.ion.nsearcher.config;

import java.io.IOException;
import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.common.MyField.MyFieldType;

import org.apache.lucene.index.CorruptIndexException;

public class IndexSchemaBuilder {
	
	private CentralConfig centralConfig;
	private Map<String, MyFieldType> types = MapUtil.newMap() ;
	
	public IndexSchemaBuilder(CentralConfig centralConfig) {
		this.centralConfig = centralConfig ;
	}
	
	public CentralConfig parent(){
		return centralConfig ;
	}
	
	public Central build() throws CorruptIndexException, IOException{
		return centralConfig.build() ;
	}

	public IndexSchemaBuilder add(String fname, MyFieldType ftype) {
		types.put(fname, ftype) ;
		return this ;
	}

	public IndexSchemaBuilder keyword(String... fnames){
		for (String fname : fnames) {
			add(fname, MyFieldType.Keyword) ;
		}
		return this ;
	}
	
	public IndexSchemaBuilder number(String... fnames){
		for (String fname : fnames) {
			add(fname, MyFieldType.Number) ;
		}
		return this ;
	}
	
	public IndexSchemaBuilder date(String... fnames){
		for (String fname : fnames) {
			add(fname, MyFieldType.Date) ;
		}
		return this ;
	}
	
	public IndexSchemaBuilder unknown(String... fnames){
		for (String fname : fnames) {
			add(fname, MyFieldType.Unknown) ;
		}
		return this ;
	}
	
	public IndexSchemaBuilder text(String... fnames){
		for (String fname : fnames) {
			add(fname, MyFieldType.Text) ;
		}
		return this ;
	}
	
	
}
