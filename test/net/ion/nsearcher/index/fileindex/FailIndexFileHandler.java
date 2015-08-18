package net.ion.nsearcher.index.fileindex;

import java.io.File;

public interface FailIndexFileHandler<T> {

	public T onFail(File file, Exception ex)  ;

}
