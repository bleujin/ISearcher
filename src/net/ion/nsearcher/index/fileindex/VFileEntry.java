package net.ion.nsearcher.index.fileindex;

import java.util.Map;

import net.ion.framework.vfs.VFile;

public class VFileEntry {

	private VFile file;
	private StringBuffer content;
	private Map<String, String> meta;

	private VFileEntry(VFile file, StringBuffer content, Map<String, String> meta) {
		this.file = file ;
		this.content = content ;
		this.meta = meta ;
	}


	public static final VFileEntry create(VFile file, StringBuffer content, Map<String, String> meta){
		return new VFileEntry(file, content, meta) ;
	}
	

	public VFile file(){
		return file ;
	}
	
	public StringBuffer contentBuffer(){
		return content ;
	}
	
	public Map<String, String> meta(){
		return meta ;
	}
}

