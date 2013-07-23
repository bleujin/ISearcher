package net.ion.bleujin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Optional;

import net.ion.framework.util.IOUtil;

public class IOManager {

	private ExceptionHandler ehandler = new ExceptionHandler() ;
	private ExecutorService es = Executors.newCachedThreadPool() ;
	
	public IOManager exceptionHandler(ExceptionHandler ehandler){
		this.ehandler = ehandler ;
		return this ;
	}
	
	public <T> Future<T> writeRequest(final String path, final WriteJob<T> job) {
		return es.submit(new Callable<T>(){
			public T call() throws Exception {
				File file = new File(path);
				FileOutputStream output = null ;
				try {
					output = new FileOutputStream(file);
					T result = job.handle(output);
					return result ;
				} catch(Exception ex){
					ehandler.handleException(ex) ;
					throw ex ;
				} finally {
					IOUtil.closeQuietly(output) ;
				}
			}
		}) ;
		
	}

	public <T> T syncWriteRequest(String path, WriteJob<T> job) throws Exception {
		try {
			return writeRequest(path, job).get();
		} catch(ExecutionException ex){
			throw new IOException(ex.getCause()) ;
		}
	}

}
