package net.ion.nsearcher.index.fileindex;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import net.ion.framework.util.FileUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.WithinThreadExecutor;
import net.ion.nsearcher.config.Central;

import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class FileIndexBuilder {

	private Central central;
	private File baseDir = new File("./");
	private boolean recursive = false;
	private ExecutorService executors = new WithinThreadExecutor();

	private String[] extNames = new String[] { "docx", "pptx", "rtf", "txt", "xls", "pdf", "hwp" };
	private List<IOFileFilter> filters = ListUtil.newList();

	public FileIndexBuilder(Central central) {
		this.central = central;
		filters.add(FileFileFilter.FILE) ;
	}

	public static FileIndexBuilder create(Central central) {
		return new FileIndexBuilder(central);
	}

	public String[] extNames() {
		return extNames;
	}

	public FileIndexBuilder extNames(String[] extNames) {
		this.extNames = extNames;
		return this;
	}
	
	public FileIndexBuilder ageOverFilter(Date cutoffDate, boolean acceptOlder){
		filters.add(FileFilterUtils.ageFileFilter(cutoffDate, acceptOlder)) ;
		return this ;
	}

	public FileIndexBuilder ageOverFilter(long cutoffDate, boolean acceptOlder){
		filters.add(FileFilterUtils.ageFileFilter(cutoffDate, acceptOlder)) ;
		return this ;
	}
	
	public FileIndexBuilder sizeFilter(long threshold, boolean acceptLarger){
		filters.add(FileFilterUtils.sizeFileFilter(threshold, acceptLarger)) ;
		return this ;
	}

	public FileIndexBuilder prefixFilter(String prefix){
		filters.add(FileFilterUtils.prefixFileFilter(prefix)) ;
		return this ;
	}

	public FileIndexBuilder filters(IOFileFilter... append){
		filters.addAll(Arrays.asList(append)) ;
		return this ;
	}


	public File baseDir() {
		return baseDir;
	}

	public FileIndexBuilder baseDir(File baseDir) {
		if (baseDir.exists() && baseDir.isDirectory() && baseDir.canRead()) {
			this.baseDir = baseDir;
			return this;
		} else {
			throw new IllegalArgumentException(baseDir + " not exist or not directory");
		}

	}

	public boolean recursive() {
		return recursive;
	}

	public FileIndexBuilder recursive(boolean recursive) {
		this.recursive = recursive;
		return this;
	}

	public FileIndexer build() {
		return new FileIndexer(this.central, this.executors, targetFiles());
	}

	private List<File> targetFiles() {
		IOFileFilter filter = FileFilterUtils.and(filters.toArray(new IOFileFilter[0]));
		List<File> result = FileFilterUtils.filterList(filter, FileUtil.listFiles(baseDir, this.extNames(), this.recursive));
		return result;

	}

	public FileIndexBuilder executors(ExecutorService executors) {
		this.executors = executors;
		return this;
	}

}
