package net.ion.nsearcher.index.fileindex;

import java.io.File;
import java.io.IOException;

import net.ion.nsearcher.index.IndexSession;

public interface FileIndexHandler<T> {

	public T onSuccess(IndexSession isession, FileEntry fentry) throws IOException ;
	
	public T onFail(IndexSession isession, File file, Exception ex) ;
}
