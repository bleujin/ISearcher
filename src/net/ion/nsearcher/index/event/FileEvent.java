package net.ion.nsearcher.index.event;

import java.io.File;
import java.io.IOException;

import net.ion.nsearcher.common.HashFunction;
import net.ion.nsearcher.index.collect.ICollector;


public class FileEvent extends CollectorEvent {

	private static final long serialVersionUID = -1946667700198794558L;
	private ICollector collector ;
	private File file ;
	public FileEvent(ICollector collector, File file) {
		this.collector = collector ;
		this.file = file ;
	}

	
	public File getFile(){
		return file ;
	}
	
	public long getEventId() throws IOException{
		return HashFunction.hashGeneral(file.getCanonicalPath() + collector.getCollectName());
	}
	
	public long getEventBody() throws IOException{
		StringBuilder valueField = new StringBuilder() ;
		valueField.append(getEventId() + DIV + file.lastModified()) ;
		
		return HashFunction.hashGeneral(valueField.toString()) ;
	}	

	public String toString(){
		return getCollectorName() + " " + file.getAbsolutePath() + "[" + getClass().getName() + "]" ;
	}


	public ICollector getCollector() {
		return collector ;
	}
	
	public String getCollectorName(){
		return collector.getCollectName() ;
	}
	
}
