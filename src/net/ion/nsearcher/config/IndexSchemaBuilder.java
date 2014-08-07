package net.ion.nsearcher.config;

import java.io.IOException;
import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.common.FieldIndexingStrategy.FieldType;

import org.apache.lucene.index.CorruptIndexException;

public class IndexSchemaBuilder {
	
	private CentralConfig centralConfig;
	private Map<String, FieldType> types = MapUtil.newMap() ;
	
	public IndexSchemaBuilder(CentralConfig centralConfig) {
		this.centralConfig = centralConfig ;
	}
	
	public CentralConfig parent(){
		return centralConfig ;
	}
	
	public Central build() throws CorruptIndexException, IOException{
		return centralConfig.build() ;
	}

	public IndexSchemaBuilder add(String fname, FieldType ftype) {
		types.put(fname, ftype) ;
		return this ;
	}

	public IndexSchemaBuilder keyword(String... fnames){
		for (String fname : fnames) {
			add(fname, FieldType.Keyword) ;
		}
		return this ;
	}
	
	public IndexSchemaBuilder number(String... fnames){
		for (String fname : fnames) {
			add(fname, FieldType.Number) ;
		}
		return this ;
	}
	
	public IndexSchemaBuilder date(String... fnames){
		for (String fname : fnames) {
			add(fname, FieldType.Date) ;
		}
		return this ;
	}
	
	public IndexSchemaBuilder manual(String... fnames){
		for (String fname : fnames) {
			add(fname, FieldType.Manual) ;
		}
		return this ;
	}
	
	public IndexSchemaBuilder text(String... fnames){
		for (String fname : fnames) {
			add(fname, FieldType.Text) ;
		}
		return this ;
	}
	
	
}
