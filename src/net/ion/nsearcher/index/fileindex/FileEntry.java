package net.ion.nsearcher.index.fileindex;

import java.io.File;
import java.util.Map;

public class FileEntry {

	private File file;
	private StringBuffer content;
	private Map<String, String> meta;

	private FileEntry(File file, StringBuffer content, Map<String, String> meta) {
		this.file = file ;
		this.content = content ;
		this.meta = meta ;
	}


	public static final FileEntry create(File file, StringBuffer content, Map<String, String> meta){
		return new FileEntry(file, content, meta) ;
	}
	

	public File file(){
		return file ;
	}
	
	public StringBuffer contentBuffer(){
		return content ;
	}
	
	public Map<String, String> meta(){
		return meta ;
	}
}
