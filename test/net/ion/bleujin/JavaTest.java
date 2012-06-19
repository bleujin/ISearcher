package net.ion.bleujin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import net.ion.isearcher.crawler.util.HashFunction;

import org.apache.commons.lang.math.NumberUtils;

public class JavaTest extends TestCase{

	public void testLongParse() throws Exception {
		String s = null ;
		try {
			Debug.debug(Long.parseLong(s)) ;
		} catch(NumberFormatException ex){
			ex.printStackTrace() ;
		}

		Debug.debug(Long.parseLong("")) ;

	}

	
	public void testNull() throws Exception {
		
	}

	
	public void testStackTraceSpeed() throws Exception {
		for (int i = 0; i < 1000; i++) {
			Thread.currentThread().getStackTrace()[0].getMethodName() ;
			// Debug.debug(Thread.currentThread().getStackTrace()[0].getMethodName()) ;
		}
	}
	
	public void testHashMismatch() throws Exception {
		
		Map<Long, String> prefix = new HashMap<Long, String>() ;
		int prefixMax = 10000 ;
		for (int i = 0; i < prefixMax; i++) {
			int length = RandomUtil.nextInt(80) + 20 ;
			String value = RandomUtil.nextRandomString(length) ;
			long key = HashFunction.hashGeneral(value) ;
			prefix.put(key, value) ;
		}
		Debug.debug("PREFIX STORE COMPLETE") ;
		
		int diffMax = 100000000 ;
		for (int i = 0; i < diffMax ; i++) {
			int length = RandomUtil.nextInt(80) + 20 ;
			String value = RandomUtil.nextRandomString(length) ;
			long key = HashFunction.hashGeneral(value) ;
			if (prefix.containsKey(key)){
				if (! prefix.get(key).equals(value)){
					Debug.debug(key, value, prefix.get(key)) ;
				}
			}
			if (i % 1000000 == 0) System.out.print(".") ;
		}
	}
	
	public void testNullToInt() throws Exception {
		Object i = null ;
		
		Debug.debug( ((Integer)i).intValue() ) ;
	}
	

	public void testNumber() throws Exception {
		String s = "010" ;
		assertEquals(10L, Long.parseLong(s)) ;
		Debug.debug(NumberUtils.toInt("+10")) ;
	}
	
	
	public void testClass() throws Exception {
		
		assertEquals(true, String.class instanceof Class) ;
		final Object str = new String("abc");
		final Object strClass = String.class ;
		
		assertEquals(false, str instanceof Class) ;
		assertEquals(true, str instanceof String) ;

		assertEquals(true, strClass instanceof Class) ;
		assertEquals(false, strClass instanceof String) ;

		String s = (String) ((Class)strClass).newInstance() ;
		
	}
	
	public void testString() throws Exception {
		Debug.debug(new String("abcd".getBytes("8859_1"), "UTF-8")) ;
	}

	
	public void testEqual() throws Exception {
		String A1 = "abc" ;
		String A2 = "abc" ;
		
		Debug.debug(A1 == A2) ;
		
	}
	
	
	public void testOut() throws Exception {
		out(null) ;
		out(new Object[]{null, null}) ;
	}
	
	private void out(Object... objs){
		System.out.println( Arrays.deepToString(objs)) ;
	}
}
