package net.ion.bleujin.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.RandomUtil;

public class TestCompleteService extends TestCase{

	public void testCreate() throws Exception {
		 ExecutorService exec = Executors.newFixedThreadPool(10);
		 Render r = new Render(exec);
		 r.renderPage();
		 
		Thread.sleep(10000) ;
	}
}

class Render {

	private int count = 0;
	private ExecutorService exec;

	Render(ExecutorService exec) {
		this.exec = exec;
	}

	public void renderPage() {
		CompletionService<String> cs = new ExecutorCompletionService<String>(exec);

		for (count = 0; count < 10; count++) {
			cs.submit(new Callable<String>() {
				private int index = count ;
				public String call() throws Exception {
					Thread.sleep(RandomUtil.nextInt(2000));
					return String.valueOf(index);
				}

			});
		}

		try {
			for (int i = 0; i < 20; i++) {

				Future<String> f = cs.take();
				String tempString = f.get(2, TimeUnit.SECONDS);
				Debug.line("Image Render" + tempString) ;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

	}

}
