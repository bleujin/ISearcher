package net.ion.isearcher.indexer.channel;

import java.io.File;

import junit.framework.TestCase;
import net.ion.isearcher.indexer.channel.persistor.StackFile;
import net.ion.isearcher.indexer.channel.persistor.VariableStringPersistor;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

public class TestStackFile extends TestCase{

	
	private String fileName = "c:/temp/abc.ser" ;
	private int maxLength = 5;
	private VariableStringPersistor persistor = new VariableStringPersistor() ;
	public void testPush() throws Exception {
		new File(fileName).delete() ;
		
		StackFile<String> stack = new StackFile<String>(fileName, persistor) ;

		for (int i = 0; i < maxLength; i++) {
			String str = RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(20)) ;
			stack.push(str) ;
		}

		for (int i = 0; i < maxLength; i++) {
			String str = stack.pop() ;
		}
		
		stack.close() ;
	}
	
}
