package net.ion.nsearcher.index.fileindex;

import net.ion.framework.vfs.VFile;

public interface FailIndexVFileHandler <T> {
	public T onFail(VFile file, Exception ex)  ;

}
