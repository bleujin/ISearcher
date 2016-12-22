package net.ion.nsearcher.index.fileindex;

import java.io.File;

import net.ion.framework.util.Debug;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.LocalSubDirFileProvider;
import net.ion.framework.vfs.VFS;
import net.ion.framework.vfs.VFile;
import junit.framework.TestCase;

public class TestFileEntryFactory extends TestCase {
	
	public void testReadHWP() throws Exception {
		
		FileSystemEntry vfs = VFS.createEmpty() ;
		vfs.addProvider("local", new LocalSubDirFileProvider());
		
		VFile vfile = vfs.resolveFile("local:/resource/0001409152.hwp") ;
		Debug.line(vfile.exists());
		
		
		VFileEntry entry = FileEntryFactory.create().makeEntry(vfile, new FailIndexVFileHandler<VFileEntry>(){
			@Override
			public VFileEntry onFail(VFile file, Exception ex) {
				// TODO Auto-generated method stub
				return null;
			}
			
		}) ;
		
		Debug.line(entry.contentBuffer(), entry.meta());
	}

}
