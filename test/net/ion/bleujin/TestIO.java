package net.ion.bleujin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Optional;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import junit.framework.TestCase;

public class TestIO extends TestCase {

	public void testWriteRead() throws Exception {
		File file = new File("./resource/temp/hello.txt");
		
		final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
		IOUtil.write("Hello", writer) ;
		writer.close();

		String readed = IOUtil.toString(new FileInputStream(file));
		Debug.line(readed) ;
	}
	
	public void testIOManager() throws Throwable {
		IOManager im = new IOManager();
		
		Integer result = im.syncWriteRequest("./resou8rce/temp/hello.txt", new WriteJob<Integer>(){
			public Integer handle(OutputStream output) throws Exception {
				final OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
				writer.write("Hello") ;
				return 5 ;
			}}
		);
	}
	
	
}




class B {
	private A a = new A();
	public void callA() throws Exception {
		String rtn = a.a() ;
		for(StackTraceElement ele : Thread.currentThread().getStackTrace()) {
			Debug.line(ele) ;
		}
	}
}

class A {
	public String a(){
		return "a" ;
	}
	
}